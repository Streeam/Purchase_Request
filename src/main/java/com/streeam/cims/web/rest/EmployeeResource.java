package com.streeam.cims.web.rest;

import com.streeam.cims.domain.User;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.elasticsearch.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final String ENTITY_NAME = "employee";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EmployeeService employeeService;

    public EmployeeResource(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * {@code POST  /employees} : employees are created only when a user is created.
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
        if ( employeeId == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if(employeeService.findOne(employeeId).isPresent()){

        }

        User linkedUser = employeeService.findLinkedUserByLogin(employeeDTO.getLogin()).orElseThrow(()->new ResourceNotFoundException("No user linked to employee " + employeeDTO.getEmail()));

        EmployeeDTO result = employeeService.save(employeeDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, employeeDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /employees} : get all the employees.
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
        log.debug("REST request to delete Employee : {}", id);
        employeeService.delete(id);
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

}
