package com.teatreats.purchase.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {

  private int userId;

  private String userName;

  private String password;

  private String email;

  private String roles;

  private String address;

  private boolean isDeleted;

  private boolean isBlocked;
}
