package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Company;
import com.bilgeadam.utility.enums.EState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findOptionalByName(String companyName);

    Optional<Company> findById(String id);

    List<Company> findAllByStateNot(EState state);

    List<Company> findAllByState (EState state);


}