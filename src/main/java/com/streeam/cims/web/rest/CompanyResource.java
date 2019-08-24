package com.streeam.cims.web.rest;

import com.streeam.cims.domain.Authority;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.security.SecurityUtils;
import com.streeam.cims.service.CompanyService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * REST controller for managing {@link com.streeam.cims.domain.Company}.
 */
@RestController
@RequestMapping("/api")
public class CompanyResource {

    private final Logger log = LoggerFactory.getLogger(CompanyResource.class);

    private static final String ENTITY_NAME = "company";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;


    private final CompanyService companyService;

    private final MailService mailService;

    @Autowired
    private CompanyMapper companyMapper;

    private final AuthorityRepository authorityRepository;

    public CompanyResource(CompanyService companyService, AuthorityRepository authorityRepository, MailService mailService) {
        this.companyService = companyService;
        this.authorityRepository = authorityRepository;
        this.mailService = mailService;
    }

    /**
     * {@code POST  /companies} : Create a new company.
     *
     * @param companyDTO the companyDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new companyDTO, or with status {@code 400 (Bad Request)} if the company has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/companies")
    public ResponseEntity<CompanyDTO> createCompany(@Valid @RequestBody CompanyDTO companyDTO) throws URISyntaxException {
        log.debug("REST request to save Company : {}", companyDTO);
        if (companyDTO.getId() != null) {
            throw new BadRequestAlertException("A new company cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (companyService.companyEmailAlreadyExists(companyDTO.getEmail())) {
            throw new BadRequestAlertException("This company email is already being used", ENTITY_NAME, "emailexists");
        }
        if (companyService.companyNameAlreadyExists(companyDTO.getName())) {
            throw new BadRequestAlertException("This company name is already being used", ENTITY_NAME, "emailexists");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User user = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee employee = companyService.findEmployeeFromUser(user).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (companyService.checkUserHasRoles(user, AuthoritiesConstants.EMPLOYEE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN, AuthoritiesConstants.ANONYMOUS)) {
            throw new BadRequestAlertException("You can't create a company if you already have a company or are employed by another", ENTITY_NAME, "wrongroleforcreatingcompany");
        }

        Set<Authority> authorities = user.getAuthorities();
        authorityRepository.findById(AuthoritiesConstants.MANAGER).ifPresent(authorities::add);
        user.setAuthorities(authorities);
        employee.setUser(user);

        Company company = companyMapper.toEntity(companyDTO);

        CompanyDTO result = companyService.saveUserEmployeeAndComapany(employee, user, company);

        return ResponseEntity.created(new URI("/api/companies/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /companies} : Updates an existing company.
     *
     * @param companyDTO the companyDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated companyDTO,
     * or with status {@code 400 (Bad Request)} if the companyDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the companyDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/companies")
    public ResponseEntity<CompanyDTO> updateCompany(@Valid @RequestBody CompanyDTO companyDTO) throws URISyntaxException {
        log.debug("REST request to update Company : {}", companyDTO);
        CompanyDTO result;

        if (companyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        Optional<Company> companyToModify = companyService.findCompanyById(companyDTO.getId());
        if (!companyToModify.isPresent()) {
            throw new BadRequestAlertException("No company found with the id: " + companyDTO.getId(), ENTITY_NAME, "nocompanyfound");
        }


        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = companyService.findEmployeeFromUser(currentUser).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));
        Company currentCompany = companyService.findUsersCompany(currentEmployee).orElseThrow(() -> new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee"));

        if (companyDTO.getEmployees() != null) {
            throw new BadRequestAlertException("Editing the employees from this endpoint is forbidden. Leave the employee list empty and try again.", ENTITY_NAME, "cantmodifyemployees");
        }

        if (!companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.ADMIN, AuthoritiesConstants.MANAGER)) {
            throw new BadRequestAlertException("You don't have the authority to modify the details of the company", ENTITY_NAME, "noauthoritytochangecomp");
        }

        if (companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {

            if (!currentCompany.getId().equals(companyDTO.getId())) {
                throw new BadRequestAlertException("The manager doesn't have the authority to update other companies, only his own.", ENTITY_NAME, "managercanonlyupdatehisowncompany");
            }
            result = companyService.save(companyDTO);
        } else {
            result = companyService.save(companyDTO);
        }


        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /companies} : get all the companies.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of companies in body.
     */
    @GetMapping("/companies")
    public ResponseEntity<List<CompanyDTO>> getAllCompanies(Pageable pageable) {
        log.debug("REST request to get a page of Companies");

        Page<CompanyDTO> page = new PageImpl<>(new ArrayList<>());

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User user = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        if (companyService.checkUserHasRoles(user, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER) && !companyService.checkUserHasRoles(user, AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE)) {
            page = companyService.findAll(pageable);
        }
        if (companyService.checkUserHasRoles(user, AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE)) {
            page = companyService.findCompanyWithCurrentUser(user);
        }


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /companies/:id} : get the "id" company.
     *
     * @param id the id of the companyDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the companyDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/companies/{id}")
    public ResponseEntity<CompanyDTO> getCompany(@PathVariable Long id) {
        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        log.debug("REST request to get Company : {}", id);
        Optional<CompanyDTO> companyDTO = companyService.findOne(id);
        return ResponseUtil.wrapOrNotFound(companyDTO);
    }

