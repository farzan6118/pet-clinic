package com.github.farzan6118.owner.repository;

import com.github.farzan6118.owner.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByUuid(UUID uuid);

    boolean existsByPerson_Contact_Email(String email);

    boolean existsByPerson_Contact_MobileNumber(String mobile);

    boolean existsByPerson_Contact_EmailAndUuidNot(String email, UUID uuid);

    boolean existsByPerson_Contact_MobileNumberAndUuidNot(String mobile, UUID uuid);
}
