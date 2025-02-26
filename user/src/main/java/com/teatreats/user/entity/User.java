package com.teatreats.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Generated;

@Entity
@Data
public class User {
    @Id

    @GeneratedValue(generator = "sequence-generator")
    private int userId;

    @NotBlank(message = "Username is mandatory")
    @Size(min = 5, message = "Username must be at least 5 characters long")
    private String userName;

    @NotBlank(message = "Password is mandatory")
    @Pattern(regexp ="(?=.*\\d.*)(?=.*[a-zA-Z].*)(?=.*[!#\\$%&@\\?].*).{8,100}",message = "password must contain 1 lowercase,1 uppercase and 1 special character and must be of min. length 8")
    private String password;

//    @NotNull(message = "Email cannot be null")
    @Email(regexp = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", message ="please provide a valid email address")

    private String email;

    @Enumerated(EnumType.STRING)
    private Role roles;
     private String address;

    private  boolean isDeleted;

    private  boolean isBlocked;



}

