package com.chalk.ffs.Controller.admin;

import com.chalk.ffs.DTO.User.UserDTO;
import com.chalk.ffs.DTO.User.UserListDTO;
import com.chalk.ffs.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/organizations/{orgId}/users")
    public ResponseEntity<UserDTO> createUserByOrgId(@PathVariable Long orgId, @Valid @RequestBody UserDTO userDTO) {
        UserDTO created = userService.createUserByOrgId(orgId, userDTO);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping("/organizations/{orgId}/users")
    public ResponseEntity<UserListDTO> getUserListByOrgId(@PathVariable Long orgId) {
        UserListDTO userListDTO = userService.getUserListByOrgId(orgId);
        return ResponseEntity.ok(userListDTO);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long userId) {
        UserDTO userDTO = userService.getUserById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/users/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long userId, @RequestBody UserDTO userDTO) {
        UserDTO updated = userService.updateUser(userId, userDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