    /**
     * {@code DELETE  /companies/:id} : delete the "id" company.
     *
     * @param id the id of the companyDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {

        if (id == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        log.debug("REST request to delete Company : {}", id);
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        Company companyToDelete = companyService.findCompanyById(id).orElseThrow(() -> new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        User currentUser = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = companyService.findEmployeeFromUser(currentUser).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (!companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("You don't have the authority to delete this company", ENTITY_NAME, "companyremoveforbiden");
        }

        if (companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {

            Company currentCompany = companyService.findUsersCompany(currentEmployee).orElseThrow(() -> new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee"));

            if (!currentCompany.getId().equals(companyToDelete.getId())) {
                throw new BadRequestAlertException("The manager doesn't have the authority to delete other companies, only his own.", ENTITY_NAME, "managercanonlyremovehisowncompany");
            }
            companyService.delete(id);
            companyService.notifyEmployeeThatTheyHaveBeenFired(companyToDelete);
            companyService.sendEmailToAllEmployees(companyToDelete);
        } else {
            companyService.delete(id);
            companyService.notifyEmployeeThatTheyHaveBeenFired(companyToDelete);
        }
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, currentUser.getId().toString())).build();
    }

    /**
     * {@code SEARCH  /_search/companies?query=:query} : search for the company corresponding
     * to the query.
     *
     * @param query    the query of the company search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/companies")
    public ResponseEntity<List<CompanyDTO>> searchCompanies(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Companies for query {}", query);
        Page<CompanyDTO> page = companyService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code POST  /companies/{id}/hire-employee/{userEmail} : hire a user to the company
     *
     * @param userEmail the email of the user who wants to join the company.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/companies/{companyId}/approve-employee/{employeeId}")
    public ResponseEntity<CompanyDTO> approveEmployee(@PathVariable Long companyId, @PathVariable Long employeeId) {

        if (employeeId == null) {
            throw new BadRequestAlertException("Invalid employee id", ENTITY_NAME, "idemployeenull");
        }
        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = companyService.findEmployeeFromUser(currentUser).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (!companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("You don't have the authority to accept employee applications.", ENTITY_NAME, "companyremoveforbiden");
        }

        Employee approvedEmployee = companyService.findEmployeeById(employeeId).orElseThrow(() ->
            new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        log.debug("REST request to hire the user: {}", approvedEmployee.getLogin());

        User approvedUser = companyService.findUserByEmail(approvedEmployee.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserforthisemployee"));

        if (companyService.checkUserHasRoles(approvedUser, AuthoritiesConstants.EMPLOYEE, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN) && approvedEmployee.isHired()) {
            throw new BadRequestAlertException("This application cannot be accepted. This employee is already in a company.", ENTITY_NAME, "companyremoveforbiden");
        }
        Company companyWhereEmployeeApplied = companyService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

// Manager can only reject employees applying to his company
        if (companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {
            Company currentCompany = companyService.findUsersCompany(currentEmployee).orElseThrow(() ->
                new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee"));

            if (!currentCompany.getId().equals(companyWhereEmployeeApplied.getId())) {
                throw new BadRequestAlertException("The manager cannot approve a application by a employee who is applying to a different company then his.", ENTITY_NAME, "cannotapproveemployeeifheapplyestoadiffcompany");
            }

        }
// Admin can reject anyone's application
        Set<Authority> authorities = approvedUser.getAuthorities();
        authorityRepository.findById(AuthoritiesConstants.EMPLOYEE).ifPresent(authorities::add);
        approvedUser.setAuthorities(authorities);
        approvedEmployee.setUser(approvedUser);
        CompanyDTO companyDTO = companyService.saveUserEmployeeAndComapany(approvedEmployee, approvedUser, companyWhereEmployeeApplied);


        mailService.sendRejectionEmail(approvedEmployee.getEmail(), currentUser);
        companyService.sendNotificationToEmployee(approvedEmployee, NotificationType.ACCEPT_INVITE, "Your application to join " + companyWhereEmployeeApplied.getName() + " has been approved!");


        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyId.toString()))
            .body(companyDTO);
    }


    /**
     * {@code POST  /companies/{id}/reject-employee/{userEmail} : reject a user's request to join a company
     *
     * @param userEmail the email of the user who wants to join the company.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping(" /companies/{companyId}/reject-employee/{employeeId} ")
    public void rejectEmployee(@PathVariable Long companyId, @PathVariable Long employeeId) {
        log.debug("REST to reject a user's request to join a company.");

        idNotNull(companyId, employeeId);

        Company companyWhereEmployeeApplied = companyService.findCompanyById(companyId).orElseThrow(() -> new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        Employee rejectedEmployee = companyService.findEmployeeById(employeeId).orElseThrow(() ->
            new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        User userToReject = companyService.findUserByEmail(rejectedEmployee.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserforthisemployee"));

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = companyService.findEmployeeFromUser(currentUser).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        if (!companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("You don't have the authority to reject employee applications.", ENTITY_NAME, "companyremoveforbiden");
        }
// Manager can only reject employees applying to his company
        if (companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {

            Company currentCompany = companyService.findUsersCompany(currentEmployee).orElseThrow(() -> new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee"));

            if (!currentCompany.getId().equals(companyWhereEmployeeApplied.getId())) {
                throw new BadRequestAlertException("The manager cannot reject a application by a employee who is applying to a different company then his.", ENTITY_NAME, "cannotrejectemployeeifheapplyestoadiffcompany");
            }
            mailService.sendRejectionEmail(rejectedEmployee.getEmail(), currentUser);
            companyService.sendNotificationToEmployee(rejectedEmployee, NotificationType.REJECT_INVITE, "Your application to join " + companyWhereEmployeeApplied.getName() + " has been rejected!");

        }
// Admin can reject anyone's application
        else {
            mailService.sendRejectionEmail(rejectedEmployee.getEmail(), currentUser);
            companyService.sendNotificationToEmployee(rejectedEmployee, NotificationType.REJECT_INVITE, "Your application to join " + companyWhereEmployeeApplied.getName() + " has been rejected!");
        }

    }

    private void idNotNull(@PathVariable Long companyId, @PathVariable Long employeeId) {
        if (employeeId == null) {
            throw new BadRequestAlertException("Invalid employee id", ENTITY_NAME, "idemployeenull");
        }
        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }
    }

    /**
     * {@code POST  /companies/{companyId}/fire/{employeeId} : fire a employee      *
     *
     * @param The companyId of the current company. EmployeeId of the employee to be fired
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the company, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/companies/{companyId}/fire/{employeeId}")
    public ResponseEntity<CompanyDTO> fireEmployee(@PathVariable Long companyId, @PathVariable Long employeeId) {

        if (employeeId == null) {
            throw new BadRequestAlertException("Invalid employee id", ENTITY_NAME, "idemployeenull");
        }
        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }

        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();

        User currentUser = companyService.findCurrentUser(currentUserLogin).orElseThrow(() -> new ResourceNotFoundException("No user logged in."));

        Employee currentEmployee = companyService.findEmployeeFromUser(currentUser).orElseThrow(() -> new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));


        if (!companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER, AuthoritiesConstants.ADMIN)) {
            throw new BadRequestAlertException("You don't have the authority to fire a employee.", ENTITY_NAME, "fireingemployeeforbiden");
        }

        Employee employeeToFire = companyService.findEmployeeById(employeeId).orElseThrow(() ->
            new BadRequestAlertException("No employee linked to this user", ENTITY_NAME, "userwithnoemployee"));

        log.debug("REST request to hire the user: {}", employeeToFire.getLogin());

        User userToFire = companyService.findUserByEmail(employeeToFire.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No user linked to this employee", ENTITY_NAME, "nouserforthisemployee"));


        Company companyThatEmployeeWasFiredFrom = companyService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        if (currentUser.getId().equals(userToFire.getId())) {
            new BadRequestAlertException("You cannot fire yourself.", ENTITY_NAME, "fireingyourselfisforbiden");
        }

        // Manager can only fire employees within his company
        if (companyService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {
            Company currentCompany = companyService.findUsersCompany(currentEmployee).orElseThrow(() ->
                new BadRequestAlertException("No company found with the employee.", ENTITY_NAME, "nocompanylinkedtoemployee"));


            if (!currentCompany.getId().equals(companyThatEmployeeWasFiredFrom.getId())) {
                throw new BadRequestAlertException("The manager cannot fire an employee who isn't from his company.", ENTITY_NAME, "cannotfirefromothercompany");
            }

        }
        // Admin can reject anyone's application
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        userToFire.setAuthorities(authorities);
        employeeToFire.setUser(userToFire);
        companyThatEmployeeWasFiredFrom.getEmployees().remove(employeeToFire);

        CompanyDTO companyDTO = companyService.saveUserEmployeeAndComapany(employeeToFire, userToFire, companyThatEmployeeWasFiredFrom);

        mailService.sendFiredEmail(employeeToFire.getEmail(), currentUser);
        companyService.sendNotificationToEmployee(employeeToFire, NotificationType.FIRED, "You have been fired from " + companyThatEmployeeWasFiredFrom.getName() + ".");


        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, companyId.toString()))
            .body(companyDTO);
    }





}
