package com.bilgeadam.repository;

import com.bilgeadam.repository.entity.UserProfile;
import com.bilgeadam.utility.enums.ERole;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserProfile, String> {

    Optional<UserProfile> findOptionalByAuthId(Long authId);

    Optional<UserProfile> findOptionalByEmail(String email);

    Optional<UserProfile> findByPersonalEmail(String personalEmail);

    List<UserProfile> findAllUserProfileByManagerId(String id);


    Optional<List<UserProfile>> findOptionalUserProfileByRole(ERole role);
}
