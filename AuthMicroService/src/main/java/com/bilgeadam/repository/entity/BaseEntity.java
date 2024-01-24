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

@MappedSuperclass
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity {
    private Long createDate;
    private Long UpdateDate;
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EState state = EState.ACTIVE;
}
