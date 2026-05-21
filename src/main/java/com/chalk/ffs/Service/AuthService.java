package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.Auth.AuthResponseDTO;
import com.chalk.ffs.DTO.Auth.LoginRequestDTO;
import com.chalk.ffs.DTO.Auth.RegisterRequestDTO;
import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Entity.User;
import com.chalk.ffs.Enums.UserRole;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.User.EmailAlreadyExistsException;
import com.chalk.ffs.Repository.OrganizationRepository;
import com.chalk.ffs.Repository.UserRepository;
import com.chalk.ffs.Security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public AuthService(
            UserRepository userRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDetailsService userDetailsService,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtService = jwtService;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public AuthResponseDTO register(RegisterRequestDTO registerRequestDTO) {
        if (userRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A user already exists with email: " + registerRequestDTO.getEmail());
        }

        Organization organization = organizationRepository.getOrganizationById(registerRequestDTO.getOrgId())
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "No organization found with id:" + registerRequestDTO.getOrgId()
                ));

        User user = new User();
        user.setOrganization(organization);
        user.setName(registerRequestDTO.getName());
        user.setEmail(registerRequestDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        boolean organizationHasAdmin = organization.getUserList()
                .stream()
                .anyMatch(existingUser -> existingUser.getRole() == UserRole.ADMIN);
        user.setRole(organizationHasAdmin ? UserRole.USER : UserRole.ADMIN);

        user = userRepository.save(user);
        organization.getUserList().add(user);

        return buildAuthResponse(user);
    }

    public AuthResponseDTO login(LoginRequestDTO loginRequestDTO) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDTO.getEmail(), loginRequestDTO.getPassword())
        );

        User user = userRepository.findByEmail(loginRequestDTO.getEmail())
                .orElseThrow(() -> new org.springframework.security.core.userdetails.UsernameNotFoundException(
                        "No user found with email: " + loginRequestDTO.getEmail()
                ));

        return buildAuthResponse(user);
    }

    private AuthResponseDTO buildAuthResponse(User user) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String token = jwtService.generateToken(
                userDetails,
                Map.of("role", user.getRole().name(), "userId", user.getId(), "orgId", user.getOrganization().getId())
        );

        return new AuthResponseDTO(
                token,
                user.getId(),
                user.getOrganization().getId(),
                user.getName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
