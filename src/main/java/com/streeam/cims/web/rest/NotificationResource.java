package com.streeam.cims.web.rest;

import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.security.SecurityUtils;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.NotificationService;
import com.streeam.cims.service.dto.NotificationDTO;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

import static com.streeam.cims.security.AuthoritiesConstants.ADMIN;
import static com.streeam.cims.security.AuthoritiesConstants.MANAGER;

/**
 * REST controller for managing {@link com.streeam.cims.domain.Notification}.
 */
@RestController
@RequestMapping("/api")
public class NotificationResource {

    private final Logger log = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "notification";

    @Autowired
    private EmployeeService employeeService;

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;

    public NotificationResource(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/notifications")
    public ResponseEntity<NotificationDTO> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) throws URISyntaxException {
        log.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity.created(new URI("/api/notifications/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /notifications} : Updates an existing notification.
     *
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/notifications")
    public ResponseEntity<NotificationDTO> updateNotification(@Valid @RequestBody NotificationDTO notificationDTO) throws URISyntaxException {
        log.debug("REST request to update Notification : {}", notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        NotificationDTO result = notificationService.save(notificationDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, notificationDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications")
    public ResponseEntity<List<NotificationDTO>> getAllNotifications(Pageable pageable) {
        log.debug("REST request to get a page of Notifications");
        Page<NotificationDTO> page = notificationService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *

     * @param pageable the pagination information.

     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications/all")
    public ResponseEntity<List<NotificationDTO>> getAllNotificationsWithoutPagination() {
        log.debug("REST request to get all Notifications");
        List<NotificationDTO> list = notificationService.findAllNotifiations();
        return ResponseEntity.ok().body(list);
    }

    /**
     * {@code GET  /notifications/current} : get all the employee notifications.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications/current")
    public ResponseEntity<List<NotificationDTO>> getCurrentNotifications() {
        log.debug("REST request to get all employee notifications");
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        List<NotificationDTO> notifications = notificationService.findAllByEmployee(currentEmployee);
        return ResponseEntity.ok().body(notifications);
    }

    /**
     * {@code GET  /notifications/current} : get all the company notifications.
     * @param companyId the id of company.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/notifications/company/{companyId}")
    public ResponseEntity<List<NotificationDTO>> getCompanyNotifications(@PathVariable Long companyId) {
        if (companyId == null) {
            throw new BadRequestAlertException("Invalid company id", ENTITY_NAME, "idcompanynull");
        }

        log.debug("REST request to get all employee notifications");
        String currentUserLogin = SecurityUtils.getCurrentUserLogin().get();
        User currentUser = employeeService.findCurrentUser(currentUserLogin).orElseThrow(() ->
            new ResourceNotFoundException("No user logged in."));
        Employee currentEmployee = employeeService.findOneByEmail(currentUser.getEmail()).orElseThrow(() ->
            new BadRequestAlertException("No Employee currently logged in", ENTITY_NAME, "noemployeeloggedin"));

        if (!employeeService.checkUserHasRoles(currentUser, ADMIN, MANAGER)) {
            throw new BadRequestAlertException("You don't have the authority to access this endpoint.", ENTITY_NAME, "accessrestricted");
        }
        Company company = employeeService.findCompanyById(companyId).orElseThrow(() ->
            new BadRequestAlertException("No company with this id found.", ENTITY_NAME, "nocompwithid"));

        if (employeeService.checkUserHasRoles(currentUser, AuthoritiesConstants.MANAGER)) {

            if (!company.getId().equals(currentEmployee.getCompany().getId())) {
                throw new BadRequestAlertException("The manager can only access notifications sent to his company.", ENTITY_NAME, "canonlyaccesscompanynotifications");
            }

        }

        List<NotificationDTO> notifications = notificationService.findAllByCompanyId(companyId);
        return ResponseEntity.ok().body(notifications);
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/notifications/{id}")
    public ResponseEntity<NotificationDTO> getNotification(@PathVariable Long id) {
        log.debug("REST request to get Notification : {}", id);
        Optional<NotificationDTO> notificationDTO = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/notifications/{id}")
    public ResponseEntity<Void> deleteNotification(@PathVariable Long id) {
        log.debug("REST request to delete Notification : {}", id);
        notificationService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build();
    }

    /**
     * {@code SEARCH  /_search/notifications?query=:query} : search for the notification corresponding
     * to the query.
     *
     * @param query the query of the notification search.
     * @param pageable the pagination information.
     * @return the result of the search.
     */
    @GetMapping("/_search/notifications")
    public ResponseEntity<List<NotificationDTO>> searchNotifications(@RequestParam String query, Pageable pageable) {
        log.debug("REST request to search for a page of Notifications for query {}", query);
        Page<NotificationDTO> page = notificationService.search(query, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

}
