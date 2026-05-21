package com.chalk.ffs.Security;

import com.chalk.ffs.Repository.UserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public ApplicationUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.chalk.ffs.Entity.User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found with email: " + username));

        return User.withUsername(user.getEmail())
                .password(user.getPassword() == null ? "" : user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
