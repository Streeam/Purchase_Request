package com.streeam.cims.web.rest;

import com.streeam.cims.CidApp;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.UserRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.mapper.EmployeeMapper;
import com.streeam.cims.web.rest.errors.ExceptionTranslator;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.streeam.cims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration tests for the {@link EmployeeResource} REST controller.
 */
@SpringBootTest(classes = CidApp.class)
public class EmployeeResourceIT {

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "employee@localhost.com";
    private static final String UPDATED_EMAIL = "P^@v.g";

    private static final Boolean DEFAULT_HIRED = false;
    private static final Boolean UPDATED_HIRED = true;

    private static final String DEFAULT_LANGUAGE = "AAAAAAAAAA";
    private static final String UPDATED_LANGUAGE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    /**
     * This repository is mocked in the com.streeam.cims.repository.search test package.
     *
     * @see com.streeam.cims.repository.search.EmployeeSearchRepositoryMockConfiguration
     */
    @Autowired
    private EmployeeSearchRepository mockEmployeeSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restEmployeeMockMvc;

    private Employee employee;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final EmployeeResource employeeResource = new EmployeeResource(employeeService);
        this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .login(DEFAULT_LOGIN)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .hired(DEFAULT_HIRED)
            .language(DEFAULT_LANGUAGE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        // Add required entity

        User user;
        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        employee.setUser(user);
        return employee;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createUpdatedEntity(EntityManager em) {
        Employee employee = new Employee()
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .hired(UPDATED_HIRED)
            .language(UPDATED_LANGUAGE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
        // Add required entity

        User user ;

        if (TestUtil.findAll(em, User.class).isEmpty()) {
            user = UserResourceIT.createEntity(em);
            em.persist(user);
            em.flush();
        } else {
            user = TestUtil.findAll(em, User.class).get(0);
        }
        employee.setUser(user);
        return employee;
    }

    static Employee createRandomEmployee(EntityManager em) {
        Employee employee = new Employee()
            .login(RandomStringUtils.randomAlphabetic(8))
            .firstName(RandomStringUtils.randomAlphabetic(8))
            .lastName(RandomStringUtils.randomAlphabetic(8))
            .email(RandomStringUtils.randomAlphabetic(8)+ "@localhost.com")
            .hired(false)
            .image(TestUtil.createByteArray(1, "1"))
            .imageContentType(RandomStringUtils.randomAlphabetic(8));
        // Add required entity
        User user = UserResourceIT.createEntity(em);
        em.persist(user);
        em.flush();
        employee.setUser(user);
        return employee;
    }

    @BeforeEach
    public void initTest() {
        employee = createEntity(em);
    }

//    @Test
//    @Transactional
//    public void createEmployee() throws Exception {
//        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
//
//        // Create the Employee
//        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);
//        restEmployeeMockMvc.perform(post("/api/employees")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
//            .andExpect(status().isCreated());
//
//        // Validate the Employee in the database
//        List<Employee> employeeList = employeeRepository.findAll();
//        assertThat(employeeList).hasSize(databaseSizeBeforeCreate + 1);
//        Employee testEmployee = employeeList.get(employeeList.size() - 1);
//        assertThat(testEmployee.getLogin()).isEqualTo(DEFAULT_LOGIN);
//        assertThat(testEmployee.getFirstName()).isEqualTo(DEFAULT_FIRST_NAME);
//        assertThat(testEmployee.getLastName()).isEqualTo(DEFAULT_LAST_NAME);
//        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_EMAIL);
//        assertThat(testEmployee.isHired()).isEqualTo(DEFAULT_HIRED);
//        assertThat(testEmployee.getLanguage()).isEqualTo(DEFAULT_LANGUAGE);
//        assertThat(testEmployee.getImage()).isEqualTo(DEFAULT_IMAGE);
//        assertThat(testEmployee.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
//
//        // Validate the Employee in Elasticsearch
//        verify(mockEmployeeSearchRepository, times(1)).save(testEmployee);
//    }

//    @Test
//    @Transactional
//    public void createEmployeeWithExistingId() throws Exception {
//        int databaseSizeBeforeCreate = employeeRepository.findAll().size();
//
//        // Create the Employee with an existing ID
//        employee.setId(1L);
//        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);
//
//        // An entity with an existing ID cannot be created, so this API call must fail
//        restEmployeeMockMvc.perform(post("/api/employees")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
//            .andExpect(status().isBadRequest());
//
//        // Validate the Employee in the database
//        List<Employee> employeeList = employeeRepository.findAll();
//        assertThat(employeeList).hasSize(databaseSizeBeforeCreate);
//
//        // Validate the Employee in Elasticsearch
//        verify(mockEmployeeSearchRepository, times(0)).save(employee);
//    }


    @Test
    @Transactional
    public void checkLoginIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the field null
        employee.setLogin(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc.perform(post("/api/employees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the field null
        employee.setEmail(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc.perform(post("/api/employees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkHiredIsRequired() throws Exception {
        int databaseSizeBeforeTest = employeeRepository.findAll().size();
        // set the field null
        employee.setHired(null);

        // Create the Employee, which fails.
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        restEmployeeMockMvc.perform(post("/api/employees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeTest);
    }

//    @Test
//    @Transactional
//    public void getAllEmployees() throws Exception {
//        // Initialize the database
//        employeeRepository.saveAndFlush(employee);
//
//        // Get all the employeeList
//        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc"))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
//            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN.toString())))
//            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME.toString())))
//            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME.toString())))
//            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL.toString())))
//            .andExpect(jsonPath("$.[*].hired").value(hasItem(DEFAULT_HIRED.booleanValue())))
//            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE.toString())))
//            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
//            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
//    }
//
//    @Test
//    @Transactional
//    public void getEmployee() throws Exception {
//        // Initialize the database
//        employeeRepository.saveAndFlush(employee);
//
//        // Get the employee
//        restEmployeeMockMvc.perform(get("/api/employees/{id}", employee.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.id").value(employee.getId().intValue()))
//            .andExpect(jsonPath("$.login").value(DEFAULT_LOGIN.toString()))
//            .andExpect(jsonPath("$.firstName").value(DEFAULT_FIRST_NAME.toString()))
//            .andExpect(jsonPath("$.lastName").value(DEFAULT_LAST_NAME.toString()))
//            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL.toString()))
//            .andExpect(jsonPath("$.hired").value(DEFAULT_HIRED.booleanValue()))
//            .andExpect(jsonPath("$.language").value(DEFAULT_LANGUAGE.toString()))
//            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
//            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
//    }

    @Test
    @Transactional
    public void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }
//
//    @Test
//    @Transactional
//    public void updateEmployee() throws Exception {
//        // Initialize the database
//
//        User user = UserResourceIT.createEntity(em);
//        user.setEmail(employee.getEmail());
//        user.setLogin(employee.getLogin());
//        User userUpdate = userRepository.saveAndFlush(user);
//        employee.setUser(userUpdate);
//
//        employeeRepository.saveAndFlush(employee);
//
//        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();
//
//        // Update the employee
//        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();
//        // Disconnect from session so that the updates on updatedEmployee are not directly saved in db
//        em.detach(updatedEmployee);
//        updatedEmployee
//            .login(UPDATED_LOGIN)
//            .firstName(UPDATED_FIRST_NAME)
//            .lastName(UPDATED_LAST_NAME)
//            .email(UPDATED_EMAIL)
//            .hired(UPDATED_HIRED)
//            .language(UPDATED_LANGUAGE)
//            .image(UPDATED_IMAGE)
//            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);
//        EmployeeDTO employeeDTO = employeeMapper.toDto(updatedEmployee);
//
//        restEmployeeMockMvc.perform(put("/api/employees")
//            .contentType(TestUtil.APPLICATION_JSON_UTF8)
//            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
//            .andExpect(status().isOk());
//
//        // Validate the Employee in the database
//        List<Employee> employeeList = employeeRepository.findAll();
//        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
//        Employee testEmployee = employeeList.get(employeeList.size() - 1);
//        assertThat(testEmployee.getLogin()).isEqualTo(UPDATED_LOGIN);
//        assertThat(testEmployee.getFirstName()).isEqualTo(UPDATED_FIRST_NAME);
//        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_LAST_NAME);
//        assertThat(testEmployee.getEmail()).isEqualTo(UPDATED_EMAIL);
//        assertThat(testEmployee.isHired()).isEqualTo(UPDATED_HIRED);
//        assertThat(testEmployee.getLanguage()).isEqualTo(UPDATED_LANGUAGE);
//        assertThat(testEmployee.getImage()).isEqualTo(UPDATED_IMAGE);
//        assertThat(testEmployee.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
//
//        // Validate the Employee in Elasticsearch
//        verify(mockEmployeeSearchRepository, times(1)).save(testEmployee);
//    }

    @Test
    @Transactional
    public void updateNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc.perform(put("/api/employees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee);
    }

//    @Test
//    @Transactional
//    public void deleteEmployee() throws Exception {
//        // Initialize the database
//        employeeRepository.saveAndFlush(employee);
//
//        int databaseSizeBeforeDelete = employeeRepository.findAll().size();
//
//        // Delete the employee
//        restEmployeeMockMvc.perform(delete("/api/employees/{id}", employee.getId())
//            .accept(TestUtil.APPLICATION_JSON_UTF8))
//            .andExpect(status().isNoContent());
//
//        // Validate the database contains one less item
//        List<Employee> employeeList = employeeRepository.findAll();
//        assertThat(employeeList).hasSize(databaseSizeBeforeDelete - 1);
//
//        // Validate the Employee in Elasticsearch
//        verify(mockEmployeeSearchRepository, times(1)).deleteById(employee.getId());
//    }
//
//    @Test
//    @Transactional
//    public void searchEmployee() throws Exception {
//        // Initialize the database
//        employeeRepository.saveAndFlush(employee);
//        when(mockEmployeeSearchRepository.search(queryStringQuery("id:" + employee.getId()), PageRequest.of(0, 20)))
//            .thenReturn(new PageImpl<>(Collections.singletonList(employee), PageRequest.of(0, 1), 1));
//        // Search the employee
//        restEmployeeMockMvc.perform(get("/api/_search/employees?query=id:" + employee.getId()))
//            .andExpect(status().isOk())
//            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
//            .andExpect(jsonPath("$.[*].id").value(hasItem(employee.getId().intValue())))
//            .andExpect(jsonPath("$.[*].login").value(hasItem(DEFAULT_LOGIN)))
//            .andExpect(jsonPath("$.[*].firstName").value(hasItem(DEFAULT_FIRST_NAME)))
//            .andExpect(jsonPath("$.[*].lastName").value(hasItem(DEFAULT_LAST_NAME)))
//            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
//            .andExpect(jsonPath("$.[*].hired").value(hasItem(DEFAULT_HIRED.booleanValue())))
//            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_LANGUAGE)))
//            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
//            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
//    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Employee.class);
        Employee employee1 = new Employee();
        employee1.setId(1L);
        Employee employee2 = new Employee();
        employee2.setId(employee1.getId());
        assertThat(employee1).isEqualTo(employee2);
        employee2.setId(2L);
        assertThat(employee1).isNotEqualTo(employee2);
        employee1.setId(null);
        assertThat(employee1).isNotEqualTo(employee2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EmployeeDTO.class);
        EmployeeDTO employeeDTO1 = new EmployeeDTO();
        employeeDTO1.setId(1L);
        EmployeeDTO employeeDTO2 = new EmployeeDTO();
        assertThat(employeeDTO1).isNotEqualTo(employeeDTO2);
        employeeDTO2.setId(employeeDTO1.getId());
        assertThat(employeeDTO1).isEqualTo(employeeDTO2);
        employeeDTO2.setId(2L);
        assertThat(employeeDTO1).isNotEqualTo(employeeDTO2);
        employeeDTO1.setId(null);
        assertThat(employeeDTO1).isNotEqualTo(employeeDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(employeeMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(employeeMapper.fromId(null)).isNull();
    }

}
