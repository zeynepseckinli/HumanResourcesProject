package com.bilgeadam.repository.entity;

import com.bilgeadam.utility.enums.EState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;


@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseEntity implements Serializable {
    private Long createDate;
    private Long updateDate;
    private EState state;
}
