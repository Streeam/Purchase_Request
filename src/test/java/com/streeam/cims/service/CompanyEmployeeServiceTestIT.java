package com.streeam.cims.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.streeam.cims.CidApp;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.Notification;
import com.streeam.cims.domain.User;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.*;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.repository.search.UserSearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.dto.CompanyDTO;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
import com.streeam.cims.web.rest.EmployeeResource;
import com.streeam.cims.web.rest.TestUtil;
import com.streeam.cims.web.rest.errors.ExceptionTranslator;
import org.apache.commons.lang3.RandomStringUtils;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.streeam.cims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for {@link CompanyService}, {@link EmployeeService}, {@link UserService}  .
 */
@SpringBootTest(classes = CidApp.class)
@Transactional
class CompanyEmployeeServiceTestIT {

    @Autowired
    private EntityManager em;

    @Autowired
    private WebApplicationContext context;

    private final Logger log = LoggerFactory.getLogger(CompanyService.class);

    //**********COMPANY DEFAULT VALUES******************

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_EMAIL = "dummy@localhost.com";
    private static final String UPDATED_COMPANY_EMAIL = "dummy2@localhost.co.uk";

    private static final String DEFAULT_PHONE = "AAAAAAAAAA";
    private static final String UPDATED_PHONE = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_1 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_1 = "BBBBBBBBBB";

    private static final String DEFAULT_ADDRESS_LINE_2 = "AAAAAAAAAA";
    private static final String UPDATED_ADDRESS_LINE_2 = "BBBBBBBBBB";

    private static final String DEFAULT_CITY = "AAAAAAAAAA";
    private static final String UPDATED_CITY = "BBBBBBBBBB";

    private static final String DEFAULT_COUNTRY = "AAAAAAAAAA";
    private static final String UPDATED_COUNTRY = "BBBBBBBBBB";

    private static final String DEFAULT_POSTCODE = "AAAAAAAAAA";
    private static final String UPDATED_POSTCODE = "BBBBBBBBBB";

    private static final byte[] DEFAULT_COMPANY_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_COMPANY_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_COMPANY_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_COMPANY_LOGO_CONTENT_TYPE = "image/png";


    private static final String DEFAULT2_NAME = "CCCCCCCCC";
    private static final String UPDATED2_NAME = "DDDDDDDDD";

    private static final String DEFAULT2_COMPANY_EMAIL = "dummy3@localhost.com";
    private static final String UPDATED2_COMPANY_EMAIL = "dummy4@localhost.co.uk";

    private static final String DEFAULT2_PHONE = "CCCCCCCCC";
    private static final String UPDATED2_PHONE = "DDDDDDDDD";

    private static final String DEFAULT2_ADDRESS_LINE_1 = "CCCCCCCCC";
    private static final String UPDATED2_ADDRESS_LINE_1 = "DDDDDDDDD";

    private static final String DEFAULT2_ADDRESS_LINE_2 = "CCCCCCCCC";
    private static final String UPDATED2_ADDRESS_LINE_2 = "DDDDDDDDD";

    private static final String DEFAULT2_CITY = "CCCCCCCCC";
    private static final String UPDATED2_CITY = "DDDDDDDDD";

    private static final String DEFAULT2_COUNTRY = "CCCCCCCCC";
    private static final String UPDATED2_COUNTRY = "DDDDDDDDD";

    private static final String DEFAULT2_POSTCODE = "CCCCCCCCC";
    private static final String UPDATED2_POSTCODE = "DDDDDDDDD";

    private static final byte[] DEFAULT2_COMPANY_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED2_COMPANY_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT2_COMPANY_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED2_COMPANY_LOGO_CONTENT_TYPE = "image/png";


    //***************EMPLOYEE DEFAULT VALUES*********
    private static final String DEFAULT_EMPLOYEE_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_LOGIN = "BBBBBBBBBB";
    private static final String DEFAULT_EMPLOYEE2_LOGIN = "CCCCCCCCC";
    private static final String UPDATED_EMPLOYEE2_LOGIN = "DDDDDDDDD";

    private static final String DEFAULT_EMPLOYEE_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_FIRST_NAME = "BBBBBBBBBB";
    private static final String DEFAULT_EMPLOYEE2_FIRST_NAME = "CCCCCCCCC";
    private static final String UPDATED_EMPLOYEE2_FIRST_NAME = "DDDDDDDDD";

