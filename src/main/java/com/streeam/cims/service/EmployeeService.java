package com.streeam.cims.service;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.dto.UserDTO;
import com.streeam.cims.service.mapper.EmployeeMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper,
                           EmployeeSearchRepository employeeSearchRepository) {

        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.employeeSearchRepository = employeeSearchRepository;
    }

    /**
     * Save a employee.
     *
     * @param employeeDTO the entity to save.
     * @return the persisted entity.
     */
    public EmployeeDTO save(EmployeeDTO employeeDTO) {
        log.debug("Request to save Employee : {}", employeeDTO);
        Employee employee = employeeMapper.toEntity(employeeDTO);
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
        employee.company(company);
        Employee updatedEmployee = employeeRepository.save(employee);
        employeeSearchRepository.save(updatedEmployee);
        EmployeeDTO result = employeeMapper.toDto(updatedEmployee);
        return result;
    }

    public Optional<User> findLinkedUserByLogin(String login) {
        return  userService.findOneByLogin(login);
    }

    public UserDTO mapEmployeeDTOToUser(User linkedUser, EmployeeDTO employeeDTO) {

        linkedUser.setLogin(employeeDTO.getLogin());
        linkedUser.setFirstName(employeeDTO.getFirstName());
        linkedUser.setLastName(employeeDTO.getLastName());
        linkedUser.setLangKey(employeeDTO.getLanguage());
        return  userService.save(linkedUser);
    }
}
