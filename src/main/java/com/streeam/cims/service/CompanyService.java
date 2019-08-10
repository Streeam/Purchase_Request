package com.streeam.cims.service;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.CompanyRepository;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Company}.
 */
@Service
@Transactional
public class CompanyService {

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    private final CompanyRepository companyRepository;

    private final CompanyMapper companyMapper;

    private final CompanySearchRepository companySearchRepository;

    private final UserService userService;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, UserService userService,
                          CompanySearchRepository companySearchRepository, EmployeeService employeeService, EmployeeMapper employeeMapper) {
        this.userService = userService;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companySearchRepository = companySearchRepository;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO save(CompanyDTO companyDTO) {
        log.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        CompanyDTO result = companyMapper.toDto(company);
        companySearchRepository.save(company);
        return result;
    }


    /**
     * Save a company.
     *
     * @param companyDTO the entity to save.
     * @param employee
     * @return the persisted entity.
     */
    public CompanyDTO saveWithEmployee(CompanyDTO companyDTO, Optional<Employee> employee) {
        log.debug("Request to save Company : {}", companyDTO);
        Company company = companyMapper.toEntity(companyDTO);
        if(employee.isPresent()) {
            employee.get().setHired(true);
            Set<Employee> employees = company.getEmployees();
            employees.add(employee.get());
            company.setEmployees(employees);
            company = companyRepository.save(company);
            employeeService.saveWithCompany(employee.get(), company);
            log.debug("Request to save Company with employee: {}", employee.get().getLogin());
        }


        CompanyDTO result = companyMapper.toDto(company);
        companySearchRepository.save(company);
        return result;
    }

    /**
     * Get all the companies.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Companies");
        return companyRepository.findAll(pageable)
            .map(companyMapper::toDto);
    }


    /**
     * Get one company by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<CompanyDTO> findOne(Long id) {
        log.debug("Request to get Company : {}", id);
        return companyRepository.findById(id)
            .map(companyMapper::toDto);
    }

    /**
     * Delete the company by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);
        companyRepository.deleteById(id);
        companySearchRepository.deleteById(id);
    }

    /**
     * Search for the company corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<CompanyDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Companies for query {}", query);
        return companySearchRepository.search(queryStringQuery(query), pageable)
            .map(companyMapper::toDto);
    }

    /**
     * Checks if the company email already exists
     * @param email
     * @return true if the company email is present in the database false otherwise
     */
    public boolean companyEmailAlreadyExists(String email) {

        return companyRepository.findAll().stream().anyMatch(company -> company.getEmail().equals(email));

    }

    /**
     * Checks if the company name already exists
     * @param name
     * @return true if the company name is present in the database false otherwise
     */
    public boolean companyNameAlreadyExists(String name) {

        return companyRepository.findAll().stream().anyMatch(company -> company.getName().equals(name));

    }
    /**
     *  Checks if the current uses has the provided authorities
     * @param roles
     * @return true if the user has at least one of the authorities false otherwise
     */
    public boolean checkUserHasRoles(User user,String... roles) {

        return userService.checkIfUserHasRoles(user, roles);
    }

    /**
     *
     * @return an optional of the current user
     */
    public Optional<User> findCurrentUser() {

        return userService.getCurrentUser();
    }

    public Optional<Employee> findEmployeeFromUser(User user) {



        return userService.findLinkedEmployee(user);
    }
}