    private static final String DEFAULT_EMPLOYEE_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_LAST_NAME = "BBBBBBBBBB";
    private static final String DEFAULT_EMPLOYEE2_LAST_NAME = "CCCCCCCCC";
    private static final String UPDATED_EMPLOYEE2_LAST_NAME = "DDDDDDDDD";

    private static final String DEFAULT_EMPLOYEE_EMAIL = "eeewrrwe@localhost.com";
    private static final String UPDATED_EMPLOYEE_EMAIL = "P^g@localhost.com";
    private static final String DEFAULT_EMPLOYEE2_EMAIL = "$++@localhost.com";
    private static final String UPDATED_EMPLOYEE2_EMAIL = "P^gfdg@localhost.com";

    private static final Boolean DEFAULT_HIRED = false;
    private static final Boolean UPDATED_HIRED = true;
    private static final Boolean DEFAULT2_HIRED = false;
    private static final Boolean UPDATED2_HIRED = true;

    private static final byte[] DEFAULT_EMPLOYEE_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_EMPLOYEE_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_EMPLOYEE_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_EMPLOYEE2_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_EMPLOYEE2_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_EMPLOYEE2_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_EMPLOYEE2_IMAGE_CONTENT_TYPE = "image/png";


    //***************USER DEFAULT VALUES**************
    private static final String DEFAULT_USER_LOGIN = "johndoe";
    private static final String DEFAULT_USER_EMAIL = "johndoe@localhost.com";
    private static final String DEFAULT_USER_FIRSTNAME = "john";
    private static final String DEFAULT_USER_LASTNAME = "doe";
    private static final String DEFAULT_USER_IMAGEURL = "http://placehold1.it/50x50";
    private static final String DEFAULT_USER_LANGKEY = "dummy";

    //***************USER2 DEFAULT VALUES**************
    private static final String DEFAULT_USER2_LOGIN = "legolas";
    private static final String DEFAULT_USER2_EMAIL = "aragors@localhost.com";
    private static final String DEFAULT_USER2_FIRSTNAME = "gimli";
    private static final String DEFAULT_USER2_LASTNAME = "frodo";
    private static final String DEFAULT_USER2_IMAGEURL = "http://placehold2.it/50x50";
    private static final String DEFAULT_USER2_LANGKEY = "elvish";

    //***************USER3 DEFAULT VALUES**************
    private static final String DEFAULT_USER3_LOGIN = "boromir";
    private static final String DEFAULT_USER3_EMAIL = "frode@localhost.com";
    private static final String DEFAULT_USER3_FIRSTNAME = "gandalf";
    private static final String DEFAULT_USER3_LASTNAME = "theGRey";
    private static final String DEFAULT_USER3_IMAGEURL = "http://placehold3.it/50x50";
    private static final String DEFAULT_USER3_LANGKEY = "urukay";

    //***************USER4 DEFAULT VALUES**************
    private static final String DEFAULT_USER4_LOGIN = "faramir";
    private static final String DEFAULT_USER4_EMAIL = "bilbo@localhost.com";
    private static final String DEFAULT_USER4_FIRSTNAME = "baggings";
    private static final String DEFAULT_USER4_LASTNAME = "shortFoot";
    private static final String DEFAULT_USER4_IMAGEURL = "http://placehold4.it/50x50";
    private static final String DEFAULT_USER4_LANGKEY = "spanish";

    //***************Notification1 DEFAULT VALUES**************
    private static final String NOTIFICATION1_COMMENT = "dkcnbsodvndfovbfodeyvofdybvdofvbdfo";
    private static final String NOTIFICATION2_COMMENT = "jhcbiksuygdcuvybsdvoh";
    private static final String NOTIFICATION3_COMMENT = "nvpisodvnpisnvdnvfidnfv";
    private static final String NOTIFICATION4_COMMENT = "kcvjnfdvnsoiuhbdvcbreoivneiv";

    private static final boolean NOTIFICATION1_READ = true;
    private static final boolean NOTIFICATION2_READ = false;
    private static final boolean NOTIFICATION3_READ = false;
    private static final boolean NOTIFICATION4_READ = false;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyService companyService;

    /**
     * This repository is mocked in the com.streeam.cims.repository.search test package.
     *
     * @see com.streeam.cims.repository.search.CompanySearchRepositoryMockConfiguration
     */
    @Autowired
    private CompanySearchRepository mockCompanySearchRepository;

    @Autowired
    private UserService userService;

    /**
     * This repository is mocked in the com.streeam.cims.repository.search test package.
     *
     * @see com.streeam.cims.repository.search.UserSearchRepositoryMockConfiguration
     */
    @Autowired
    private UserSearchRepository mockUserSearchRepository;

