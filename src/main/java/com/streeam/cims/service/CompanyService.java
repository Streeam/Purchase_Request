package com.streeam.cims.service;

import com.streeam.cims.domain.Authority;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.CompanyRepository;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.dto.NotificationDTO;
import com.streeam.cims.service.dto.UserDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
import com.streeam.cims.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

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

    private final UserMapper userMapper;

    private final EmployeeService employeeService;

    private final EmployeeMapper employeeMapper;

    private final NotificationService notificationService;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, UserService userService,
                          CompanySearchRepository companySearchRepository, EmployeeService employeeService, EmployeeMapper employeeMapper,
                          UserMapper userMapper, NotificationService notificationService) {
        this.userService = userService;
        this.notificationService = notificationService;
        this.userMapper = userMapper;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companySearchRepository = companySearchRepository;
        this.employeeService = employeeService;
        this.employeeMapper = employeeMapper;
    }

//    /**
//     * Save a company.
//     *
//     * @param companyDTO the entity to save.
//     * @return the persisted entity.
//     */
//    public CompanyDTO save(CompanyDTO companyDTO) {
//        log.debug("Request to save Company : {}", companyDTO);
//        Company company = companyMapper.toEntity(companyDTO);
//        company = companyRepository.save(company);
//        CompanyDTO result = companyMapper.toDto(company);
//        companySearchRepository.save(company);
//        return result;
//    }


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

        // Find all employees from the company and the manager and remove the ROLE_EMPLOYEE and ROLE_MANAGER

        Optional<Company> company = companyRepository.findOneById(id);
        if(company.isPresent()){
            Set<Employee>   employees = company.get().getEmployees();
            employees.stream().filter(employee-> {
                Optional<User> user = userService.findOneByLogin(employee.getLogin());
                boolean managersAndEmployees = userService.checkIfUserHasRoles(user.get(), AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE);

                Set<Authority> authorities = user.get().getAuthorities().stream().
                    filter(authority -> !authority.getName().equals(AuthoritiesConstants.MANAGER) && !authority.getName().equals(AuthoritiesConstants.EMPLOYEE)).
                    collect(Collectors.toSet());
                user.get().setAuthorities(authorities);
                UserDTO userDTO = userMapper.userToUserDTO(user.get());
                userService.updateUser(userDTO);

                return managersAndEmployees;
            }).forEach(employee -> employee.setHired(false));

        }

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


    public Page<CompanyDTO> findCompanyWithCurrentUser(User user) {
        Optional<Employee> employee = employeeService.findOneByLogin(user.getLogin());
        Optional<Company> company = companyRepository.findOneByEmployees(Collections.singleton(employee.get()));
        CompanyDTO companyDTO = companyMapper.toDto(company.get());

        return new PageImpl<>(Arrays.asList(companyDTO));
    }

    public Optional<Employee> getCompanysManager(Company company) {

        return company.getEmployees().stream()
            .filter(employee -> employee.getUser().getAuthorities().stream().anyMatch(authority-> authority.getName().equals(AuthoritiesConstants.MANAGER))).findAny();

    }

    public String getEmployeeEmail(Employee employee) {
        log.debug("Retrieving the employee email address:{}", employee.getEmail());
        return employee.getEmail();
    }

    public Optional<Company> findCompanyById(Long companyId) {
        return companyRepository.findOneById(companyId);
    }

    public void sendNotificationToEmployee(Employee employee) {

       NotificationDTO notificationDTO =  notificationService.saveWithEmployee(employee);

    }
}
