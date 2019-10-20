package com.streeam.cims.service;

import com.streeam.cims.domain.Authority;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.repository.CompanyRepository;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.UserRepository;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.repository.search.UserSearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
import com.streeam.cims.service.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    @Autowired
    private  UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private EmployeeService employeeService;

    private  final UserRepository userRepository;

    private final UserMapper userMapper;

    private  final EmployeeRepository employeeRepository;

    private final UserSearchRepository userSearchRepository;

    private final EmployeeMapper employeeMapper;

    private final NotificationService notificationService;

    private final AuthorityRepository authorityRepository;

    public CompanyService(CompanyRepository companyRepository, CompanyMapper companyMapper, UserRepository userRepository,
                          CompanySearchRepository companySearchRepository, EmployeeMapper employeeMapper, UserSearchRepository userSearchRepository,
                          EmployeeRepository employeeRepository,UserMapper userMapper, NotificationService notificationService, AuthorityRepository authorityRepository) {

        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authorityRepository = authorityRepository;
        this.notificationService = notificationService;
        this.companyRepository = companyRepository;
        this.companyMapper = companyMapper;
        this.companySearchRepository = companySearchRepository;
        this.employeeMapper = employeeMapper;
        this.employeeRepository = employeeRepository;
        this.userSearchRepository = userSearchRepository;
    }

    /**
     * Save a company.
     * @param companyDTO the entity to save.
     * @return the persisted entity.
     */
    public CompanyDTO save(CompanyDTO companyDTO) {
        Company company = companyMapper.toEntity(companyDTO);
        company = companyRepository.save(company);
        CompanyDTO result = companyMapper.toDto(company);
        companySearchRepository.save(company);
        return result;
    }

    /**
     * Save a company
     * @param company the entity to save.
     * @param employee
     * @return the persisted entity.
     */
    public CompanyDTO saveWithEmployee(Company company, Employee employee) {

        employee.setHired(true);
        company.getEmployees().add(employee);

        Company updatedCompany = companyRepository.save(company);
        EmployeeDTO employeeDTO = employeeService.saveWithCompany(employee, updatedCompany);

        log.debug("Request to save Company with employee: {}", employeeDTO);

        CompanyDTO result = companyMapper.toDto(updatedCompany);
        companySearchRepository.save(updatedCompany);
        log.debug("Request to save Company : {}", result);
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
     * Delete the company by email.
     *
     * @param id the id of the entity.
     */

    public void delete(Long id) {
        log.debug("Request to delete Company : {}", id);

        // Find all employees from the company and remove the ROLE_EMPLOYEE and ROLE_MANAGER

        Optional.of(companyRepository
            .findOneById(id))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .map(company -> {
                company.getEmployees().stream()
                    .map(Employee::getLogin)
                    .map(employeeRepository::findByLogin)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(employee -> {
                        Optional.of(userRepository
                        .findOneByLogin(employee.getLogin()))
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(user1 -> {
                                Set<Authority> managedAuthorities = new HashSet<>();
                                //managedAuthorities.clear(); // Clear all old authorities
                                user1.getAuthorities().stream()
                                    .map(Authority::getName)
                                    .map(authorityRepository::findOneByName)
                                    .filter(Optional::isPresent)
                                    .map(Optional::get)
                                    .filter(authority ->
                                            !AuthoritiesConstants.MANAGER.equals( authority.getName())&&
                                            !AuthoritiesConstants.EMPLOYEE.equals( authority.getName())
                                    )
                                    .forEach(managedAuthorities::add);
                                user1.setAuthorities(managedAuthorities);
                               // userRepository.save(user1);
                                userSearchRepository.save(user1);
                                userService.clearUserCaches(user1);
                                return user1;
                            });
                         employee.setHired(false);
                    });
                companySearchRepository.delete(company);
                return company;
            }).ifPresent(
                companyRepository::delete);
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
    public Optional<User> findCurrentUser(String login) {
        return userService.getCurrentUser(login);
    }

    public Optional<Employee> findEmployeeFromUser(User user) {
        return employeeService.findOneByLogin(user.getLogin());
    }


    public Page<CompanyDTO> findCompanyWithCurrentUser(User user) {
        Optional<Employee> employee = employeeService.findOneByEmail(user.getEmail());
        Optional<Company> company = companyRepository.findOneByEmployees(Collections.singleton(employee.get()));
        CompanyDTO companyDTO = companyMapper.toDto(company.get());

        return new PageImpl<>(Arrays.asList(companyDTO));
    }

    public Optional<Company> findCompanyWithCurrentUserNonPage(User user) {
        Optional<Employee> employee = employeeService.findOneByLogin(user.getLogin());
        Optional<Company> company = companyRepository.findOneByEmployees(Collections.singleton(employee.get()));
        return company;
    }

    public Optional<Employee> getCompanysManager(Company company) {

        return company.getEmployees().stream()
            .filter(employee ->
                employee.getUser()
                    .getAuthorities()
                    .stream()
                    .anyMatch(authority->
                        authority
                            .getName()
                            .equals(AuthoritiesConstants.MANAGER))).
                findAny();

    }

    public Optional<Company> findCompanyById(Long companyId) {
        return companyRepository.findOneById(companyId);
    }

    public Employee sendNotificationToEmployee(Employee authorEmployee, String referencedUserEmail,Long companyId, NotificationType notificationType, String comment) {

       return notificationService.saveWithEmployee(authorEmployee,referencedUserEmail,companyId, notificationType, comment);

    }

    public Optional<User> findUserByEmail(String userEmail) {
        return userService.findOneByEmail(userEmail);
    }

    public CompanyDTO saveUserEmployeeAndComapany(Employee employee, User user, Company company) {
        employee.setUser(user);
       CompanyDTO companyDTO = saveWithEmployee(company, employee);
        return  companyDTO;
    }

    public CompanyDTO removeEmployeeFromCompany(Employee employee, User user, Company company) {
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        user.setAuthorities(authorities);

        employee.setHired(false);
        employee.setUser(user);
        Set remainingEmployees = company.getEmployees();
        remainingEmployees.remove(employee);
        company.setEmployees(remainingEmployees);

        employee.setCompany(null);

        log.debug("Request to save Company");
        Company updatedCompany = companyRepository.save(company);

        log.debug("Request to save employee");
        employeeRepository.save(employee);

        return companyMapper.toDto(updatedCompany);
    }

    public Optional<Company> findUsersCompany(Employee currentEmployee) {
        return companyRepository.findOneByEmployees(Collections.singleton(currentEmployee));
    }

    public void notifyEmployeeThatTheyHaveBeenFired(Company company, String referencedUserEmail) {

        company.getEmployees().stream().forEach(employee -> {
            employeeService.saveEmployee(
                this.sendNotificationToEmployee(
                    employee,
                    employee.getEmail(),
                    company.getId(),
                    NotificationType.FIRED,
                    "The company " + company.getName() + " has been struck off. You are out of job."
                )
            );
        });
    }

    public void sendEmailToAllEmployees(Company company) {
        List<Employee> employees = employeeService.findAllEmployeesFromCompany(company);
        mailService.sendEmailToAllFromCompany(employees);
    }

    public Optional<Employee> findEmployeeById(Long employeeId) {
        return employeeService.findOneById(employeeId);
    }

    public Optional<Employee> findEmployeeByEmail(String email) {

    return employeeService.findOneByEmail(email);
    }

    public boolean didUserRequestedTojoinLessThen14Days(Employee approvedEmployee, NotificationType requestToJoin, Long companyId, int i) {
        return  notificationService.hasEventOccurredInThePast(approvedEmployee, requestToJoin, companyId , i);
    }

    public void sendNotificationToAllFromCompanyExceptManagerAndEmployee(Long companyId, Employee approvedEmployee, NotificationType notificationType, String subject) {
        employeeService.sendNotificationToAllFromCompanyExceptManagerAndEmployee(companyId, approvedEmployee, notificationType, subject);
    }

    public void saveEmployee(Employee approvedEmployee) {
        employeeService.saveEmployee(approvedEmployee);
    }
}
