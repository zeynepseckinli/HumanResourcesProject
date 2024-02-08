package com.bilgeadam.repository.entity;


import com.bilgeadam.utility.enums.ERole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "tbl_auth")
public class Auth extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 128)
    private String password;

    @Email
    private String email;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ERole role = ERole.EMPLOYEE;

}
