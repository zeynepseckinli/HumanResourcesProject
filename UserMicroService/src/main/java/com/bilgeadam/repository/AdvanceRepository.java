package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.Advance;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface AdvanceRepository extends MongoRepository<Advance, String > {

    List<Advance> findAllByRequestUserId(String id);
    List<Advance> findAllByResponseUserId(String id);


}
