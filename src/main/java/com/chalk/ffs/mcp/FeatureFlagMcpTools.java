package com.chalk.ffs.mcp;

import com.chalk.ffs.DTO.FeatureFlag.FeatureFlagDTO;
import com.chalk.ffs.Service.FeatureFlagService;
import com.chalk.ffs.Service.FeatureFlagVersionService;
import com.chalk.ffs.Service.FlagEvaluationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.modelcontextprotocol.server.McpServerFeatures;
import io.modelcontextprotocol.spec.McpSchema;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
public class FeatureFlagMcpTools {
    private final FeatureFlagService featureFlagService;
    private final FeatureFlagVersionService versionService;
    private final FlagEvaluationService evaluationService;
    private final ObjectMapper objectMapper;

    public FeatureFlagMcpTools(FeatureFlagService featureFlagService,
                               FeatureFlagVersionService versionService,
                               FlagEvaluationService evaluationService,
                               ObjectMapper objectMapper) {
        this.featureFlagService = featureFlagService;
        this.versionService = versionService;
        this.evaluationService = evaluationService;
        this.objectMapper = objectMapper;
    }

    public List<McpServerFeatures.SyncToolSpecification> specifications() {
        return List.of(
                tool("list_feature_flags", "List all feature flags in an environment.",
                        schema(Map.of("environmentId", integerProperty()), List.of("environmentId")),
                        args -> featureFlagService.listFeatureFlags(longArg(args, "environmentId"))),
                tool("get_feature_flag", "Get a feature flag by id.",
                        schema(Map.of("flagId", integerProperty()), List.of("flagId")),
                        args -> featureFlagService.getFeatureFlag(longArg(args, "flagId"))),
                tool("evaluate_feature_flag", "Evaluate a flag using the server-side admin authorization boundary.",
                        schema(Map.of("environmentId", integerProperty(), "key", stringProperty(),
                                "context", objectProperty()), List.of("environmentId", "key")),
                        args -> evaluationService.evaluateForAdmin(longArg(args, "environmentId"),
                                stringArg(args, "key"), objectArg(args, "context"))),
                tool("create_feature_flag", "Create a feature flag in an environment.",
                        schema(Map.of("environmentId", integerProperty(), "flag", objectProperty()),
                                List.of("environmentId", "flag")),
                        args -> featureFlagService.createFeatureFlag(longArg(args, "environmentId"),
                                flagArg(args))),
                tool("update_feature_flag", "Update a feature flag by id.",
                        schema(Map.of("flagId", integerProperty(), "flag", objectProperty()),
                                List.of("flagId", "flag")),
                        args -> featureFlagService.updateFeatureFlagDTO(longArg(args, "flagId"), flagArg(args))),
                tool("publish_feature_flag", "Publish a new immutable release snapshot for a flag.",
                        schema(Map.of("flagId", integerProperty()), List.of("flagId")),
                        args -> versionService.publish(longArg(args, "flagId"))),
                tool("list_feature_flag_releases", "List published releases for a feature flag.",
                        schema(Map.of("flagId", integerProperty()), List.of("flagId")),
                        args -> versionService.list(longArg(args, "flagId"))),
                tool("rollback_feature_flag", "Restore a feature flag from a published release.",
                        schema(Map.of("flagId", integerProperty(), "versionId", integerProperty()),
                                List.of("flagId", "versionId")),
                        args -> versionService.rollback(longArg(args, "flagId"), longArg(args, "versionId")))
        );
    }

    private McpServerFeatures.SyncToolSpecification tool(String name, String description,
                                                          Map<String, Object> inputSchema,
                                                          Function<Map<String, Object>, Object> action) {
        McpSchema.Tool definition = McpSchema.Tool.builder(name, inputSchema)
                .description(description)
                .build();
        return McpServerFeatures.SyncToolSpecification.builder()
                .tool(definition)
                .callHandler((exchange, request) -> execute(request.arguments(), action))
                .build();
    }

    private McpSchema.CallToolResult execute(Map<String, Object> arguments,
                                             Function<Map<String, Object>, Object> action) {
        try {
            Object result = action.apply(arguments == null ? Map.of() : arguments);
            return McpSchema.CallToolResult.builder()
                    .content(List.of(McpSchema.TextContent.builder(
                            objectMapper.writeValueAsString(result)).build()))
                    .isError(false)
                    .build();
        } catch (JsonProcessingException | RuntimeException exception) {
            return McpSchema.CallToolResult.builder()
                    .content(List.of(McpSchema.TextContent.builder(
                            "MCP tool failed: " + exception.getMessage()).build()))
                    .isError(true)
                    .build();
        }
    }

    private FeatureFlagDTO flagArg(Map<String, Object> arguments) {
        Object rawFlag = arguments.get("flag");
        if (!(rawFlag instanceof Map<?, ?>)) {
            throw new IllegalArgumentException("flag must be a JSON object");
        }
        return objectMapper.convertValue(rawFlag, FeatureFlagDTO.class);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> objectArg(Map<String, Object> arguments, String name) {
        Object value = arguments.get(name);
        if (value == null) return Collections.emptyMap();
        if (!(value instanceof Map<?, ?>)) {
            throw new IllegalArgumentException(name + " must be a JSON object");
        }
        return (Map<String, Object>) value;
    }

    private Long longArg(Map<String, Object> arguments, String name) {
        Object value = arguments.get(name);
        if (value instanceof Number number) return number.longValue();
        if (value instanceof String string) return Long.valueOf(string);
        throw new IllegalArgumentException(name + " is required and must be an integer");
    }

    private String stringArg(Map<String, Object> arguments, String name) {
        Object value = arguments.get(name);
        if (value instanceof String string && !string.isBlank()) return string;
        throw new IllegalArgumentException(name + " is required and must be a non-empty string");
    }

    private Map<String, Object> schema(Map<String, Object> properties, List<String> required) {
        Map<String, Object> schema = new LinkedHashMap<>();
        schema.put("type", "object");
        schema.put("properties", properties);
        schema.put("required", required);
        return schema;
    }

    private Map<String, Object> integerProperty() {
        return Map.of("type", "integer", "description", "Numeric identifier");
    }

    private Map<String, Object> stringProperty() {
        return Map.of("type", "string");
    }

    private Map<String, Object> objectProperty() {
        return Map.of("type", "object");
    }
}
