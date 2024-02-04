package com.bilgeadam.rabbitmq.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterMailModel implements Serializable {
    private String personalEmail;
    private String email;
    private String name;
    private String activationCode;
}
