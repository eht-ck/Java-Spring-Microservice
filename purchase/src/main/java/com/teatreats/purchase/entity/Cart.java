package com.teatreats.purchase.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
//https://mayankposts.medium.com/database-auditing-in-spring-boot-with-spring-security-context-and-spring-data-jpa-9215b43744bb
public class Cart {
    @Id
    @GeneratedValue(generator = "sequence-generator")
    private int cartId;

    @NotNull(message = "User ID cannot be null")
    private int userId;

     @CreatedDate
    private Date createdAt;

     @LastModifiedDate
    private Date updatedAt;
}