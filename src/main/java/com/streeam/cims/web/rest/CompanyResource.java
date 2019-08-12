package com.streeam.cims.web.rest;

import com.streeam.cims.domain.Authority;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.CompanyService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.elasticsearch.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

        Optional<Employee> employee = Optional.empty();
        Optional<User> user = companyService.findCurrentUser();
        if(user.isPresent()){

            if(companyService.checkUserHasRoles(user.get() , AuthoritiesConstants.EMPLOYEE, AuthoritiesConstants.MANAGER,
                AuthoritiesConstants.ADMIN)){
                throw new BadRequestAlertException("You can't create a company if you already have a company or are employed by another", ENTITY_NAME, "wrongrole");
            }

            Set<Authority> authorities = user.get().getAuthorities();
            authorityRepository.findById(AuthoritiesConstants.MANAGER).ifPresent(authorities::add);
            user.get().setAuthorities(authorities);
            employee =  companyService.findEmployeeFromUser(user.get());
        }

        CompanyDTO result = companyService.saveWithEmployee(companyDTO,employee);

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
        if (companyDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }

        Optional<Employee> employee = Optional.empty();
        Optional<User> user = companyService.findCurrentUser();
        if(user.isPresent()){
            employee =  companyService.findEmployeeFromUser(user.get());
        }

        CompanyDTO result = companyService.saveWithEmployee(companyDTO, employee);
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

        Optional<User> user = companyService.findCurrentUser();
        if(!user.isPresent()){
            throw new ResourceNotFoundException("No user logged in.");
        }

        if(companyService.checkUserHasRoles(user.get(), AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)){
            page = companyService.findAll(pageable);
        }
        if (companyService.checkUserHasRoles(user.get(),AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE)){
            page = companyService.findCompanyWithCurrentUser(user.get());
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
        Optional<User> user = companyService.findCurrentUser();

        if(user.isPresent() && !companyService.checkUserHasRoles(user.get() , AuthoritiesConstants.MANAGER,
            AuthoritiesConstants.ADMIN)){
            throw new BadRequestAlertException("You don't have the authority to delete this company", ENTITY_NAME, "companyremoveforbiden");
        }

        log.debug("REST request to delete Company : {}", id);
        companyService.delete(id);

        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
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
     * {@code POST  /companies/:id/request-to-join} : request to join a company/companyID
     *
     * @param companyId the id of the companyDTO to to which the user wants to join.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("companies/{companyId}/request-to-join")
    public ResponseEntity<CompanyDTO> requestToJoinCompany(@PathVariable Long companyId) {
        log.debug("REST request to request to join the company : {}", companyId);
        // get the user (email and login)
        Optional<User> user = companyService.findCurrentUser();
        if (!user.isPresent()) {
            throw new ResourceNotFoundException("No user logged in.");
        }
        if(companyService.checkUserHasRoles(user.get(), AuthoritiesConstants.MANAGER,AuthoritiesConstants.EMPLOYEE)){
            throw new BadRequestAlertException("You don't have the eligible to request to join a company", ENTITY_NAME, "requesttojoinforbiden");
        }
        String managersEmail = companyService.getCompaniesManagerEmail(companyId);
        if("No company with this id found.".equals(managersEmail)){
            throw new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid");
        }
        if("No user with the role of manager found at this company.".equals(managersEmail)){
            throw new BadRequestAlertException("No user with the role of manager found at this company.", ENTITY_NAME, "requesttojoinforbiden");
        }

        mailService.sendRequestToJoinEmail(user.get());
        // send an email to the manager to inform him of a employee wanting to join the company

        // create a Notification(REQUEST_TO_JOIN) and link it to the manager

        return ResponseUtil.wrapOrNotFound(null);
    }

    /**
     * {@code POST  /companies/{id}/hire-employee/{userEmail} : hire a user to the company
     *
     * @param userEmail the email of the user who wants to join the company.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/companies/{id}/hire-employee/{userEmail}")
    public ResponseEntity<CompanyDTO> hireEmployee(@PathVariable String userEmail) {
        log.debug("REST request to hire the user: {}", userEmail);


        return ResponseUtil.wrapOrNotFound(null);
    }

    /**
     * {@code POST  /companies/{id}/reject-employee/{userEmail} : reject a user's request to join a company
     *
     * @param userEmail the email of the user who wants to join the company.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the employeeDTO, or with status {@code 404 (Not Found)}.
     */
    @PostMapping("/companies/{id}/reject-employee/{userEmail}")
    public ResponseEntity<CompanyDTO> rejectEmployee(@PathVariable String userEmail) {
        log.debug("REST to reject a user's request to join a company.");


        return ResponseUtil.wrapOrNotFound(null);
    }
}
