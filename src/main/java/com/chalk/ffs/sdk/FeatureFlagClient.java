package com.chalk.ffs.sdk;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagEvaluationResponseDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/** Lightweight Java client with a TTL cache and optional background synchronization. */
public final class FeatureFlagClient implements AutoCloseable {
    private final String baseUrl;
    private final Long environmentId;
    private final String clientKey;
    private final Duration cacheTtl;
    private final Map<String, Object> defaultContext;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final Map<String, CachedValue> cache = new ConcurrentHashMap<>();
    private final Set<String> registeredKeys = ConcurrentHashMap.newKeySet();
    private final ScheduledExecutorService synchronizer = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread thread = new Thread(r, "feature-flag-sync");
        thread.setDaemon(true);
        return thread;
    });
    private ScheduledFuture<?> syncTask;

    public FeatureFlagClient(String baseUrl, Long environmentId, String clientKey) {
        this(baseUrl, environmentId, clientKey, Duration.ofSeconds(30), Map.of());
    }

    public FeatureFlagClient(String baseUrl, Long environmentId, String clientKey,
                             Duration cacheTtl, Map<String, Object> defaultContext) {
        this.baseUrl = trimTrailingSlash(baseUrl);
        this.environmentId = environmentId;
        this.clientKey = clientKey;
        this.cacheTtl = cacheTtl;
        this.defaultContext = defaultContext == null ? Map.of() : Map.copyOf(defaultContext);
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public EvaluationResult evaluate(String key) throws IOException, InterruptedException {
        return evaluate(key, defaultContext);
    }

    public EvaluationResult evaluate(String key, Map<String, Object> context) throws IOException, InterruptedException {
        registeredKeys.add(key);
        String cacheKey = cacheKey(key, context);
        CachedValue cached = cache.get(cacheKey);
        if (cached != null && cached.expiresAt().isAfter(Instant.now())) return cached.result();

        try {
            FeatureFlagEvaluationResponseDTO response = postSingle(key, context == null ? Map.of() : context);
            EvaluationResult result = EvaluationResult.from(response);
            cache.put(cacheKey, new CachedValue(result, Instant.now().plus(cacheTtl)));
            return result;
        } catch (IOException | InterruptedException error) {
            if (cached != null) return cached.result();
            throw error;
        }
    }

    public boolean isEnabled(String key) throws IOException, InterruptedException {
        return evaluate(key).enabled();
    }

    public boolean getBoolean(String key, boolean fallback) throws IOException, InterruptedException {
        Object value = evaluate(key).value();
        if (value == null) return fallback;
        if (value instanceof Boolean booleanValue) return booleanValue;
        return Boolean.parseBoolean(String.valueOf(value));
    }

    public String getString(String key, String fallback) throws IOException, InterruptedException {
        Object value = evaluate(key).value();
        return value == null ? fallback : String.valueOf(value);
    }

    public BigDecimal getNumber(String key, BigDecimal fallback) throws IOException, InterruptedException {
        Object value = evaluate(key).value();
        if (value == null) return fallback;
        try { return new BigDecimal(String.valueOf(value)); }
        catch (NumberFormatException ignored) { return fallback; }
    }

    public Object getJson(String key, Object fallback) throws IOException, InterruptedException {
        Object value = evaluate(key).value();
        return value == null ? fallback : value;
    }

    public synchronized void startAutoSync(Duration interval) {
        if (syncTask != null) syncTask.cancel(false);
        syncTask = synchronizer.scheduleWithFixedDelay(() -> {
            try { synchronize(); }
            catch (Exception ignored) { /* callers continue using the last good cache value */ }
        }, interval.toMillis(), interval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void synchronize() throws IOException, InterruptedException {
        if (registeredKeys.isEmpty()) return;
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("keys", new ArrayList<>(registeredKeys));
        requestBody.put("context", defaultContext);
        HttpResponse<String> response = send("/client/environments/" + environmentId + "/flags/evaluate", requestBody);
        Map<String, FeatureFlagEvaluationResponseDTO> values = objectMapper.readValue(response.body(),
                new TypeReference<Map<String, FeatureFlagEvaluationResponseDTO>>() {});
        Instant expiresAt = Instant.now().plus(cacheTtl);
        for (Map.Entry<String, FeatureFlagEvaluationResponseDTO> entry : values.entrySet()) {
            cache.put(cacheKey(entry.getKey(), defaultContext),
                    new CachedValue(EvaluationResult.from(entry.getValue()), expiresAt));
        }
    }

    public void invalidate(String key) {
        cache.keySet().removeIf(cacheKey -> cacheKey.startsWith(key + "|"));
    }

    @Override
    public synchronized void close() {
        if (syncTask != null) syncTask.cancel(false);
        synchronizer.shutdownNow();
    }

    private FeatureFlagEvaluationResponseDTO postSingle(String key, Map<String, Object> context)
            throws IOException, InterruptedException {
        String encodedKey = URLEncoder.encode(key, StandardCharsets.UTF_8);
        HttpResponse<String> response = send("/client/environments/" + environmentId + "/flags/" + encodedKey + "/evaluate",
                Map.of("context", context));
        return objectMapper.readValue(response.body(), FeatureFlagEvaluationResponseDTO.class);
    }

    private HttpResponse<String> send(String path, Object body) throws IOException, InterruptedException {
        String json = objectMapper.writeValueAsString(body);
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
                .header("Content-Type", "application/json")
                .header("X-Environment-Key", clientKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Feature flag server returned HTTP " + response.statusCode());
        }
        return response;
    }

    private String cacheKey(String key, Map<String, Object> context) throws IOException {
        return key + "|" + objectMapper.writeValueAsString(context == null ? Map.of() : context);
    }

    private String trimTrailingSlash(String value) {
        return value == null ? "" : value.replaceAll("/+$", "");
    }

    private record CachedValue(EvaluationResult result, Instant expiresAt) {}

    public record EvaluationResult(String key, boolean enabled, Object value, String variantKey, String reason) {
        private static EvaluationResult from(FeatureFlagEvaluationResponseDTO response) {
            return new EvaluationResult(response.getKey(), Boolean.TRUE.equals(response.getEnabled()), response.getValue(),
                    response.getVariantKey(), response.getReason());
        }
    }
}