    @Autowired
    private AuditingHandler auditingHandler;

    @Mock
    private DateTimeProvider dateTimeProvider;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeMapper employeeMapper;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;


    @Autowired
    private Validator validator;

    /**
     * This repository is mocked in the com.streeam.cims.repository.search test package.
     *
     * @see com.streeam.cims.repository.search.EmployeeSearchRepositoryMockConfiguration
     */
    @Autowired
    private EmployeeSearchRepository mockEmployeeSearchRepository;

    private Employee employee1, employee2, employee3, employee4;
    private User user1, user2, user3, user4;
    private Company company, company2;
    private Notification notification1, notification2, notification3, notification4;
    private MockMvc restMockMvc;


    @BeforeEach
    void init() {

        MockitoAnnotations.initMocks(this);
        final EmployeeResource employeeResource = new EmployeeResource(employeeService);
        this.restMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();

        user1 = new User();
        user1.setLogin(DEFAULT_USER_LOGIN);
        user1.setPassword(RandomStringUtils.random(60));
        user1.setActivated(true);
        user1.setEmail(DEFAULT_USER_EMAIL);
        user1.setFirstName(DEFAULT_USER_FIRSTNAME);
        user1.setLastName(DEFAULT_USER_LASTNAME);
        user1.setImageUrl(DEFAULT_USER_IMAGEURL);
        user1.setLangKey(DEFAULT_USER_LANGKEY);
        userService.allocateAuthority(AuthoritiesConstants.USER, user1);


        user2 = new User();
        user2.setLogin(DEFAULT_USER2_LOGIN);
        user2.setPassword(RandomStringUtils.random(60));
        user2.setActivated(true);
        user2.setEmail(DEFAULT_USER2_EMAIL);
        user2.setFirstName(DEFAULT_USER2_FIRSTNAME);
        user2.setLastName(DEFAULT_USER2_LASTNAME);
        user2.setImageUrl(DEFAULT_USER2_IMAGEURL);
        user2.setLangKey(DEFAULT_USER2_LANGKEY);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);

        user3 = new User();
        user3.setLogin(DEFAULT_USER3_LOGIN);
        user3.setPassword(RandomStringUtils.random(60));
        user3.setActivated(true);
        user3.setEmail(DEFAULT_USER3_EMAIL);
        user3.setFirstName(DEFAULT_USER3_FIRSTNAME);
        user3.setLastName(DEFAULT_USER3_LASTNAME);
        user3.setImageUrl(DEFAULT_USER3_IMAGEURL);
        user3.setLangKey(DEFAULT_USER3_LANGKEY);
        userService.allocateAuthority(AuthoritiesConstants.USER, user3);


        user4 = new User();
        user4.setLogin(DEFAULT_USER4_LOGIN);
        user4.setPassword(RandomStringUtils.random(60));
        user4.setActivated(true);
        user4.setEmail(DEFAULT_USER4_EMAIL);
        user4.setFirstName(DEFAULT_USER4_FIRSTNAME);
        user4.setLastName(DEFAULT_USER4_LASTNAME);
        user4.setImageUrl(DEFAULT_USER4_IMAGEURL);
        user4.setLangKey(DEFAULT_USER4_LANGKEY);
        userService.allocateAuthority(AuthoritiesConstants.USER, user4);

        employee1 = new Employee()
            .login(DEFAULT_USER_LOGIN)
            .firstName(DEFAULT_EMPLOYEE_FIRST_NAME)
            .lastName(DEFAULT_EMPLOYEE_LAST_NAME)
            .email(DEFAULT_USER_EMAIL)
            .hired(DEFAULT_HIRED)
            .image(DEFAULT_EMPLOYEE_IMAGE)
            .imageContentType(DEFAULT_EMPLOYEE_IMAGE_CONTENT_TYPE)
            .user(user1);

        employee2 = new Employee()
            .login(DEFAULT_USER2_LOGIN)
            .firstName(UPDATED_EMPLOYEE_FIRST_NAME)
            .lastName(UPDATED_EMPLOYEE_LAST_NAME)
            .email(DEFAULT_USER2_EMAIL)
            .hired(UPDATED_HIRED)
            .image(UPDATED_EMPLOYEE_IMAGE)
            .imageContentType(UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE)
            .user(user2);

