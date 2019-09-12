package com.streeam.cims.repository;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Employee entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    Optional<Employee> findOneByEmail(String email);
    Optional<Employee> findByLogin(String login);

    Page<Employee> findAllByCompanyId(Pageable pageable, Long id);

    List<Employee> findAllByCompany(Company company);

    Page<Employee> findAllByHiredFalse(Pageable pageable);

    Optional<Employee> findOneByEmailIgnoreCase(String email);
}
