package com.teatreats.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateUserDTO {
  @NotBlank(message = "Username is mandatory")
  @Size(min = 5, message = "Username must be at least 5 characters long")
  private String userName;

  @NotBlank(message = "Email is mandatory")
  @Pattern(
      regexp =
          "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
      message = "Email should be valid")
  private String email;

  @NotBlank(message = "Address is mandatory")
  private String address;

  private String updatedUserName;
}