        employee3 = new Employee()
            .login(DEFAULT_USER3_LOGIN)
            .firstName(DEFAULT_EMPLOYEE2_FIRST_NAME)
            .lastName(DEFAULT_EMPLOYEE2_LAST_NAME)
            .email(DEFAULT_USER3_EMAIL)
            .hired(DEFAULT_HIRED)
            .image(DEFAULT_EMPLOYEE2_IMAGE)
            .imageContentType(DEFAULT_EMPLOYEE2_IMAGE_CONTENT_TYPE)
            .user(user3);

        employee4 = new Employee()
            .login(DEFAULT_USER4_LOGIN)
            .firstName(UPDATED_EMPLOYEE2_FIRST_NAME)
            .lastName(UPDATED_EMPLOYEE2_LAST_NAME)
            .email(DEFAULT_USER4_EMAIL)
            .hired(UPDATED_HIRED)
            .image(UPDATED_EMPLOYEE_IMAGE)
            .imageContentType(UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE)
            .user(user4);

        notification1 = new Notification()
            .comment(NOTIFICATION1_COMMENT)
            .format(NotificationType.REQUEST_TO_JOIN)
            .employee(employee1)
            .read(NOTIFICATION1_READ)
            .sentDate(Instant.now());

        notification2 = new Notification()
            .comment(NOTIFICATION2_COMMENT)
            .format(NotificationType.ACCEPT_INVITE)
            .employee(employee2)
            .read(NOTIFICATION2_READ)
            .sentDate(Instant.now());

        notification3 = new Notification()
            .comment(NOTIFICATION3_COMMENT)
            .format(NotificationType.FIRED)
            .employee(employee3)
            .read(NOTIFICATION3_READ)
            .sentDate(Instant.now());

        notification4 = new Notification()
            .comment(NOTIFICATION4_COMMENT)
            .format(NotificationType.LEFT_COMPANY)
            .employee(employee4)
            .read(NOTIFICATION4_READ)
            .sentDate(Instant.now());

        company = new Company()
            .name(DEFAULT_NAME)
            .email(DEFAULT_COMPANY_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .postcode(DEFAULT_POSTCODE)
            .companyLogo(DEFAULT_COMPANY_LOGO)
            .companyLogoContentType(DEFAULT_COMPANY_LOGO_CONTENT_TYPE)
            .addEmployees(employee1)
            .addEmployees(employee2);

        company2 = new Company()
            .name(UPDATED_NAME)
            .email(UPDATED_COMPANY_EMAIL)
            .phone(UPDATED_PHONE)
            .addressLine1(UPDATED_ADDRESS_LINE_1)
            .addressLine2(UPDATED_ADDRESS_LINE_2)
            .city(UPDATED_CITY)
            .country(UPDATED_COUNTRY)
            .postcode(UPDATED_POSTCODE)
            .companyLogo(UPDATED_COMPANY_LOGO)
            .companyLogoContentType(UPDATED_COMPANY_LOGO_CONTENT_TYPE)
            .addEmployees(employee3)
            .addEmployees(employee4);

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
        auditingHandler.setDateTimeProvider(dateTimeProvider);
    }


    @Test
    @Transactional
    void assertEmployeesEndpointsBehaveAsRequired() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);
        userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.USER, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);


        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(updatedCompany);
        employee2.setCompany(updatedCompany);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);
        employeeRepository.saveAndFlush(employee3);
        employeeRepository.saveAndFlush(employee4);

        notificationRepository.saveAndFlush(notification1);
        notificationRepository.saveAndFlush(notification2);
        notificationRepository.saveAndFlush(notification3);
        notificationRepository.saveAndFlush(notification4);

        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

