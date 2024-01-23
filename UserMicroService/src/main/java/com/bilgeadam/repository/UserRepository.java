package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.UserProfile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserProfile, String> {

    Optional<UserProfile> findOptionalByAuthId(Long authId);

}
