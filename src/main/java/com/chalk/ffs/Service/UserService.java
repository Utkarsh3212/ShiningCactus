package com.chalk.ffs.Service;

import com.chalk.ffs.DTO.User.UserDTO;
import com.chalk.ffs.DTO.User.UserListDTO;
import com.chalk.ffs.Entity.Organization;
import com.chalk.ffs.Entity.User;
import com.chalk.ffs.Exceptions.User.EmailAlreadyExistsException;
import com.chalk.ffs.Exceptions.Organization.OrganizationNotFoundException;
import com.chalk.ffs.Exceptions.User.UserNotFoundException;
import com.chalk.ffs.Repository.OrganizationRepository;
import com.chalk.ffs.Repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, OrganizationRepository organizationRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO getUserById(Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "User not found with the given id: " + userId
                ));

        return new UserDTO(user);
    }

    public UserListDTO getUserListByOrgId(Long orgId) {
        Organization organization = organizationRepository.getOrganizationById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "No organization found with id:" + orgId
                ));

        return new UserListDTO(organization);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDTO createUserByOrgId(Long orgId, UserDTO userDTO) {
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A user already exists with email: " + userDTO.getEmail());
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            throw new com.chalk.ffs.Exceptions.IllegalStateException("Password required for user creation");
        }

        Organization organization = organizationRepository.getOrganizationById(orgId)
                .orElseThrow(() -> new OrganizationNotFoundException(
                        "No organization found with id: " + orgId
                ));

        User user = new User(userDTO, organization);
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user = userRepository.save(user);
        organization.getUserList().add(user);

        return new UserDTO(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public UserDTO updateUser(Long userId, UserDTO userDTO) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "No user found with id:" + userId
                ));

        if (userDTO.getEmail() != null && !user.getEmail().equals(userDTO.getEmail()) && userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("A user already exists with email: " + userDTO.getEmail());
        }

        user.updateFromDTO(userDTO);
        if (userDTO.getPassword() != null && !userDTO.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        }
        user = userRepository.save(user);

        return new UserDTO(user);
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public void deleteUser(Long userId) {
        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new UserNotFoundException(
                        "No user found with id:" + userId
                ));

        Organization organization = user.getOrganization();
        organization.getUserList().remove(user);
        userRepository.delete(user);
    }
}
