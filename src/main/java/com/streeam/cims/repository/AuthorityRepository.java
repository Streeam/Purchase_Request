package com.streeam.cims.repository;

import com.streeam.cims.domain.Authority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link Authority} entity.
 */
public interface AuthorityRepository extends JpaRepository<Authority, String> {
     Optional<Authority> findOneByName(String s);
}
