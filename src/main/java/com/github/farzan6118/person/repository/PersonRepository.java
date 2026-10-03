package com.github.farzan6118.person.repository;

import com.github.farzan6118.person.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByUuid(UUID uuid);

    boolean existsByContact_Email(String email);

    boolean existsByContact_MobileNumber(String mobile);

    boolean existsByContact_EmailAndUuidNot(String email, UUID uuid);

    boolean existsByContact_MobileNumberAndUuidNot(String telephone, UUID uuid);
}
