package com.streeam.cims.web.rest;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.security.SecurityUtils;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.elasticsearch.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.streeam.cims.domain.Employee}.
 */
@RestController
@RequestMapping("/api")
public class EmployeeResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeResource.class);

    @Autowired
    private  MailService mailService;

    private static final String ENTITY_NAME = "employee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * {@code POST  /employees} : This endpoint is disabled. A employee is created only when a user registers and he's identity is confirmed.
     *
     * @param employeeDTO the employeeDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new employeeDTO, or with status {@code 400 (Bad Request)} if the employee has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/employees")
    public void createEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
//        log.debug("REST request to save Employee : {}", employeeDTO);
//        if (employeeDTO.getId() != null) {
//            throw new BadRequestAlertException("A new employee cannot already have an ID", ENTITY_NAME, "idexists");
//        }
//        EmployeeDTO result = employeeService.save(employeeDTO);
//        return ResponseEntity.created(new URI("/api/employees/" + result.getId()))
//            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
//            .body(result);
    }

    /**
     * {@code PUT  /employees} : Updates an existing employee and also update the linked user.
     *
     * @param employeeDTO the employeeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated employeeDTO,
     * or with status {@code 400 (Bad Request)} if the employeeDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the employeeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/employees")
    public ResponseEntity<EmployeeDTO> updateEmployee(@Valid @RequestBody EmployeeDTO employeeDTO) throws URISyntaxException {
        log.debug("REST request to update Employee : {}", employeeDTO);
        Long employeeId =  employeeDTO.getId();
        EmployeeDTO result;

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(()->new BadRequestAlertException("No User currently logged in", ENTITY_NAME, "nouserloggedin"));

        if ( employeeId == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "employeenotfound");
        }

        Employee employeeToModify =  employeeService.findOneById(employeeId).orElseThrow(()->new BadRequestAlertException("Employee not found.", ENTITY_NAME, "emailexists"));

        if(!employeeToModify.getEmail().equalsIgnoreCase(employeeDTO.getEmail())){
            throw new BadRequestAlertException("You cannot update your email.", ENTITY_NAME, "emailcannotbemodified");
        }

        User linkedUser = employeeService.findLinkedUserByEmail(employeeToModify.getEmail()).orElseThrow(()->new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserlinkedtoemployee"));

        if(!employeeToModify.isHired().equals(employeeDTO.isHired())){
            throw new BadRequestAlertException("You cannot update the hire value.", ENTITY_NAME, "hirecannotbemodified");
        }

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(()->new BadRequestAlertException("No User currently logged in", ENTITY_NAME, "nouserloggedin"));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(()->new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        boolean currentUserIsNOTAdminOrManager = !employeeService.hasCurrentUserRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN);
        // Scenario when a employee is trying to modify the details of another  employee and he is not a manager nor an admin.
        if(!linkedUser.getEmail().equalsIgnoreCase(currentUser.getEmail()) && currentUserIsNOTAdminOrManager){
            throw new BadRequestAlertException("Modifying the details of another employee is forbidden.", ENTITY_NAME, "changejustyouraccount");
        }

        // The scenario where the user is the manager. He is only allowed to modify his details or the details of the employees from his company.
        if(employeeService.hasCurrentUserRoles(currentUser, AuthoritiesConstants.MANAGER)){
            Company currentCompany = employeeService.findEmployeesCompany(currentEmployee)
                .orElseThrow(()->new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

            boolean isEmployeeInTheCompany = currentCompany.getEmployees().stream()
                .map(Employee::getEmail)
                .anyMatch(email-> email.equalsIgnoreCase(employeeDTO.getEmail()));
            if(!isEmployeeInTheCompany){
                throw  new BadRequestAlertException("You cannot modify the details of employees from other companies then your own.", ENTITY_NAME, "noupdatestoemployeesoutsidethecompany");
            }

            result = employeeService.save(linkedUser, employeeDTO);
        }
        // The scenario where the user is the manager. He is only allowed to modify his details or the details of the employees from his company.
        else {
            result = employeeService.save(linkedUser, employeeDTO);
        }

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /employees} : get all the employees. If the user is admin he can see all employees, otherwise only see employees from your company
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeDTO>> getAllEmployees(Pageable pageable) {
        log.debug("REST request to get a page of Employees");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(()-> new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(()->new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        Page<EmployeeDTO> page ;

        if (employeeService.checkUserHasRoles(currentUser, AuthoritiesConstants.ADMIN)){
            page = employeeService.findAll(pageable);
        }
        else if(employeeService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER , AuthoritiesConstants.EMPLOYEE)) {
            Company currentCompany = employeeService.findEmployeesCompany(currentEmployee)
                .orElseThrow(()->new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));
            page = employeeService.findCompanysEmployees(pageable, currentCompany.getId());
        }
        else {
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /employees/:id} : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        log.debug("REST request to get Employee : {}", id);
        Optional<EmployeeDTO> employeeDTO = employeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    /**
     * {@code DELETE  /employees/:id} : delete the "id" employee.
     *
     * @param id the id of the employeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(()-> new ResourceNotFoundException("No user logged in."));
        Employee employeeToDelete =  employeeService.findOneById(id).orElseThrow(()->
            new BadRequestAlertException("Employee not found.", ENTITY_NAME, "employeenotfound"));


        if (!employeeService.checkUserHasRoles(currentUser, AuthoritiesConstants.ADMIN)){
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }
        //also deletes the linked user and updates the company if he is in one. Also delete all notification related to this employee

        User linkedUser = employeeService.findLinkedUserByEmail(employeeToDelete.getEmail()).orElseThrow(()->new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserlinkedtoemployee"));

        log.debug("REST request to delete Employee : {}", id);
        employeeService.delete(id);

        log.debug("REST request to delete all Employee's Notifications.");
        employeeService.deleteEmployeesNotifications(employeeToDelete);

        log.debug("REST request to delete User : {}", linkedUser.getId());
        employeeService.deleteLinkedUser(linkedUser);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/employees?query=:query} : search for the employee corresponding
     * to the query.
     *
     * @param query the query of the employee search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/employees")
    public ResponseEntity<List<EmployeeDTO>> searchEmployees(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Employees for query {}", query);
        Page<EmployeeDTO> page = employeeService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    /**
     * {@code POST  /companies/:id/request-to-join} : request to join a company/companyID
     *
     * @param companyId the id of the companyDTO to to which the user wants to  join.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("employee/{employeeId}/request-to-join/{companyId}")
    public void requestToJoinCompany(@PathVariable Long employeeId,@PathVariable Long companyId) {
        log.debug("REST request to join the company : {}", companyId);

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(()->new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        if (employeeService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE)) {
            throw new BadRequestAlertException("As a manager or a employee you are not allowed to join a company", ENTITY_NAME, "requesttojoinforbiden");
        }

        Employee employeeRequestingToJoin =  employeeService.findOneById(employeeId).orElseThrow(()->
            new BadRequestAlertException("Employee not found.", ENTITY_NAME, "employeenotfound"));


        if (!currentEmployee.getEmail().equalsIgnoreCase(employeeRequestingToJoin.getEmail())){
            throw new BadRequestAlertException("You cannot request to join a company on behalf of someone else.", ENTITY_NAME, "requestingtojoinonanothersbehalf");
        }

        //Cannot implement this method until i can find a way to link the notifications to the company

        // find the latest rejected notification. If it has been less then 48h since
//        if(!employeeService.userRequestedToJoinAndWasRejectedLessThen3DaysAgo(currentEmployee)){
//            throw new BadRequestAlertException("You have already requested to join this company less then three days ago.", ENTITY_NAME, "3daysbeforeyoucanrequestagain");
//        }



        Company company = employeeService.findCompanyById(companyId).orElseThrow(() -> new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        Employee manager = employeeService.getCompanysManager(company).orElseThrow(() -> new BadRequestAlertException("No user with the role of manager found at this company.", ENTITY_NAME, "nomanager"));


        String managersEmail = employeeService.getEmployeesEmail(manager);

        // send an email to the manager to inform him of a employee wanting to join the company
        mailService.sendRequestToJoinEmail(managersEmail, currentUser);

        // create a Notification(REQUEST_TO_JOIN) and link it to the manager
        employeeService.sendNotificationToEmployee(manager, NotificationType.REQUEST_TO_JOIN, "A user submitted a request to join your company " + company.getName());

    }

}
