package com.streeam.cims.repository;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;


/**
 * Spring Data  repository for the Company entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findOneById(Long id);

    Optional<Company> findOneByEmployees(Set<Employee> employees);

    Optional<Company> findOneByEmail(String email);
}
