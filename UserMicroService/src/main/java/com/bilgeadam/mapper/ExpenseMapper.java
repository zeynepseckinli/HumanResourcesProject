package com.bilgeadam.mapper;

import com.bilgeadam.dto.request.CreateExpenseRequestDto;
import com.bilgeadam.dto.response.ExpensesListResponseDtoForRequestUser;
import com.bilgeadam.dto.response.ExpensesListResponseDtoForResponseUser;
import com.bilgeadam.repository.entity.Expense;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ExpenseMapper {
    ExpenseMapper INSTANCE = Mappers.getMapper(ExpenseMapper.class);
    Expense fromDto(final CreateExpenseRequestDto dto);
    ExpensesListResponseDtoForResponseUser toDtoForResponseUser(final Expense expense);
    ExpensesListResponseDtoForRequestUser toDtoForRequestUser(final Expense expense);
}
