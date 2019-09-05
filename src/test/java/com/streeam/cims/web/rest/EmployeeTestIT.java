package com.streeam.cims.web.rest;

import com.streeam.cims.CidApp;
import com.streeam.cims.domain.*;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.*;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.repository.search.NotificationSearchRepository;
import com.streeam.cims.repository.search.UserSearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.CompanyService;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.UserService;
import com.streeam.cims.service.dto.EmployeeDTO;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.streeam.cims.domain.enumeration.NotificationType.*;
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
class EmployeeTestIT {

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

    @Autowired
    private NotificationSearchRepository mockNotificationSearchRepository;

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

    @Mock
    private MailService mockMailService;

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
    private MockMvc restEmployeeMockMvc;


    /**
     * Create an updated entity for this test.
     * <p>
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Employee createEntity(EntityManager em) {
        Employee employee = new Employee()
            .login(UPDATED_EMPLOYEE_LOGIN)
            .firstName(UPDATED_EMPLOYEE_FIRST_NAME)
            .lastName(UPDATED_EMPLOYEE_LAST_NAME)
            .email(UPDATED_EMPLOYEE_EMAIL)
            .hired(UPDATED_HIRED)
            .language(DEFAULT_USER2_LANGKEY)
            .image(DEFAULT_EMPLOYEE_IMAGE)
            .imageContentType(UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE);
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

    @BeforeEach
    void init() {

        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendEmailFromTemplate(any(User.class), anyString(), anyString());

        final EmployeeResource employeeResource = new EmployeeResource(employeeService);
        this.restEmployeeMockMvc = MockMvcBuilders.standaloneSetup(employeeResource)
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
            .format(NotificationType.ACCEPT_REQUEST)
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
    void assertEmployeesPostIsNotAccessible() throws Exception {

        securityAwareMockMVC();
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        userRepository.saveAndFlush(user1);
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee1);

        /**
         * This endpoint is not accessible since it is forbidden to create a employee directly.
         */
        restEmployeeMockMvc.perform(post("/api/employees")
            .with(user(user1.getLogin().toLowerCase()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.endpointdisabled")));

        userRepository.delete(user1);
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
        restEmployeeMockMvc.perform(put("/api/employees")
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
        restEmployeeMockMvc.perform(put("/api/employees")
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
        restEmployeeMockMvc.perform(put("/api/employees")
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
        restEmployeeMockMvc.perform(put("/api/employees")
            .with(user(user4.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.noupdatestoemployeesoutsidethecompany")));

        /**
         *  Update a employee
         */
        restEmployeeMockMvc.perform(put("/api/employees")
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
         * An user that does not have the role of manager nor admin.
         * He does not have the authority to access this endpoint.
         */
        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc")
            .with(user(user3.getLogin())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        restEmployeeMockMvc.perform(get("/api/employees?sort=id,desc")
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
        restEmployeeMockMvc.perform(get("/api/employees/{id}", employee4.getId())
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
        restEmployeeMockMvc.perform(get("/api/_search/employees?query=id:" + testEmployee.getId())
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

        restEmployeeMockMvc.perform(delete("/api/employees/{id}", employee4.getId())
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

        restEmployeeMockMvc.perform(delete("/api/employees/{id}", employee4.getId())
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

        notificationRepository.deleteInBatch(Arrays.asList(notification1, notification2, notification3, notification4));
        userRepository.deleteInBatch(Arrays.asList(testUser, user2, user3, user4));
        companyRepository.deleteInBatch(Arrays.asList(company, company2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2, employee3, employee4));

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
    public void updateNonExistingEmployee() throws Exception {
        int databaseSizeBeforeUpdate = employeeRepository.findAll().size();

        // Create the Employee
        EmployeeDTO employeeDTO = employeeMapper.toDto(employee1);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restEmployeeMockMvc.perform(put("/api/employees")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(employeeDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Employee in the database
        List<Employee> employeeList = employeeRepository.findAll();
        assertThat(employeeList).hasSize(databaseSizeBeforeUpdate);

        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(0)).save(employee1);
    }

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


    @Test
    @Transactional
    public void getNonExistingEmployee() throws Exception {
        // Get the employee
        restEmployeeMockMvc.perform(get("/api/employees/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }


    @Test
    @Transactional
    void assertThatRequestToJoinBehavesAsIntended() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        User user_one = userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        User user_two = userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        User user_employee = userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        User user_manager = userRepository.saveAndFlush(user4);

        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee1.setHired(false);
        employee2.setHired(false);
        employee3.setHired(true);
        employee4.setHired(true);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);

        Employee employee_user1 = employeeRepository.saveAndFlush(employee1);
        Employee employee_user2 = employeeRepository.saveAndFlush(employee2);
        Employee employee = employeeRepository.saveAndFlush(employee3);
        Employee manager = employeeRepository.saveAndFlush(employee4);

        notification1.setEmployee(employee_user2);
        notification1.sentDate(Instant.now().minus(2, ChronoUnit.DAYS));
        notification1.setFormat(NotificationType.REJECT_REQUEST);
        notification1.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification1);

        notification2.setEmployee(employee_user1);
        notification2.sentDate(Instant.now().minus(4, ChronoUnit.DAYS));
        notification2.setFormat(NotificationType.REJECT_REQUEST);
        notification2.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification2);

        int databaseNotificationsSizeBeforeUpdate = notificationRepository.findAll().size();

        assertThat(employee_user1.getId()).isNotNull();
        assertThat(updatedCompany2.getId()).isNotNull();
        assertThat(employeeService.findOneById(employee_user1.getId())).isPresent();
        assertThat(companyService.findCompanyById(updatedCompany2.getId())).isPresent();
        assertThat(user_one.getEmail()).isEqualTo(employee_user1.getEmail());
        assertThat(employee_user1.getCompany()).isNull();
        assertThat(manager.getNotifications()).isEmpty();

        /**
         * Only the logged in employee can request to join a company.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/request-to-join/{companyId}", employee_user1.getId(), updatedCompany2.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.onlycurrentloggedincanjoincomp")));

        /**
         * You cannot request to join a company if you are already into one.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/request-to-join/{companyId}", manager.getId(), updatedCompany2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.joinonlyifunemployed")));

        /**
         *  employee_user2 who is requesting to join updatedCompany2 has had a application rejected 2 days ago by the same company.
         *  He cannot apply to join a company if that company has rejected a request of his sent less then 3 days ago.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/request-to-join/{companyId}", employee_user2.getId(), updatedCompany2.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.3daysbeforeyoucanrequestagain")));

        /**
         *  employee_user1 who is requesting to join updatedCompany2 has had a application rejected 4 days ago by the same company.
         *  This time the request is valid.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/request-to-join/{companyId}", employee_user1.getId(), updatedCompany2.getId())
            .with(user(user_one.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());


        assertThat(notificationRepository.findAll().size()).isEqualTo(databaseNotificationsSizeBeforeUpdate + 1);
        assertThat(employeeRepository.findOneByEmail(manager.getEmail())).isPresent();

        Employee savedManager = employeeRepository.findOneByEmail(manager.getEmail()).get();

        assertThat(savedManager.getNotifications().stream().findAny()).isPresent();

        Notification managersNotification = savedManager.getNotifications().stream().findAny().get();

        assertThat(managersNotification.getFormat()).isEqualTo(NotificationType.REQUEST_TO_JOIN);

        assertThat(managersNotification.getReferenced_user()).isEqualTo(employee_user1.getEmail());

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(1)).save(any(Notification.class));
        // Validate the Employee in Elasticsearch
        verify(mockEmployeeSearchRepository, times(1)).save(manager);

        notificationRepository.deleteInBatch(notificationRepository.findAllByEmployee(employee_user1));
        notificationRepository.deleteInBatch(notificationRepository.findAllByEmployee(employee_user2));
        notificationRepository.deleteInBatch(notificationRepository.findAllByEmployee(manager));
        userRepository.deleteInBatch(Arrays.asList(user_one, user_two, user_employee, user_manager));
        companyRepository.deleteInBatch(Arrays.asList(updatedCompany, updatedCompany2));
        employeeRepository.deleteInBatch(Arrays.asList(employee_user1, employee_user2, employee, manager));
    }


    @Test
    @Transactional
    void assertThatGetAllUnemployedEmployees() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        User user_unemployed1 = userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);
        User user_employee = userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user3);
        User user_manager = userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.USER, user4);
        User user_unemployed2 = userRepository.saveAndFlush(user4);

        Company updatedCompany = companyRepository.saveAndFlush(company);

        employee1.setCompany(null);
        employee4.setCompany(null);
        employee1.setHired(false);
        employee2.setHired(true);
        employee3.setHired(true);
        employee4.setHired(false);
        employee2.setCompany(updatedCompany);
        employee3.setCompany(updatedCompany);

        Employee unemployed1 = employeeRepository.saveAndFlush(employee1);
        Employee employee = employeeRepository.saveAndFlush(employee2);
        Employee manager = employeeRepository.saveAndFlush(employee3);
        Employee unemployed2 = employeeRepository.saveAndFlush(employee4);


        /**
         * An user that does not have the role of manager nor admin.
         * He does not have the authority to access this endpoint.
         */
        restEmployeeMockMvc.perform(get("/api/employees/unemployed?sort=id,desc")
            .with(user(user_unemployed1.getLogin())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        /**
         * An user that does not have the role of manager nor admin.
         * He does not have the authority to access this endpoint.
         */
        restEmployeeMockMvc.perform(get("/api/employees/unemployed?sort=id,desc")
            .with(user(user_employee.getLogin())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        assertThat(employeeRepository.findAll().stream().filter(empl -> !empl.isHired())).isNotEmpty();
        List<Employee> allUnemployeedEmployees = employeeRepository.findAll().stream().filter(empl -> !empl.isHired()).collect(Collectors.toList());
        Employee randomUnemployedEmployee = allUnemployeedEmployees.stream().findAny().get();

        assertThat(employeeRepository.findAll().stream().filter(empl -> empl.isHired())).isNotEmpty();
        List<Employee> allEmployeedEmployees = employeeRepository.findAll().stream().filter(empl -> empl.isHired()).collect(Collectors.toList());
        Employee randomEmployedEmployee = allEmployeedEmployees.stream().findAny().get();

        restEmployeeMockMvc.perform(get("/api/employees/unemployed?sort=id,desc")
            .with(user(user_manager.getLogin())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(not(randomEmployedEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(not(randomEmployedEmployee.getLogin())))
            .andExpect(jsonPath("$.[*].firstName").value(not(randomEmployedEmployee.getFirstName())))
            .andExpect(jsonPath("$.[*].lastName").value(not(randomEmployedEmployee.getLastName())))
            .andExpect(jsonPath("$.[*].email").value(not(randomEmployedEmployee.getEmail())))
            .andExpect(jsonPath("$.[*].hired").value(not(randomEmployedEmployee.isHired())))

            .andExpect(jsonPath("$.[*].id").value(hasItem(randomUnemployedEmployee.getId().intValue())))
            .andExpect(jsonPath("$.[*].login").value(hasItem(randomUnemployedEmployee.getLogin())))
            .andExpect(jsonPath("$.[*].firstName").value(hasItem(randomUnemployedEmployee.getFirstName())))
            .andExpect(jsonPath("$.[*].lastName").value(hasItem(randomUnemployedEmployee.getLastName())))
            .andExpect(jsonPath("$.[*].email").value(hasItem(randomUnemployedEmployee.getEmail())))
            .andExpect(jsonPath("$.[*].hired").value(not(true)));

        userRepository.deleteInBatch(Arrays.asList(user_unemployed1, user_unemployed2, user_employee, user_manager));
        companyRepository.delete(updatedCompany);
        employeeRepository.deleteInBatch(Arrays.asList(unemployed1, unemployed2, employee, manager));
    }


    @Test
    @Transactional
    void assertThatInviteToJoinBehavesAsIntended() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        User user_one = userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        User user_two = userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        User user_employee = userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        User user_manager = userRepository.saveAndFlush(user4);

        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee1.setHired(false);
        employee2.setHired(false);
        employee3.setHired(true);
        employee4.setHired(true);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);


        Employee employee_user1 = employeeRepository.saveAndFlush(employee1);
        Employee employee_user2 = employeeRepository.saveAndFlush(employee2);
        Employee employee = employeeRepository.saveAndFlush(employee3);
        Employee manager = employeeRepository.saveAndFlush(employee4);

        notification1.setEmployee(employee_user2);
        notification1.sentDate(Instant.now().minus(2, ChronoUnit.DAYS));
        notification1.setFormat(NotificationType.REJECT_INVITE);
        notification1.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification1);

        notification2.setEmployee(employee_user1);
        notification2.sentDate(Instant.now().minus(4, ChronoUnit.DAYS));
        notification2.setFormat(NotificationType.REJECT_INVITE);
        notification2.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification2);

        int databaseNotificationsSizeBeforeUpdate = notificationRepository.findAll().size();


        /**
         * The email cannot be null. And the current user must be linked to a company.
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", employee_user1.getEmail())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.nocompanylinkedtoemployee")));

        /**
         * Invalid email.
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", "invalidEmail@com")
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.invalidemail")));

        /**
         * Only the manager and the admin can access this endpoint
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", employee_user1.getEmail())
            .with(user(user_employee.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.accessrestricted")));

        /**
         * Can't invite a user who is already in a company.
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", employee.getEmail())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.cantinviteuseralreadyincompany")));

        /**
         * This user has rejected an invitation from this company less then 3 days ago.
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", employee_user2.getEmail())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.3daysbeforeyoucaninviteagain")));


        /**
         * Invite an existing user to join updatedCompany2. This user has rejected an invitation from this company but it was more then 3 days ago
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", employee_user1.getEmail())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail())).isPresent();
        Employee employeeToJoin = employeeRepository.findOneByEmail(employee_user1.getEmail()).get();
        assertThat(employeeToJoin.getNotifications().size()).isEqualTo(1);
        assertThat(employeeToJoin.getNotifications().stream().findFirst().get().getFormat()).isEqualTo(INVITATION);
        assertThat(employeeToJoin.getNotifications().stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(employeeToJoin.getNotifications().stream().findFirst().get().getReferenced_user()).isEqualTo(manager.getEmail());
        assertThat(notificationRepository.findAll().size()).isEqualTo(databaseNotificationsSizeBeforeUpdate + 1);


        /**
         * Invite an non-existing user to join updatedCompany2.
         */
        restEmployeeMockMvc.perform(post("/api/employees/invite-to-join/{email}", "non-existing@UserEmail.com")
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        assertThat(employeeRepository.findOneByEmail("non-existing@UserEmail.com")).isNotPresent();
        assertThat(manager.getNotifications().size()).isEqualTo(1);
        assertThat(manager.getNotifications().stream().findFirst().get().getFormat()).isEqualTo(INVITATION);
        assertThat(manager.getNotifications().stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(manager.getNotifications().stream().findFirst().get().getReferenced_user()).isEqualTo("non-existing@UserEmail.com");
        assertThat(notificationRepository.findAll().size()).isEqualTo(databaseNotificationsSizeBeforeUpdate + 2);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(2)).save(any(Notification.class));

        notificationRepository.deleteInBatch(manager.getNotifications());
        notificationRepository.deleteInBatch(employeeToJoin.getNotifications());
        userRepository.deleteInBatch(Arrays.asList(user_one, user_two, user_employee, user_manager));
        companyRepository.deleteInBatch(Arrays.asList(updatedCompany, updatedCompany2));
        employeeRepository.deleteInBatch(Arrays.asList(employee_user1, employee_user2, employee, manager));
    }


    @Test
    @Transactional
    void assertThatApproveRequestBehavesAsIntended() throws Exception {

        securityAwareMockMVC();

        int initialUserDatabaseSize = userRepository.findAll().size();
        int initialEmployeeDatabaseSize = employeeRepository.findAll().size();
        int initialCompanyDatabaseSize = companyRepository.findAll().size();
        int initialNotificationsDatabaseSize = notificationRepository.findAll().size();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        User user_one = userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        User user_two = userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        User user_employee = userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        User user_manager = userRepository.saveAndFlush(user4);

        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee1.setHired(false);
        employee2.setHired(false);
        employee3.setHired(true);
        employee4.setHired(true);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);


        Employee employee_user1 = employeeRepository.saveAndFlush(employee1);
        Employee employee_user2 = employeeRepository.saveAndFlush(employee2);
        Employee employee = employeeRepository.saveAndFlush(employee3);
        Employee manager = employeeRepository.saveAndFlush(employee4);



        /**
         * The logged user has to be unemployed.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user1.getId(), updatedCompany.getId())
            .with(user(user_employee.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.joinonlyifunemployed")));

        /**
         * Only the current user can accept to join a company. No one else can accept the invitation on his behalf.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user1.getId(), updatedCompany.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.onlycurrentusercanaccept")));

        /**
         * No invitation sent to this employee_user2 by updatedCompany.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user2.getId(), updatedCompany.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasinvitationlessthen14days")));


        notification1.setEmployee(employee_user2);
        notification1.sentDate(Instant.now().minus(15, ChronoUnit.DAYS));
        notification1.setFormat(INVITATION);
        notification1.setCompany(updatedCompany.getId());
        notificationRepository.saveAndFlush(notification1);

        notification2.setEmployee(employee_user1);
        notification2.sentDate(Instant.now().minus(13, ChronoUnit.DAYS));
        notification2.setFormat(INVITATION);
        notification2.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification2);

         //An invitation was sent to employee_user2 from updatedCompany but was sent after 14 days.
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user2.getId(), updatedCompany.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasinvitationlessthen14days")));


        notification1.sentDate(Instant.now().minus(13, ChronoUnit.DAYS));
        notificationRepository.saveAndFlush(notification1);

        // Only the manager and the admin can access this endpoint
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user2.getId(), updatedCompany.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.nomanager")));

        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail()).get().getUser().getAuthorities().stream().map(Authority::getName)).doesNotContain(AuthoritiesConstants.EMPLOYEE);

        /**
         * The user joins the company and he is given the Employee role
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/approve-request/{companyId}", employee_user1.getId(), updatedCompany2.getId())
            .with(user(user_one.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail())).isPresent();
        Employee newEmployee = employeeRepository.findOneByEmail(employee_user1.getEmail()).get();
        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).contains(AuthoritiesConstants.EMPLOYEE);
        assertThat(newEmployee.isHired()).isTrue();
        List<Notification> allNotificationsFromCompany2 = notificationRepository.findAllByCompany(updatedCompany2.getId());
        assertThat(allNotificationsFromCompany2.size()).isEqualTo(4);

        assertThat(notificationRepository.findAllByEmployee(employee_user1).size()).isEqualTo(2);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).stream().map(notification -> notification.getFormat())).containsOnly(WELCOME, INVITATION);
        assertThat(notificationRepository.findAllByEmployee(manager).stream().map(notification -> notification.getFormat())).containsOnly(ACCEPT_REQUEST);
        assertThat(notificationRepository.findAllByEmployee(employee).stream().map(notification -> notification.getFormat())).containsOnly(NEW_EMPLOYEE);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(newEmployee.getCompany().getId()).isEqualTo(updatedCompany2.getId());
        assertThat(notificationRepository.findAll().size()).isEqualTo(initialNotificationsDatabaseSize+5);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(3)).save(any(Notification.class));

        notificationRepository.deleteInBatch(notificationRepository.findAllByCompany(updatedCompany.getId()));
        notificationRepository.deleteInBatch(notificationRepository.findAllByCompany(updatedCompany2.getId()));

        companyRepository.deleteInBatch(Arrays.asList(updatedCompany, updatedCompany2));
        employeeRepository.deleteInBatch(Arrays.asList(employee_user1, employee_user2, employee, manager));
        userRepository.deleteInBatch(Arrays.asList(user_one, user_two, user_employee, user_manager));

        assertThat(userRepository.findAll().size()).isEqualTo(initialUserDatabaseSize);
        assertThat(employeeRepository.findAll().size()).isEqualTo(initialEmployeeDatabaseSize);
        assertThat(companyRepository.findAll().size()).isEqualTo(initialCompanyDatabaseSize);
        assertThat(notificationRepository.findAll().size()).isEqualTo(initialNotificationsDatabaseSize);

    }



    @Test
    @Transactional
    void assertThatDeclineRequestBehavesAsIntended() throws Exception {

        securityAwareMockMVC();

        int initialUserDatabaseSize = userRepository.findAll().size();
        int initialEmployeeDatabaseSize = employeeRepository.findAll().size();
        int initialCompanyDatabaseSize = companyRepository.findAll().size();
        int initialNotificationsDatabaseSize = notificationRepository.findAll().size();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        User user_one = userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        User user_two = userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        User user_employee = userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        User user_manager = userRepository.saveAndFlush(user4);

        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee1.setHired(false);
        employee2.setHired(false);
        employee3.setHired(true);
        employee4.setHired(true);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);


        Employee employee_user1 = employeeRepository.saveAndFlush(employee1);
        Employee employee_user2 = employeeRepository.saveAndFlush(employee2);
        Employee employee = employeeRepository.saveAndFlush(employee3);
        Employee manager = employeeRepository.saveAndFlush(employee4);

        /**
         * Only the current user can decline to join a company. No one else can decline the invitation on his behalf.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee_user1.getId(), updatedCompany2.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.onlycurrentloggedincandeclinetojoincomp")));

        /**
         * The logged user has to be unemployed.
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee.getId(), updatedCompany.getId())
            .with(user(user_employee.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.joinonlyifunemployed")));
        /**
         * The company must have a manager
         */
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee_user2.getId(), updatedCompany.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.nomanager")));

        // No invitation sent to this employee_user2 by updatedCompany.
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee_user2.getId(), updatedCompany2.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasinvitationlessthen14days")));


        notification1.setEmployee(employee_user2);
        notification1.sentDate(Instant.now().minus(15, ChronoUnit.DAYS));
        notification1.setFormat(INVITATION);
        notification1.setCompany(updatedCompany.getId());
        notificationRepository.saveAndFlush(notification1);

        //No invitation sent to this employee_user2 by updatedCompany.
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee_user2.getId(), updatedCompany2.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasinvitationlessthen14days")));

        notification2.setEmployee(employee_user1);
        notification2.sentDate(Instant.now().minus(13, ChronoUnit.DAYS));
        notification2.setFormat(INVITATION);
        notification2.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification2);

        // employee_user1 declines the updatedCompany2's invitation
        restEmployeeMockMvc.perform(post("/api/employees/{employeeId}/decline-request/{companyId}", employee_user1.getId(), updatedCompany2.getId())
            .with(user(user_one.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());


        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail())).isPresent();
        Employee newEmployee = employeeRepository.findOneByEmail(employee_user1.getEmail()).get();
        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).doesNotContain(AuthoritiesConstants.EMPLOYEE);
        assertThat(newEmployee.isHired()).isFalse();
        List<Notification> allNotificationsFromCompany2 = notificationRepository.findAllByCompany(updatedCompany2.getId());
        assertThat(allNotificationsFromCompany2.size()).isEqualTo(2);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).size()).isEqualTo(1);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).stream().map(notification -> notification.getFormat())).containsOnly(INVITATION);
        assertThat(notificationRepository.findAllByEmployee(manager).stream().map(notification -> notification.getFormat())).containsOnly(REJECT_INVITE);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(newEmployee.getCompany()).isNull();
        assertThat(notificationRepository.findAll().size()).isEqualTo(initialNotificationsDatabaseSize+3);

        // Validate the Notification in Elasticsearch
        verify(mockNotificationSearchRepository, times(1)).save(any(Notification.class));

        notificationRepository.deleteInBatch(notificationRepository.findAllByCompany(updatedCompany.getId()));
        notificationRepository.deleteInBatch(notificationRepository.findAllByCompany(updatedCompany2.getId()));

        companyRepository.deleteInBatch(Arrays.asList(updatedCompany, updatedCompany2));
        employeeRepository.deleteInBatch(Arrays.asList(employee_user1, employee_user2, employee, manager));
        userRepository.deleteInBatch(Arrays.asList(user_one, user_two, user_employee, user_manager));

        assertThat(userRepository.findAll().size()).isEqualTo(initialUserDatabaseSize);
        assertThat(employeeRepository.findAll().size()).isEqualTo(initialEmployeeDatabaseSize);
        assertThat(companyRepository.findAll().size()).isEqualTo(initialCompanyDatabaseSize);
        assertThat(notificationRepository.findAll().size()).isEqualTo(initialNotificationsDatabaseSize);

    }

    private void securityAwareMockMVC() {
        // Create security-aware mockMvc
        restEmployeeMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

}
