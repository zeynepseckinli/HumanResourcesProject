package com.bilgeadam.repository.entity;

import com.bilgeadam.utility.enums.EState;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity {
    private LocalDate createDate;
    private LocalDate UpdateDate;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EState state = EState.ACTIVE;
}
