package com.github.farzan6118.petclinic.owner.repository;

import com.github.farzan6118.petclinic.owner.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByUuid(UUID uuid);

    boolean existsByPerson_profile_Email(String email);

    boolean existsByPerson_profile_MobileNumber(String mobile);

    boolean existsByPerson_profile_EmailAndUuidNot(String email, UUID uuid);

    boolean existsByPerson_profile_MobileNumberAndUuidNot(String personProfileMobileNumber, UUID uuid);
}
