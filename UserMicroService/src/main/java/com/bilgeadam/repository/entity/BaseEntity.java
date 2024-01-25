package com.bilgeadam.repository.entity;

import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDate;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {
    private LocalDate createDate;
    private LocalDate updateDate;
    private EState state;
}
