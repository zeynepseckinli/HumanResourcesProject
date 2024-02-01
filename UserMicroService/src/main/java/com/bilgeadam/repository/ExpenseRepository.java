package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Expense;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseRepository extends MongoRepository<Expense, String> {

    List<Expense> findAllByRequestUserId(String id);
    List<Expense> findAllByResponseUserId(String id);
}
