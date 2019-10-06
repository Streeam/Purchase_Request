package com.streeam.cims.web.rest;

import com.streeam.cims.domain.Authority;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.security.SecurityUtils;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.util.Validator;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static com.streeam.cims.domain.enumeration.NotificationType.*;
import static com.streeam.cims.security.AuthoritiesConstants.*;

/**
 * REST controller for managing {@link com.streeam.cims.domain.Employee}.
 */
@RestController
@RequestMapping("/api")
public class EmployeeResource {

    private final Logger log = LoggerFactory.getLogger(EmployeeResource.class);


    @Autowired
    private MailService mailService;

    @Autowired
    private AuthorityRepository authorityRepository;

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
        throw new BadRequestAlertException("It is forbidden to create a employee from this endpoint", ENTITY_NAME, "endpointdisabled");
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
        Long employeeId = employeeDTO.getId();
        EmployeeDTO result;

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new BadRequestAlertException("No User currently logged in", ENTITY_NAME, "nouserloggedin"));

        if (employeeId == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "employeenotfound");
        }

        Employee employeeToModify = employeeService.findOneById(employeeId).orElseThrow(() -> new BadRequestAlertException("Employee not found.", ENTITY_NAME, "emailexists"));

        if (!employeeToModify.getEmail().equalsIgnoreCase(employeeDTO.getEmail())) {
            throw new BadRequestAlertException("You cannot update your email.", ENTITY_NAME, "emailcannotbemodified");
        }

        User linkedUser = employeeService.findUserByEmail(employeeToModify.getEmail()).orElseThrow(() -> new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserlinkedtoemployee"));

        if (!employeeToModify.isHired().equals(employeeDTO.getHired())) {
            throw new BadRequestAlertException("You cannot update the hire value.", ENTITY_NAME, "hirecannotbemodified");
        }

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new BadRequestAlertException("No User currently logged in", ENTITY_NAME, "nouserloggedin"));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() -> new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        boolean currentUserIsNOTAdminOrManager = !employeeService.hasCurrentUserRoles(currentUser, MANAGER, ADMIN);
        // Scenario when a employee is trying to modify the details of another  employee and he is not a manager nor an admin.
        if (!linkedUser.getEmail().equalsIgnoreCase(currentUser.getEmail()) && currentUserIsNOTAdminOrManager) {
            throw new BadRequestAlertException("Modifying the details of another employee is forbidden.", ENTITY_NAME, "changejustyouraccount");
        }

        // The scenario where the user is the manager. He is only allowed to modify his details or the details of the employees from his company.
        if (employeeService.hasCurrentUserRoles(currentUser, MANAGER)) {
            Company currentCompany = employeeService.findEmployeesCompany(currentEmployee)
                .orElseThrow(() -> new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

            boolean isEmployeeInTheCompany = currentCompany.getEmployees().stream()
                .map(Employee::getEmail)
                .anyMatch(email -> email.equalsIgnoreCase(employeeDTO.getEmail()));
            if (!isEmployeeInTheCompany) {
                throw new BadRequestAlertException("You cannot modify the details of employees from other companies then your own.", ENTITY_NAME, "noupdatestoemployeesoutsidethecompany");
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
        Page<EmployeeDTO> page = employeeService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /employees/company-employees} : get all the employees. If the user is admin he can see all employees, otherwise only see employees from your company
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/company-employees")
    public ResponseEntity<List<EmployeeDTO>> getAllCompanyEmployees(Pageable pageable) {
        log.debug("REST request to get a page of Employees");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() -> new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        Company currentCompany = employeeService.findEmployeesCompany(currentEmployee)
                .orElseThrow(() -> new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));
        Page<EmployeeDTO>  page = employeeService.findCompanysEmployees(pageable, currentCompany.getId());


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /companies} : get all the unemployed Employees.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of employees in body.
     */
    @GetMapping("/employees/unemployed")
    public ResponseEntity<List<EmployeeDTO>> getAllUnemployed(Pageable pageable) {
        log.debug("REST request to get a page of all unemployed Employees");

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));

        if (!employeeService.checkUserHasRoles(currentUser, ADMIN, MANAGER)) {
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }

        Page<EmployeeDTO> page = employeeService.findAllUnemployedEmployees(pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code POST  employees/invite-to-join/{email}  : invite the "email" employee.
     *
     * @param email the email of the user to invite
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     */
    @PostMapping("employees/invite-to-join/{email}")
    public ResponseEntity inviteToJoin(@PathVariable String email) {

        if (email == null) {
            throw new BadRequestAlertException("Invalid user email", ENTITY_NAME, "emailnull");
        }

        if (Validator.validateEmail(email)) {
            throw new BadRequestAlertException("Invalid email.", ENTITY_NAME, "invalidemail");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (currentEmployee.getCompany() == null || !employeeService.findEmployeesCompany(currentEmployee).isPresent()) {
            throw new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee");
        }

        Company currentCompany = employeeService.findEmployeesCompany(currentEmployee).get();

        if (!employeeService.checkUserHasRoles(currentUser, ADMIN, MANAGER)) {
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }

        Optional<User> userToInvite = employeeService.findUserByEmail(email);

        if (userToInvite.isPresent()) { // Scenario where the user has registered.

            Employee employeeToJoin = employeeService.findOneByEmail(userToInvite.get().getEmail()).orElseThrow(() ->
                new BadRequestAlertException("No employee linked to this user " + userToInvite.get().getLogin(), ENTITY_NAME, "employeenotfound"));

            //verify that the employee hasn't rejected an invitation to join this company in the last three days;

            if (employeeService.hasEmployeeRejectedInvitationInLastThreeDays(employeeToJoin, REJECT_INVITE, currentCompany.getId(), 3)) {
                throw new BadRequestAlertException("You cannot invite a employee who has declined a previous invitation made less then three days ago.", ENTITY_NAME, "3daysbeforeyoucaninviteagain");
            }

            if (employeeToJoin.getCompany() != null) {
                throw new BadRequestAlertException("You can't invite a user who is already in a company.", ENTITY_NAME, "cantinviteuseralreadyincompany");
            }

            employeeService.sendNotificationToEmployee(employeeToJoin, currentEmployee.getEmail(), currentCompany.getId(),
                INVITATION, "You have been invited to join " + currentCompany.getName() + ".");

            mailService.sendInviteEmail(employeeToJoin.getEmail(), currentUser);
        } else {// Scenario where the user has never registered. The user doesn't yet exists so send the notification to the manager instead. Based on this notification when the user does registers he will automatically
            // have his account activated and an invite notification sent to him.

            employeeService.sendNotificationToEmployee(currentEmployee, email, currentCompany.getId(),
                INVITATION, "You have invited " + email + " to join your company.");

            mailService.sendInviteEmail(email, currentUser);
        }

        return ResponseEntity.ok().build();
    }

    /**
     * {@code GET  /employees/:id} : get the "id" employee.
     *
     * @param id the id of the employeeDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employees/{id}")
    public ResponseEntity<EmployeeDTO> getEmployee(@PathVariable Long id) {
        if (id == null) {
            throw new BadRequestAlertException("Invalid employee id", ENTITY_NAME, "idemployeenull");
        }
        log.debug("REST request to get Employee : {}", id);
        Optional<EmployeeDTO> employeeDTO = employeeService.findOne(id);
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    /**
     * {@code GET  /employees/current-employee} : get the current employee.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/employees/current-employee")
    public ResponseEntity<EmployeeDTO> getCurrentEmployee() {
        log.debug("REST request to get the current employee");
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));
        Optional<EmployeeDTO> employeeDTO = employeeService.findOne(currentUser.getEmail());
        return ResponseUtil.wrapOrNotFound(employeeDTO);
    }

    /**
     * {@code DELETE  /employees/:id} : delete the "id" employee.
     *
     * @param employeeId the id of the employeeDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/employees/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long employeeId) {

        if (employeeId == null) {
            throw new BadRequestAlertException("Invalid employee id", ENTITY_NAME, "idemployeenull");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));
        Employee employeeToDelete = employeeService.findOneById(employeeId).orElseThrow(() ->
            new BadRequestAlertException("Employee not found.", ENTITY_NAME, "employeenotfound"));


        if (!employeeService.checkUserHasRoles(currentUser, ADMIN)) {
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }
        //also deletes the linked user and updates the company if he is in one. Also delete all notification related to this employee

        User linkedUser = employeeService.findUserByEmail(employeeToDelete.getEmail()).orElseThrow(() -> new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserlinkedtoemployee"));

        log.debug("REST request to delete Employee : {}", employeeId);
        employeeService.delete(employeeId);

        log.debug("REST request to delete all Employee's Notifications.");
        employeeService.deleteEmployeesNotifications(employeeToDelete);

        log.debug("REST request to delete User : {}", linkedUser.getId());
        employeeService.deleteLinkedUser(linkedUser);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, employeeId.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/employees?query=:query} : search for the employee corresponding
     * to the query.
     *
     * @param query    the query of the employee search.
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
    @PostMapping("/employees/request-to-join/{companyId}")
    public ResponseEntity<Void> requestToJoinCompany(@PathVariable Long companyId) {
        log.debug("REST request to join the company : {}", companyId);

        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }

        Company company = employeeService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        if (employeeService.checkUserHasRoles(currentUser, EMPLOYEE, MANAGER) && Objects.nonNull(currentEmployee.getCompany())) {
            throw new BadRequestAlertException("You cannot request to join a company if you are already into one.", ENTITY_NAME, "joinonlyifunemployed");
        }

        if (employeeService.userRequestedToJoinCompanyAndWasRejectedLessThen3DaysAgo(currentEmployee, REJECT_REQUEST, companyId, 3)) {
            throw new BadRequestAlertException("You have already requested to join this company less then three days ago.", ENTITY_NAME, "3daysbeforeyoucanrequestagain");
        }

        Employee manager = employeeService.getCompanyManager(company).orElseThrow(() ->
            new BadRequestAlertException("No user with the role of manager found at this company.", ENTITY_NAME, "nomanager"));

        mailService.sendRequestToJoinEmail(manager.getEmail(), currentUser);

        employeeService.sendNotificationToEmployee(currentEmployee, manager.getEmail(), companyId, REQUEST_TO_JOIN, "You have just submitted an request to join the company " + company.getName());
        employeeService.sendNotificationToEmployee(manager, currentEmployee.getEmail(), companyId, REQUEST_TO_JOIN, "A user submitted a request to join your company " + company.getName());

        return ResponseEntity.ok().build();
    }

    /**
     * {@code POST  /employees/decline-request/:companyId} : Employee rejects a company's invitation
     *@param companyId  the id of the companyDTO to to which the user wants to  join.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/employees/decline-request/{companyId}")
    public ResponseEntity<Void> declineToJoinCompany(@PathVariable Long companyId) {
        log.debug("REST where a employee rejects a company's invitation : {}", companyId);

        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }
        Company company = employeeService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        if (employeeService.checkUserHasRoles(currentUser, EMPLOYEE, MANAGER) && Objects.nonNull(currentEmployee.getCompany())) {
            throw new BadRequestAlertException("You cannot request to join a company if you are already into one.", ENTITY_NAME, "joinonlyifunemployed");
        }

        Employee manager = employeeService.getCompanyManager(company).orElseThrow(() ->
            new BadRequestAlertException("No user with the role of manager found at this company.", ENTITY_NAME, "nomanager"));

        boolean afterTwoWeeksAgo = employeeService.companyInvitedUserToJoinLessThen14DaysAgo(currentEmployee, INVITATION, companyId, 14);

        if(!afterTwoWeeksAgo){
            throw new BadRequestAlertException("The user cannot reject a company's invitation if no invitations was sent or they haven't been sent bet before 14 gays ago.", ENTITY_NAME, "hasinvitationlessthen14days");
        }

        mailService.sendEmployeeDeclineEmail(manager.getEmail(), currentUser);

        employeeService.sendNotificationToEmployee(currentEmployee, manager.getEmail(), companyId, REJECT_INVITE, "You have declined an invitation from " + company.getName());

        employeeService.sendNotificationToEmployee(manager, currentEmployee.getEmail(), companyId, REJECT_INVITE, "A user has declined the invitation to join your company " + company.getName());

        return ResponseEntity.ok().build();
    }


    /**
     * {@code POST  /employees/{employeeId}/approve-request/{companyId} : employee accepts a company's invitation to join.
     *
     * @param companyId the company's id.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/employees/accept-invitation/{companyId}")
    public ResponseEntity<CompanyDTO> acceptCompanysInvitation(@PathVariable Long companyId) {

        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (employeeService.checkUserHasRoles(currentUser, EMPLOYEE, MANAGER) && Objects.nonNull(currentEmployee.getCompany())) {
            throw new BadRequestAlertException("You cannot request to join a company if you are already into one.", ENTITY_NAME, "joinonlyifunemployed");
        }

        Company companyWhereEmployeeHasJoined = employeeService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        boolean afterTwoWeeksAgo = employeeService.companyInvitedUserToJoinLessThen14DaysAgo(currentEmployee, INVITATION, companyId, 14);

        if(!afterTwoWeeksAgo){
            throw new BadRequestAlertException("The user cannot join the company because the company hasn't sent him an invitation in the last 14 days.", ENTITY_NAME, "hasinvitationlessthen14days");
        }

        Set<Authority> authorities = currentUser.getAuthorities();
        authorityRepository.findById(EMPLOYEE).ifPresent(authorities::add);
        currentUser.setAuthorities(authorities);

        CompanyDTO companyDTO = employeeService.saveUserEmployeeAndCompany(currentEmployee, currentUser, companyWhereEmployeeHasJoined);

        Employee manager = employeeService.getCompanyManager(companyWhereEmployeeHasJoined).orElseThrow(() ->
            new BadRequestAlertException("No user with the role of manager found at this company.", ENTITY_NAME, "nomanager"));

        mailService.sendAcceptInvitationEmail(manager.getEmail(), currentUser);
        // Send notification to the company's manager to inform him that the user accepted the invitation
        employeeService.sendNotificationToEmployee(manager, currentUser.getEmail(), companyId, ACCEPT_REQUEST,
            currentUser.getLogin() + " has accepted your invitation to join your company!");
        // Send notification to the user to welcome him to the company
        employeeService.sendNotificationToEmployee(currentEmployee, manager.getEmail(), companyId, NotificationType.WELCOME,
            "Welcome to " + companyWhereEmployeeHasJoined.getName() +"!");
        // Send notification to all (except the manager and the current user) company's employees to inform them of a new employee joining the company.
        employeeService.sendNotificationToAllFromCompanyExceptManagerAndCurrentEmployee(
            companyWhereEmployeeHasJoined.getId(),
            currentEmployee,
            NEW_EMPLOYEE,
            currentUser.getFirstName() + " " + currentUser.getLastName() + " has joined our company!");

        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyId.toString()))
            .body(companyDTO);
    }

}
