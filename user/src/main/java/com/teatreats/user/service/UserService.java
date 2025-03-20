package com.teatreats.user.service;

import com.teatreats.user.customexception.UserAlreadyExistsException;
import com.teatreats.user.dto.UpdateUserDTO;
import com.teatreats.user.entity.Role;
import com.teatreats.user.entity.User;
import com.teatreats.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.teatreats.user.customexception.UserNotFoundException;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  @Autowired private UserRepository userRepository;

  @Autowired private JWTService jwtService;
  @Autowired AuthenticationManager authManager;
  private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

  //    @Transactional
  public String registerUser(User user) {
    try {
      if (userRepository.existsByUserName(user.getUserName())) {
        throw new UserAlreadyExistsException("Username already exists");
      }
      if (userRepository.existsByEmail(user.getEmail())) {
        throw new UserAlreadyExistsException("Email already exists");
      }
      user.setPassword(encoder.encode(user.getPassword()));
      User createdUser =  userRepository.save(user);
      return jwtService.generateToken(createdUser.getUserName(), createdUser.getRoles(), createdUser.getUserId());

    } catch (Exception e) {

      e.printStackTrace();
      throw e;
    }
  }

  public Optional<User> getUserById(int userId) {
    return userRepository.findById(userId);
  }

  public String verify(User user) {
    try {
       String userName = user.getUserName();
      Authentication authentication =
          authManager.authenticate(
              new UsernamePasswordAuthenticationToken(user.getUserName(), user.getPassword()));

      if (authentication.isAuthenticated()) {
        User user1 = userRepository.findByUserName(userName);
        return jwtService.generateToken(user.getUserName(), user1.getRoles(), user1.getUserId());
      } else {
        throw new BadCredentialsException("Authentication failed");
      }
    } catch (BadCredentialsException e) {
      throw new BadCredentialsException("Invalid username or password", e);
    }
  }

  public User updateUser(UpdateUserDTO updateUserDTO) {
    User user = userRepository.findByUserName(updateUserDTO.getUserName());
    if (updateUserDTO.getUpdatedUserName() != null) {
      if (userRepository.existsByUserName(updateUserDTO.getUpdatedUserName())) {
        throw new UserAlreadyExistsException("Username already exists");
      }
      user.setUserName(updateUserDTO.getUpdatedUserName());
    }
    if (userRepository.existsByEmail(updateUserDTO.getEmail()) && !user.getEmail().equals(updateUserDTO.getEmail() )) {
      throw new UserAlreadyExistsException("Email already exists");
    }
    user.setEmail(updateUserDTO.getEmail());
    user.setAddress(updateUserDTO.getAddress());
    return userRepository.save(user);
  }

  public void deleteUser(int userId) {
    Optional<User> userOptional = userRepository.findById(userId);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      user.setDeleted(true);
      userRepository.save(user);
    } else {
      throw new UserNotFoundException("User with ID " + userId + " not found");
    }
  }

  public boolean changePassword(String username, String oldPassword, String newPassword) {
    Optional<User> userOptional = Optional.ofNullable(userRepository.findByUserName(username));
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (encoder.matches(oldPassword, user.getPassword())) {
        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return true;
      }
    }
    return false;
  }

  public User blockUser(int userId) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found"));
    user.setBlocked(true);
    return userRepository.save(user);
  }

  public boolean existsByUsername(String username) {
    return userRepository.existsByUserName(username);
  }

  public User getByUserName(String username) {
    return userRepository.findByUserName(username);
  }

  public int updateRole(Role role, int id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    user.setRoles(role); // Set the role dynamically
    userRepository.save(user); // Save the updated user
    return id;
  }

  public List<User> getAll() {
    return  userRepository.findAll();
  }
}
