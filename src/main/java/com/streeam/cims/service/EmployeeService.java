package com.streeam.cims.service;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Employee}.
 */
@Service
@Transactional
public class EmployeeService {

    private final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final EmployeeSearchRepository employeeSearchRepository;


    @Autowired
    private  UserService userService;

    @Autowired
    private AuthorityRepository authorityRepository;

    private final NotificationService notificationService;

    private final CompanyService companyService;

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,CompanyService companyService,
                           EmployeeSearchRepository employeeSearchRepository, NotificationService notificationService) {
        this.companyService = companyService;
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeSearchRepository = employeeSearchRepository;
        this.notificationService = notificationService;
    }

    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save.
     * @return the persisted entity.
     */
    public EmployeeDTO save(User linkedUser, EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);

        linkedUser.setLogin(employeeDTO.getLogin());
        linkedUser.setFirstName(employeeDTO.getFirstName());
        linkedUser.setLastName(employeeDTO.getLastName());
        linkedUser.setLangKey(employeeDTO.getLanguage());

        Employee employee = employeeMapper.toEntity(employeeDTO);
        employee.setUser(linkedUser);
        employee = employeeRepository.save(employee);
        EmployeeDTO result = employeeMapper.toDto(employee);
        employeeSearchRepository.save(employee);
        return result;
    }

    /**
     * Get all the employees.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Employees");
        return employeeRepository.findAll(pageable)
            .map(employeeMapper::toDto);
    }


    /**
     * Get one employee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id)
            .map(employeeMapper::toDto);
    }

    /**
     * Get one employee by email.
     *
     * @param email the email of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<EmployeeDTO> findOne(String email) {
        log.debug("Request to get Employee : {}", email);
        return employeeRepository.findOneByEmail(email)
            .map(employeeMapper::toDto);
    }

    /**
     * Get one employee by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Employee> findOneById(Long id) {
        log.debug("Request to get Employee : {}", id);
        return employeeRepository.findById(id);
    }

    /**
     * Delete the employee by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Employee : {}", id);
        employeeRepository.deleteById(id);
        employeeSearchRepository.deleteById(id);
    }

    /**
     * Search for the employee corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<EmployeeDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Employees for query {}", query);
        return employeeSearchRepository.search(queryStringQuery(query), pageable)
            .map(employeeMapper::toDto);
    }


    /**
     *  Create and save a employee and link it to a user
     * @param newUser
     * @return Employee with the same details as the user
     */
    public Employee createEmployeeFromUser(User newUser) {
        Employee employee = new Employee().
            login(newUser.getLogin())
            .email(newUser.getEmail())
            .hired(false)
            .user(newUser);

        employee = employeeRepository.save(employee);
        employeeSearchRepository.save(employee);

        return employee;
    }

    public Optional<Employee> findOneByLogin(String login) {
        return employeeRepository.findByLogin(login);
    }


    /**
     *  Creates and saves a employee and links it to a company
     * @param employee
     * @param company
     */
    public EmployeeDTO saveWithCompany(Employee employee, Company company) {
        employee.setCompany(company);
        Employee updatedEmployee = employeeRepository.save(employee);
        employeeSearchRepository.save(updatedEmployee);
        EmployeeDTO result = employeeMapper.toDto(updatedEmployee);
        return result;
    }

    public Optional<User> findUserByEmail(String email) {
        return  userService.findOneByEmail(email);
    }

    public Optional<User> findCurrentUser(String login) {

        return userService.getCurrentUser(login);
    }

    public boolean hasCurrentUserRoles(User currentUser, String... roles) {

        return userService.checkIfUserHasRoles(currentUser, roles);
    }

    public Optional<Company> findEmployeesCompany(Employee currentEmployee) {
        return companyService.findCompanyById(currentEmployee.getCompany().getId());
    }

    public Optional<Employee> findOneByEmail(String email) {
        return employeeRepository.findOneByEmail(email);
    }

    public boolean checkUserHasRoles(User user, String... roles) {
        return userService.checkIfUserHasRoles(user, roles);
    }

    public Page<EmployeeDTO> findCompanysEmployees(Pageable pageable, Long id) {

        return  employeeRepository.findAllByCompanyId(pageable, id).map(employeeMapper::toDto);
    }

    public void deleteLinkedUser(User linkedUser) {
        userService.deleteUser(linkedUser.getLogin());
    }

    public void deleteEmployeesNotifications(Employee employeeToDelete) {
        notificationService.deleteAllByEmployee(employeeToDelete);
    }

    public List<Employee> findAllEmployeesFromCompany(Company company) {
        return employeeRepository.findAllByCompany(company);
    }

    public Optional<Company> findCompanyById(Long companyId) {
        return companyService.findCompanyById(companyId);
    }

    public Optional<Employee> getCompanyManager(Company company) {
        return companyService.getCompanysManager(company);
    }

    public void sendNotificationToEmployee(Employee authorEmployee,String referencedUserEmail,Long companyId, NotificationType requestToJoin, String subject) {
        notificationService.saveWithEmployee(authorEmployee,referencedUserEmail, companyId,requestToJoin, subject);
    }

    public boolean userRequestedToJoinCompanyAndWasRejectedLessThen3DaysAgo(Employee employeeToBeHired,NotificationType theEvent,Long companyId, int nDaysAgo) {

        return notificationService.hasEventOccurredInThePast(employeeToBeHired, theEvent, companyId, nDaysAgo);
    }

    public Page<EmployeeDTO> findAllUnemployedEmployees(Pageable pageable) {

        return employeeRepository.findAllByHiredFalse(pageable)
            .map(employeeMapper::toDto);
    }

    public CompanyDTO saveUserEmployeeAndCompany(Employee approvedEmployee, User currentUser, Company companyWhereEmployeeHasJoined) {
        return companyService.saveUserEmployeeAndComapany(approvedEmployee, currentUser, companyWhereEmployeeHasJoined);
    }

    public void sendNotificationToAllFromCompanyExceptManagerAndCurrentEmployee(Long companyId,Employee referencedEmployee , NotificationType notificationType, String subject) {
        // get all company's employees

        Optional.of(companyService
            .findCompanyById(companyId))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(company -> {
                company.getEmployees().stream()
                    .map(Employee::getEmail)
                    .map(employeeRepository::findOneByEmail)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .filter(employee1 ->!userService.checkIfUserHasRoles(employee1.getUser(),AuthoritiesConstants.MANAGER))
                    .filter(employee2 ->!employee2.getId().equals(referencedEmployee.getId()) )
                    .forEach(employee3 -> {
                        notificationService.saveWithEmployee(employee3, referencedEmployee.getEmail(), companyId, notificationType, subject);
                    });
                return company;
            });
    }

    public boolean hasEmployeeRejectedInvitationInLastThreeDays( Employee employeeToBeHired,NotificationType theEvent,Long companyId, int nDaysAgo) {

        return notificationService.hasEventOccurredInThePast(employeeToBeHired, theEvent, companyId, nDaysAgo);
    }

    public boolean companyInvitedUserToJoinLessThen14DaysAgo( Employee employeeToBeHired,NotificationType theEvent,Long companyId, int nDaysAgo) {

        return notificationService.hasEventOccurredInThePast(employeeToBeHired, theEvent, companyId, nDaysAgo);

    }
}
