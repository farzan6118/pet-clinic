package com.github.farzan6118.petclinic.person.repository;

import com.github.farzan6118.petclinic.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUuid(UUID uuid);

    boolean existsByProfile_Email(String email);

    boolean existsByProfile_MobileNumber(String mobile);

    boolean existsByProfile_EmailAndUuidNot(String email, UUID uuid);

    boolean existsByProfile_MobileNumberAndUuidNot(String telephone, UUID uuid);
}
