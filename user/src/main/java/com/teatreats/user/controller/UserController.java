package com.teatreats.user.controller;

import com.teatreats.user.dto.ChangePasswordDTO;
import com.teatreats.user.dto.UpdateUserDTO;
import com.teatreats.user.dto.UserDTO;
import com.teatreats.user.customexception.UserAlreadyExistsException;
import com.teatreats.user.customexception.UserNotFoundException;

import com.teatreats.user.entity.Role;
import com.teatreats.user.entity.User;
import com.teatreats.user.service.JWTService;
import com.teatreats.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

  @Autowired private UserService userService;

  @Autowired private JWTService jwtService;

  @GetMapping("/id/{userId}")
  public ResponseEntity<?> getUserById(@PathVariable int userId) {
    Optional<User> user = userService.getUserById(userId);
    if (user.isPresent()) {
      UserDTO userDTO = convertToDTO(user.get());
      return ResponseEntity.ok(userDTO);
    } else {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Optional.empty());
    }
  }

  private UserDTO convertToDTO(User user) {
    UserDTO userDTO = new UserDTO();
    userDTO.setUserId(user.getUserId());
    userDTO.setUserName(user.getUserName());
    userDTO.setEmail(user.getEmail());
    userDTO.setAddress(user.getAddress());
    return userDTO;
  }

  @GetMapping
  public ResponseEntity<String> healthCheck() {
    return ResponseEntity.ok("User Microservice is UP!!!");
  }

  @PostMapping("/signup")
  public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
    try {
      user.setRoles(Role.USER);
      User registeredUser = userService.registerUser(user);
      UserDTO userDTO = new UserDTO();
      userDTO.setUserId(registeredUser.getUserId());
      userDTO.setUserName(registeredUser.getUserName());
      userDTO.setEmail((registeredUser.getEmail()));
      userDTO.setAddress(registeredUser.getAddress());
      userDTO.setRoles(registeredUser.getRoles());
      return ResponseEntity.status(HttpStatus.CREATED).body(userDTO);
    } catch (UserAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @GetMapping("/csrf-token")
  public ResponseEntity<CsrfToken> getCsrfToken(HttpServletRequest request) {
    CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
    return ResponseEntity.ok(csrfToken);
  }

  @PostMapping("/login")
  public ResponseEntity<?> verify(@Valid @RequestBody User user) {
    try {
      String token = userService.verify(user);
      return ResponseEntity.ok(token);
    } catch (BadCredentialsException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
    }
  }

  @GetMapping("/protected")
  public ResponseEntity<String> protectedEndpoint() {
    return ResponseEntity.ok("Access granted to protected endpoint!");
  }

  @PutMapping("/update")
  public ResponseEntity<?> updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
    try {
      User updatedUser = userService.updateUser(updateUserDTO);
      return ResponseEntity.ok("User details updated.");
    } catch (UserAlreadyExistsException e) {
      return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    }
  }

  @DeleteMapping("/delete/{userId}")
  public ResponseEntity<?> deleteUser(@PathVariable int userId) {

    try {
      userService.deleteUser(userId);
      return ResponseEntity.status(HttpStatus.NO_CONTENT)
          .body("User account disabled successfully");
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
  }

  @PutMapping("/change-password")
  public ResponseEntity<?> changePassword(
      @Valid @RequestBody ChangePasswordDTO changePasswordDTO, HttpServletRequest request) {
    String authHeader = request.getHeader("Authorization");
    String token = authHeader.substring(7);
    String username = jwtService.extractUserName(token);

    boolean isPasswordChanged =
        userService.changePassword(
            username, changePasswordDTO.getOldPassword(), changePasswordDTO.getNewPassword());
    if (isPasswordChanged) {
      return ResponseEntity.ok("Password changed successfully");
    } else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Old password is incorrect");
    }
  }

  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/block-user/{userId}")
  public ResponseEntity<User> blockUser(@PathVariable int userId) {
    User blockedUser = userService.blockUser(userId);
    return ResponseEntity.ok(blockedUser);
  }

  @PatchMapping("/update/role/{id}")
  public ResponseEntity<?> updateRole(
      @RequestBody Map<String, String> request, @PathVariable int id) {
    try {
      String roleStr = request.get("role");
      Role role = Role.valueOf(roleStr); // Convert the string to Role enum
      return ResponseEntity.ok(userService.updateRole(role, id));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
          .body("Invalid role: " + request.get("role"));
    } catch (UserNotFoundException e) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User Not Found !!");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
          .body("An unexpected error occurred");
    }
  }
}