/**
 * ****************** PUT api/employees ****************
 */
        assertThat(employeeRepository.findById(employee1.getId())).isPresent();
        Employee updatedEmployee = employeeRepository.findById(employee1.getId()).get();

        updatedEmployee
            .login(UPDATED_EMPLOYEE2_LOGIN)
            .firstName(UPDATED_EMPLOYEE2_FIRST_NAME)
            .lastName(UPDATED_EMPLOYEE2_LAST_NAME)
            //.email(UPDATED_EMPLOYEE2_EMAIL)
            //.hired(UPDATED2_HIRED)
            .language(DEFAULT_USER4_LANGKEY)
            .image(UPDATED_EMPLOYEE2_IMAGE)
            .imageContentType(UPDATED_EMPLOYEE2_IMAGE_CONTENT_TYPE);

        EmployeeDTO employeeDTO = employeeMapper.toDto(updatedEmployee);

        /**
         * Modifying the details of another employee is forbidden.
         */
        restMockMvc.perform(put("/api/employees")
            .with(user(user3.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.changejustyouraccount")));

        employeeDTO.setEmail("dummyemail@localhost.com");

        assertThat(employeeDTO.getEmail()).isNotEqualTo(employee1.getEmail());
        /**
         * You cannot update your email.
         */
        restMockMvc.perform(put("/api/employees")
            .with(user(user3.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.emailcannotbemodified")))
        ;

        employeeDTO.setEmail(DEFAULT_USER_EMAIL);
        employeeDTO.setHired(true);
        assertThat(employeeDTO.isHired()).isNotEqualTo(employee1.isHired());
        /**
         * You cannot update the hire value.
         */
        restMockMvc.perform(put("/api/employees")
            .with(user(user3.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hirecannotbemodified")))
        ;

        employeeDTO.setHired(false);
         /**
          *  As a manager you cannot modify the details of employees from other companies then your own.
          */
        restMockMvc.perform(put("/api/employees")
            .with(user(user4.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.noupdatestoemployeesoutsidethecompany")));

        /**
         *  Update a employee
         */
        restMockMvc.perform(put("/api/employees")
            .with(user(user1.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isOk())
        ;

        em.flush();

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);
        Employee testEmployee = employeeList.get(employeeList.size() - 4);
        assertThat(testEmployee.getLogin()).isEqualTo(UPDATED_EMPLOYEE2_LOGIN);
        assertThat(testEmployee.getFirstName()).isEqualTo(UPDATED_EMPLOYEE2_FIRST_NAME);
        assertThat(testEmployee.getLastName()).isEqualTo(UPDATED_EMPLOYEE2_LAST_NAME);
        assertThat(testEmployee.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(testEmployee.isHired()).isEqualTo(DEFAULT_HIRED);
        assertThat(testEmployee.getLanguage()).isEqualTo(DEFAULT_USER4_LANGKEY);
        assertThat(testEmployee.getImage()).isEqualTo(UPDATED_EMPLOYEE2_IMAGE);
        assertThat(testEmployee.getImageContentType()).isEqualTo(UPDATED_EMPLOYEE2_IMAGE_CONTENT_TYPE);

        // Validate the Employee in the database

        User testUser = testEmployee.getUser();
        assertThat(testUser.getLogin()).isEqualToIgnoringCase(UPDATED_EMPLOYEE2_LOGIN);
        assertThat(testUser.getFirstName()).isEqualTo(UPDATED_EMPLOYEE2_FIRST_NAME);
        assertThat(testUser.getLastName()).isEqualTo(UPDATED_EMPLOYEE2_LAST_NAME);
        assertThat(testUser.getEmail()).isEqualTo(DEFAULT_USER_EMAIL);
        assertThat(testUser.getLangKey()).isEqualTo(DEFAULT_USER4_LANGKEY);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(1)).save(testEmployee);

/**
 * ****************** GET api/employees ****************
 */
        /**
         * You don't have the authority to access this endpoint.
         */
        restMockMvc.perform(get("/api/employees?sort=id,desc")
            .with(user(user3.getLogin())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        restMockMvc.perform(get("/api/employees?sort=id,desc")
            .with(user(user1.getLogin())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].email").value(not(DEFAULT_USER4_EMAIL)))
            .andExpect(jsonPath("$.[*].email").value(not(DEFAULT_USER3_EMAIL)))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(UPDATED_EMPLOYEE2_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(UPDATED_EMPLOYEE2_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(UPDATED_EMPLOYEE2_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_USER_EMAIL)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_USER2_EMAIL)))
            .andExpect(jsonPath("$.[*].hired").value(hasItem(DEFAULT_HIRED.booleanValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_USER4_LANGKEY)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(UPDATED_EMPLOYEE2_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(UPDATED_EMPLOYEE2_IMAGE))));



/**
 * ****************** GET api/employees/{id} ****************
 */

        // Get the employee
        restMockMvc.perform(get("/api/employees/{id}", employee4.getId())
            .with(user(user1.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(employee4.getId().intValue()))
            .andExpect(jsonPath("$.login").value(DEFAULT_USER4_LOGIN))
            .andExpect(jsonPath("$.firstName").value(UPDATED_EMPLOYEE2_FIRST_NAME))
            .andExpect(jsonPath("$.lastName").value(UPDATED_EMPLOYEE2_LAST_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_USER4_EMAIL))
            .andExpect(jsonPath("$.hired").value(UPDATED_HIRED))
            .andExpect(jsonPath("$.imageContentType").value(UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(UPDATED_EMPLOYEE_IMAGE)));


/**
 * ****************** SEARCH /api/_search/employees ****************
 */


        when(mockEmployeeSearchRepository.search(queryStringQuery("id:" + testEmployee.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(testEmployee), PageRequest.of(0, 1), 1));
        // Search the employee
        restMockMvc.perform(get("/api/_search/employees?query=id:" + testEmployee.getId())
            .with(user(user1.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(testEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(UPDATED_EMPLOYEE2_LOGIN)))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(UPDATED_EMPLOYEE2_FIRST_NAME)))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(UPDATED_EMPLOYEE2_LAST_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_USER_EMAIL)))
            .andExpect(jsonPath("$.[*].hired").value(hasItem(DEFAULT_HIRED.booleanValue())))
            .andExpect(jsonPath("$.[*].language").value(hasItem(DEFAULT_USER4_LANGKEY)))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(UPDATED_EMPLOYEE2_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(UPDATED_EMPLOYEE2_IMAGE))));


/**
 * ****************** DELETE api/employees ****************
 */
        int databaseSizeBeforeDelete = employeeRepository.findAll().size();
        int databaseUsersBeforeDelete = userRepository.findAllByActivatedIsTrue().size();
        int databaseSizeEmployeesNotifications = notificationRepository.findAllByEmployee(employee4).size();

        /**
         * Delete the employee with the manager role
         */

        restMockMvc.perform(delete("/api/employees/{id}", employee4.getId())
            .with(user(user1.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        // Validate the database contains one less item
        List<Employee> employeeListAfterDelete = employeeRepository.findAll();
        List<User> allUsersAfterDelete = userRepository.findAllByActivatedIsTrue();
        List<Notification> allEmployeeNotificationsAfterDelete = notificationRepository.findAllByEmployee(employee4);

        assertThat(employeeListAfterDelete).hasSize(databaseSizeBeforeDelete);
        assertThat(allUsersAfterDelete).hasSize(databaseUsersBeforeDelete);
        assertThat(allEmployeeNotificationsAfterDelete).hasSize(databaseSizeEmployeesNotifications);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).deleteById(employee4.getId());


        /**
         * Delete the employee with the admin role
         */

        restMockMvc.perform(delete("/api/employees/{id}", employee4.getId())
            .with(user("admin"))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        employeeListAfterDelete = employeeRepository.findAll();
        allUsersAfterDelete = userRepository.findAllByActivatedIsTrue();
        allEmployeeNotificationsAfterDelete = notificationRepository.findAllByEmployee(employee4);

        assertThat(employeeListAfterDelete).hasSize(databaseSizeBeforeDelete - 1);
        assertThat(allUsersAfterDelete).hasSize(databaseUsersBeforeDelete - 1);
        assertThat(allEmployeeNotificationsAfterDelete).hasSize(databaseSizeEmployeesNotifications - 1);



        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(1)).deleteById(employee4.getId());

        notificationRepository.deleteInBatch(Arrays.asList(notification1,notification2, notification3,notification4));
        userRepository.deleteInBatch(Arrays.asList(testUser, user2,user3, user4));
        companyRepository.deleteInBatch(Arrays.asList(company,company2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2, employee3, employee4));

    }


    @Test
    @Transactional
    void assertCompanyEndpointsBehaveAsRequired() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.USER, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);


        Company updatedCompany ;
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        List<Company> initialCompanies ;
        List<Employee> initialEmployees;
        List<User> initialUsers;

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee3.setCompany(null);
        employee4.setCompany(updatedCompany2);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);
        employeeRepository.saveAndFlush(employee3);
        employeeRepository.saveAndFlush(employee4);

        notificationRepository.saveAndFlush(notification1);
        notificationRepository.saveAndFlush(notification2);
        notificationRepository.saveAndFlush(notification3);
        notificationRepository.saveAndFlush(notification4);

        int databaseEmployeesSizeBeforeUpdate = employeeRepository.findAll().size();
        int databaseUsersSizeBeforeUpdate = userRepository.findAll().size();
        int databaseCompaniesSizeBeforeUpdate = companyRepository.findAll().size();
        int databaseNotificationsSizeBeforeUpdate = notificationRepository.findAll().size();

/**
 * ****************** POST api/companies ****************
 */

        CompanyDTO companyDTO = companyMapper.toDto(company);

        restMockMvc.perform(post("/api/companies")
            .with(user(user1.getLogin().toLowerCase()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isCreated());

        initialCompanies = companyRepository.findAll();

        assertThat(initialCompanies.get(databaseCompaniesSizeBeforeUpdate)).isNotNull();

        Company testCompany = initialCompanies.get(databaseCompaniesSizeBeforeUpdate);

        assertThat(testCompany.getEmail()).isEqualToIgnoringCase(company.getEmail());

        assertThat(userRepository.findOneByEmailIgnoreCase(user1.getEmail())).isPresent();

        User testUser = userRepository.findOneByEmailIgnoreCase(user1.getEmail()).get();

        assertThat(userService.checkIfUserHasRoles(testUser, AuthoritiesConstants.MANAGER)).isTrue();

        assertThat(initialCompanies).hasSize(databaseCompaniesSizeBeforeUpdate + 1);

        // Validate the Employee in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).save(any(Company.class));
        verify(mockUserSearchRepository, times(1)).save(any(User.class));
        verify(mockEmployeeSearchRepository, times(1)).save(any(Employee.class));


/**
 * ****************** GET api/companies ****************
 */

        /**
         * The manager of a company cannot see other companies
         */
        ResultActions resultManagersCompany = restMockMvc.perform(get("/api/companies?sort=id,desc")
            .with(user(user1.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(Matchers.not(updatedCompany2.getId().intValue())));

        String managersCompanyAsString = resultManagersCompany.andReturn().getResponse().getContentAsString();
        /**
         * The User can see all companies
         */
        ResultActions resultActions = restMockMvc.perform(get("/api/companies?sort=id,desc")
            .with(user(user2.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(testCompany.getId().intValue())))
            .andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(updatedCompany2.getId().intValue()))) // The json object has 2 companies
            .andExpect(jsonPath("$.[*].name").value(Matchers.hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(Matchers.hasItem(DEFAULT_COMPANY_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(Matchers.hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(Matchers.hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(Matchers.hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(Matchers.hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].country").value(Matchers.hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].postcode").value(Matchers.hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].companyLogoContentType").value(Matchers.hasItem(DEFAULT_COMPANY_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].companyLogo").value(Matchers.hasItem(Base64Utils.encodeToString(DEFAULT_COMPANY_LOGO))));

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        DocumentContext context = JsonPath.parse(contentAsString);
        int jsonLength = context.read("$.length()");
        assertThat(jsonLength).isEqualTo(databaseCompaniesSizeBeforeUpdate + 1);

       //System.out.println(JsonFormatter.prettyPrint(managersCompanyAsString));

        assertThat(context.read("$.[*].email").toString()).contains(DEFAULT_COMPANY_EMAIL);

/**
 * ****************** DELETE api/companies ****************
 */

        /**
         * Delete the company without the manager nor admin role
         */
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        userRepository.saveAndFlush(user3);

        assertThat(userService.checkIfUserHasRoles(user4, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user3, AuthoritiesConstants.EMPLOYEE)).isTrue();

        databaseCompaniesSizeBeforeUpdate = companyRepository.findAll().size();


        initialCompanies = companyRepository.findAll();
        initialEmployees = employeeRepository.findAll();
        initialUsers = userRepository.findAll();

        restMockMvc.perform(delete("/api/companies/{id}", updatedCompany2.getId())
            .with(user(user2.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.companyremoveforbiden")));


        assertThat(initialCompanies).hasSize(databaseCompaniesSizeBeforeUpdate);

        /**
         * Manager deletes a company that it's not his.
         */
        updatedCompany = companyRepository.findOneByEmail(DEFAULT_COMPANY_EMAIL).get();
        updatedCompany2 = companyRepository.findOneByEmail(UPDATED_COMPANY_EMAIL).get();


        restMockMvc.perform(delete("/api/companies/{id}", updatedCompany.getId())
            .with(user(user4.getLogin().toLowerCase())) // ROLE_MANAGER
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.managercanonlyremovehisowncompany")));

        assertThat(initialCompanies).hasSize(databaseCompaniesSizeBeforeUpdate);

        /**
         * Manager deletes his own company.
         */

        restMockMvc.perform(delete("/api/companies/{id}", updatedCompany2.getId())
            .with(user(user4.getLogin().toLowerCase())) // ROLE_MANAGER
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        em.clear();

        databaseCompaniesSizeBeforeUpdate = companyRepository.findAll().size();

        assertThat(userService.checkIfUserHasRoles(user4, AuthoritiesConstants.MANAGER)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user3, AuthoritiesConstants.EMPLOYEE)).isFalse();
        assertThat(initialCompanies).hasSize(databaseCompaniesSizeBeforeUpdate);
        assertThat(initialEmployees).hasSize(databaseEmployeesSizeBeforeUpdate);
        assertThat(initialUsers).hasSize(databaseUsersSizeBeforeUpdate);
/**
 * ****************** PUT api/companies ****************
 */


        /**
         * Delete all the test entities
         */
        notificationRepository.deleteInBatch(Arrays.asList(notification1,notification2, notification3,notification4));
        userRepository.deleteInBatch(Arrays.asList(user1, user2,user3, user4));
        companyRepository.deleteInBatch(Arrays.asList(testCompany,company2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2, employee3, employee4));

    }


    @Test
    @Transactional
    void assertThatSaveWithCompany() {
        company.setEmployees(null);
        userRepository.saveAndFlush(user1);
        companyRepository.saveAndFlush(company);

        employeeService.saveWithCompany(employee1, company);
        Optional<Company> maybeCompany = companyRepository.findOneByEmployees(Collections.singleton(employee1));
        assertThat(maybeCompany).isPresent();
        assertThat(maybeCompany.get()).isEqualTo(company);

        verify(mockEmployeeSearchRepository, times(1)).save(employee1);

        employeeService.delete(employee1.getId());
        companyRepository.delete(company);
    }

    @Test
    @Transactional
    void assertThatCheckIfUserHasRoles() {

        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);

        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.MANAGER, AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.EMPLOYEE)).isFalse();

        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
    }

    @Test
    @Transactional
    void assertThatAnyActivatedUserHasALinkedEmployee() {

        List<User> allUsers = userRepository.findAllByActivatedIsTrue();
        if (!allUsers.isEmpty()) {
            User randomUser = allUsers.stream().findAny().get();
            assertThat(employeeRepository.findByLogin(randomUser.getLogin())).isPresent();

            Employee linkedEmployee = employeeRepository.findByLogin(randomUser.getLogin()).get();

            assertThat(randomUser.getEmail()).isEqualTo(linkedEmployee.getEmail());
        }

    }


    @Test
    @Transactional
    void assertThatCompanyIsSavedWithEmployee() {

        userService.save(user1);
        companyService.saveWithEmployee(company, employee1);

        assertThat(userRepository.findOneByEmailIgnoreCase(user1.getEmail())).isPresent();
        assertThat(employeeRepository.findOneByEmail(user1.getEmail())).isPresent();

        Employee testEmployee = employeeRepository.findOneByEmail(user1.getEmail()).get();

        assertThat(companyRepository.findOneByEmployees(Collections.singleton(testEmployee))).isPresent();

        Company testCompany = companyRepository.findOneByEmployees(Collections.singleton(testEmployee)).get();

        User testUser = userRepository.findOneByLogin(employee1.getLogin()).get();

        assertThat(testEmployee.getCompany().getName()).isEqualTo(testCompany.getName());
        assertThat(testUser.getEmail()).isEqualTo(user1.getEmail());

        userRepository.delete(user1);
        employeeRepository.delete(employee1);
        companyRepository.delete(company);

    }


    @Test
    @Transactional
    void assertThatWhenTheCompanyIsRemovedTheEmployeesLoseTheirRoles() {


        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);
        userRepository.saveAndFlush(user2);
        Company updatedCompany = companyRepository.saveAndFlush(company);
        int initialCompanySize = companyRepository.findAll().size();

        employee1.setCompany(updatedCompany);
        employee2.setCompany(updatedCompany);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);
        int initialEmployeesInACompany = employeeRepository.findAll().size();

        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.USER)).isTrue();
        assertThat(updatedCompany.getId()).isNotNull();

        companyService.delete(updatedCompany.getId());

        assertThat(companyRepository.findAll().size()).isEqualTo(initialCompanySize - 1);
        assertThat(initialEmployeesInACompany).isEqualTo(employeeRepository.findAll().size());
        assertThat(userRepository.findOneByLogin(user1.getLogin())).isPresent();
        assertThat(userRepository.findOneByLogin(user2.getLogin())).isPresent();

        User testUser1 = userRepository.findOneByLogin(user1.getLogin()).get();
        User testUser2 = userRepository.findOneByLogin(user2.getLogin()).get();


        assertThat(userService.checkIfUserHasRoles(testUser1, AuthoritiesConstants.MANAGER)).isFalse();
        assertThat(userService.checkIfUserHasRoles(testUser2, AuthoritiesConstants.EMPLOYEE)).isFalse();
        assertThat(userService.checkIfUserHasRoles(testUser2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(testUser1, AuthoritiesConstants.USER)).isTrue();


        userRepository.deleteInBatch(Arrays.asList(user1, user2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2));
    }

    private void securityAwareMockMVC() {
        // Create security-aware mockMvc
        restMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

}
