package com.teatreats.user.dto;

import com.teatreats.user.entity.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.springframework.web.bind.annotation.CrossOrigin;

@Entity
@Data
@CrossOrigin
public class UserDTO {
  @Id
  @GeneratedValue(generator = "sequence-generator")
  private int userId;

  @NotBlank(message = "Username is mandatory")
  @Size(min = 5, message = "Username must be at least 5 characters long")
  private String userName;

  @NotBlank(message = "Password is mandatory")
  @Pattern(
      regexp = "(?=.*\\d.*)(?=.*[a-zA-Z].*)(?=.*[!#\\$%&\\?].*).{8,20}",
      message =
          "password must contain 1 lowercase,1 uppercase and 1 special character and must be of min. length 8")
  private String password;

  @NotBlank(message = "Email is mandatory")
  @Email(
      regexp =
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
      message = "Email should be valid")
  private String email;

  @Enumerated(EnumType.STRING)
  private Role roles;

  @NotBlank(message = "Address is mandatory")
  private String address;

  private boolean isDeleted;

  private boolean isBlocked;
}
