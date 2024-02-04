package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Company;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends MongoRepository<Company, String> {

    Optional<Company> findOptionalByName(String companyName);

    Optional<Company> findById(String id);


}