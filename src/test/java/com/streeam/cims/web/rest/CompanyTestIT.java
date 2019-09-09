package com.streeam.cims.web.rest;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
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
import com.streeam.cims.service.dto.CompanyDTO;
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
import org.springframework.test.web.servlet.ResultActions;
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

import static com.streeam.cims.domain.enumeration.NotificationType.*;
import static com.streeam.cims.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
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
class CompanyTestIT {

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
    private static final String DEFAULT_USER3_LANGKEY = "en";

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
    private NotificationSearchRepository mockNotificationSearchRepository;

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

    @Mock
    private MailService mockMailService;

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
    private MockMvc restCompanyMockMvc;
    private boolean initialDatabaseCheck = true;

    private int initialUserDatabaseSize, initialEmployeeDatabaseSize, initialCompanyDatabaseSize, initialNotificationsDatabaseSize;


    @BeforeEach
    void init() {
        if(initialDatabaseCheck){
            initialUserDatabaseSize = userRepository.findAll().size();
            initialEmployeeDatabaseSize = employeeRepository.findAll().size();
            initialCompanyDatabaseSize = companyRepository.findAll().size();
            initialNotificationsDatabaseSize = notificationRepository.findAll().size();
            initialDatabaseCheck = false;
        }

        MockitoAnnotations.initMocks(this);
        doNothing().when(mockMailService).sendEmailFromTemplate(any(User.class),anyString(),anyString());

        final CompanyResource companyResource = new CompanyResource(companyService,authorityRepository, mockMailService);
        this.restCompanyMockMvc = MockMvcBuilders.standaloneSetup(companyResource)
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
    void assertTheCreationOfACompany() throws  Exception{

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);

        Company savedCompany2 = companyRepository.saveAndFlush(company2);

        List<Company> initialCompanies ;

        employee1.setCompany(null);
        employee3.setCompany(savedCompany2);
        employee4.setCompany(savedCompany2);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee3);
        employeeRepository.saveAndFlush(employee4);

        int databaseCompaniesSizeBeforeUpdate = companyRepository.findAll().size();

/**
 * ****************** POST api/companies ****************
 */

        assertThat(userRepository.findOneByEmailIgnoreCase(user1.getEmail())).isPresent();
        User testUser = userRepository.findOneByEmailIgnoreCase(user1.getEmail()).get();
        assertThat(userService.checkIfUserHasRoles(testUser, AuthoritiesConstants.MANAGER)).isFalse();

        CompanyDTO companyDTO = companyMapper.toDto(company);

        /**
         * Verify that if you are a manager or a employee you cannot create a company
         */
        restCompanyMockMvc.perform(post("/api/companies")
            .with(user(Arrays.asList(user3, user4).stream().findAny().get().getLogin().toLowerCase()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.wrongroleforcreatingcompany")));

        restCompanyMockMvc.perform(post("/api/companies")
            .with(user(user1.getLogin().toLowerCase()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isCreated());

        initialCompanies = companyRepository.findAll();

        assertThat(initialCompanies.get(databaseCompaniesSizeBeforeUpdate)).isNotNull();

        Company testCompany = initialCompanies.get(databaseCompaniesSizeBeforeUpdate);

        assertThat(testCompany.getEmail()).isEqualToIgnoringCase(company.getEmail());
        assertThat(userService.checkIfUserHasRoles(testUser, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(initialCompanies).hasSize(databaseCompaniesSizeBeforeUpdate + 1);

        // Validate the Employee in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).save(any(Company.class));
        verify(mockUserSearchRepository, times(1)).save(any(User.class));
        verify(mockEmployeeSearchRepository, times(1)).save(any(Employee.class));


        userRepository.deleteInBatch(Arrays.asList(user1, user3, user4));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee3, employee4));
        companyRepository.deleteInBatch(Arrays.asList(testCompany, savedCompany2));
    }


    @Test
    @Transactional
    void assertThatPostCompaniesBehaviorWorksAsIntended() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.USER, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);


        Company updatedCompany = companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);
        employeeRepository.saveAndFlush(employee3);
        employeeRepository.saveAndFlush(employee4);

        int databaseCompaniesSizeBeforeUpdate = companyRepository.findAll().size();

/**
 * ****************** GET api/companies ****************
 */
        /**
         * The manager of a company cannot see other companies
         */
        ResultActions resultManagersCompany = restCompanyMockMvc.perform(get("/api/companies?sort=id,desc")
            .with(user(user1.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(Matchers.not(updatedCompany2.getId().intValue())));

        String managersCompanyAsString = resultManagersCompany.andReturn().getResponse().getContentAsString();
        /**
         * The User can see all companies
         */
        ResultActions resultActions = restCompanyMockMvc.perform(get("/api/companies?sort=id,desc")
            .with(user(user2.getLogin().toLowerCase())))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(Matchers.hasItem(updatedCompany.getId().intValue())))
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
        assertThat(jsonLength).isEqualTo(databaseCompaniesSizeBeforeUpdate);

        //System.out.println(JsonFormatter.prettyPrint(managersCompanyAsString));

        assertThat(context.read("$.[*].email").toString()).contains(DEFAULT_COMPANY_EMAIL);

        userRepository.deleteInBatch(Arrays.asList(user1, user2, user3, user4));
        employeeRepository.deleteInBatch(Arrays.asList(employee1,employee2, employee3, employee4));
        companyRepository.deleteInBatch(Arrays.asList(company, company2));
    }


    @Test
    @Transactional
    void assertThatDeleteCompaniesBehaviorWorksAsIntended() throws Exception {

        securityAwareMockMVC();

        userService.allocateAuthority(AuthoritiesConstants.USER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.USER, user2);
        userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);


        Company updatedCompany= companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);


        employee1.setCompany(null);
        employee2.setCompany(null);
        employee3.setCompany(updatedCompany2);
        employee4.setCompany(updatedCompany2);
        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);
        employeeRepository.saveAndFlush(employee3);
        employeeRepository.saveAndFlush(employee4);


        int databaseCompaniesSizeBeforeDelete = companyRepository.findAll().size();
        int databaseNotificationsSizeBeforeDelete = notificationRepository.findAll().size();

/**
 * ****************** DELETE api/companies ****************
 */
        /**
         * Delete the company without the manager nor admin role
         */

        assertThat(userService.checkIfUserHasRoles(user4, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user3, AuthoritiesConstants.EMPLOYEE)).isTrue();


        restCompanyMockMvc.perform(delete("/api/companies/{id}", updatedCompany2.getId())
            .with(user(user2.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.companyremoveforbiden")));

        assertThat(initialCompanyDatabaseSize).isEqualTo(databaseCompaniesSizeBeforeDelete-2);

        /**
         * Manager deletes a company that it's not his.
         */
        updatedCompany = companyRepository.findOneByEmail(DEFAULT_COMPANY_EMAIL).get();
        updatedCompany2 = companyRepository.findOneByEmail(UPDATED_COMPANY_EMAIL).get();


        restCompanyMockMvc.perform(delete("/api/companies/{id}", updatedCompany.getId())
            .with(user(user4.getLogin().toLowerCase())) // ROLE_MANAGER
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.managercanonlyremovehisowncompany")));

        assertThat(initialCompanyDatabaseSize).isEqualTo(databaseCompaniesSizeBeforeDelete-2);
        assertThat(initialNotificationsDatabaseSize).isEqualTo(databaseNotificationsSizeBeforeDelete);

        /**
         * Manager deletes his own company.
         */

        restCompanyMockMvc.perform(delete("/api/companies/{id}", updatedCompany2.getId())
            .with(user(user4.getLogin().toLowerCase())) // ROLE_MANAGER
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isNoContent());

        List<Company> companiesAfterDelete = companyRepository.findAll();
        List<Notification> notificationsAfterDelete = notificationRepository.findAll();

        assertThat(employeeRepository.findOneByEmail(employee3.getEmail())).isPresent();
        assertThat(employeeRepository.findOneByEmail(employee4.getEmail())).isPresent();

        Employee testEmmployee3 = employeeRepository.findOneByEmail(employee3.getEmail()).get();
        Employee testEmmployee4 = employeeRepository.findOneByEmail(employee4.getEmail()).get();

        assertThat(notificationRepository.findAllByEmployee(testEmmployee3)).isNotEmpty();
        Notification employee3FiredNotification = notificationRepository.findAllByEmployee(testEmmployee3).get(0);
        Notification employee4FiredNotification = notificationRepository.findAllByEmployee(testEmmployee4).get(0);
        assertThat(Arrays.asList(employee3FiredNotification, employee4FiredNotification).stream().findAny().get().getFormat())
            .isEqualTo(NotificationType.FIRED);
        assertThat(userService.checkIfUserHasRoles(user4, AuthoritiesConstants.MANAGER)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user3, AuthoritiesConstants.EMPLOYEE)).isFalse();
        assertThat(companyRepository.findAll()).hasSize(initialCompanyDatabaseSize -1);
        assertThat(employeeRepository.findAll()).hasSize(initialEmployeeDatabaseSize);
        assertThat(userRepository.findAll()).hasSize(initialUserDatabaseSize);
        assertThat(notificationRepository.findAll()).hasSize(initialNotificationsDatabaseSize + 2);

        verify(mockNotificationSearchRepository, times(2)).save(any(Notification.class));
        verify(mockCompanySearchRepository, times(1)).delete(updatedCompany2);

        notificationRepository.deleteInBatch(testEmmployee3.getNotifications());
        notificationRepository.deleteInBatch(testEmmployee4.getNotifications());
        userRepository.deleteInBatch(Arrays.asList(user1, user2,user3, user4));
        companyRepository.deleteInBatch(Arrays.asList(company,company2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2, employee3, employee4));
    }



    @Test
    @Transactional
    void assertThatPutCompaniesBehaviorWorksAsIntended() throws Exception {

        securityAwareMockMVC();

        UserService.allocateAuthority(AuthoritiesConstants.USER, user1);
        userRepository.saveAndFlush(user1);
        UserService.allocateAuthority(AuthoritiesConstants.USER, user2);
        userRepository.saveAndFlush(user2);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user3);
        userRepository.saveAndFlush(user3);
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user4);
        userRepository.saveAndFlush(user4);

        Company updatedCompany= companyRepository.saveAndFlush(company);
        Company updatedCompany2 = companyRepository.saveAndFlush(company2);

        employee1.setCompany(null);
        employee2.setCompany(null);
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

/**
 * ****************** PUT api/companies ****************
 */

        updatedCompany2 = companyRepository.findOneByEmail(UPDATED_COMPANY_EMAIL).get();
        assertThat(companyRepository.findOneById(updatedCompany.getId())).isPresent();
        assertThat(companyRepository.findOneById(updatedCompany2.getId())).isPresent();

        updatedCompany.email(UPDATED2_COMPANY_EMAIL)
            .name(UPDATED2_NAME)
            .addressLine1(UPDATED2_ADDRESS_LINE_1)
            .addressLine2(UPDATED2_ADDRESS_LINE_2)
            .city(UPDATED2_CITY)
            .country(UPDATED2_COUNTRY)
            .companyLogo(UPDATED2_COMPANY_LOGO)
            .companyLogoContentType(UPDATED2_COMPANY_LOGO_CONTENT_TYPE);


        CompanyDTO updatedCompanyDTO = companyMapper.toDto(updatedCompany);
        CompanyDTO updatedCompanyDTO2 = companyMapper.toDto(updatedCompany2);
        updatedCompanyDTO.setEmployees(Collections.EMPTY_SET);
        updatedCompanyDTO2.setEmployees(Collections.EMPTY_SET);

        /**
         * Modifying the details of a company by a user that doesn't have the role of Manager or Admin is forbidden.
         */
        restCompanyMockMvc.perform(put("/api/companies")
            .with(user(user3.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.noauthoritytochangecomp")));
        /**
         * Manager is trying to modify  a company that is not his.  Make user 1 a Manager for company 1;
         */
        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        employee1.setCompany(company);
        userRepository.saveAndFlush(user1);

        restCompanyMockMvc.perform(put("/api/companies")
            .with(user(user1.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyDTO2)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.managercanonlyupdatehisowncompany")));

        /**
         * Manager is updating his own company.
         */
        restCompanyMockMvc.perform(put("/api/companies")
            .with(user(user4.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyDTO2)))
            .andExpect(status().is2xxSuccessful());

        // Validate the Employee in Elasticsearch
        verify(mockCompanySearchRepository, times(1)).save(any(Company.class));

        updatedCompanyDTO.setEmployees(Collections.singleton(employee1));


        /**
         * Trying to modify the employees from this endpoint is forbidden.
         */
        restCompanyMockMvc.perform(put("/api/companies")
            .with(user(user3.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.cantmodifyemployees")));

        /**
         * Trying to modify a company that doesn't exit
         */

        updatedCompanyDTO.setId(null);

        restCompanyMockMvc.perform(put("/api/companies")
            .with(user(user4.getLogin()))
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCompanyDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.idnull")));
        /**
         * Delete all the test entities
         */
        cleanUp();

    }

    private void cleanUp() {
        notificationRepository.deleteInBatch(Arrays.asList(notification1,notification2, notification3,notification4));
        userRepository.deleteInBatch(Arrays.asList(user1, user2,user3, user4));
        companyRepository.deleteInBatch(Arrays.asList(company,company2));
        employeeRepository.deleteInBatch(Arrays.asList(employee1, employee2, employee3, employee4));
    }

    @Test
    @Transactional
    void updateNonExistingCompany() throws Exception {
        // Create the Company
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCompanyMockMvc.perform(put("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        assertThat( companyRepository.findAll()).hasSize(initialCompanyDatabaseSize);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void searchCompany() throws Exception {
        // Initialize the database
        companyRepository.saveAndFlush(company);
        when(mockCompanySearchRepository.search(queryStringQuery("id:" + company.getId()), PageRequest.of(0, 20)))
            .thenReturn(new PageImpl<>(Collections.singletonList(company), PageRequest.of(0, 1), 1));
        // Search the company
        ResultActions dghdfhdf = restCompanyMockMvc.perform(get("/api/_search/companies?query=id:" + company.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(company.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_COMPANY_EMAIL)))
            .andExpect(jsonPath("$.[*].phone").value(hasItem(DEFAULT_PHONE)))
            .andExpect(jsonPath("$.[*].addressLine1").value(hasItem(DEFAULT_ADDRESS_LINE_1)))
            .andExpect(jsonPath("$.[*].addressLine2").value(hasItem(DEFAULT_ADDRESS_LINE_2)))
            .andExpect(jsonPath("$.[*].city").value(hasItem(DEFAULT_CITY)))
            .andExpect(jsonPath("$.[*].country").value(hasItem(DEFAULT_COUNTRY)))
            .andExpect(jsonPath("$.[*].postcode").value(hasItem(DEFAULT_POSTCODE)))
            .andExpect(jsonPath("$.[*].companyLogoContentType").value(hasItem(DEFAULT_COMPANY_LOGO_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].companyLogo").value(hasItem(Base64Utils.encodeToString(DEFAULT_COMPANY_LOGO))));
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
    void assertThatWhenTheCompanyIsRemovedTheEmployeesLosesTheirRoles() {


        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);
        userRepository.saveAndFlush(user2);

        Company updatedCompany = companyRepository.saveAndFlush(company);

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

        assertThat(companyRepository.findAll().size()).isEqualTo(initialCompanyDatabaseSize);
        assertThat(initialEmployeesInACompany).isEqualTo(initialEmployeeDatabaseSize+2);
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



    @Test
    @Transactional
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Company.class);
        Company company1 = new Company();
        company1.setId(1L);
        Company company2 = new Company();
        company2.setId(company1.getId());
        assertThat(company1).isEqualTo(company2);
        company2.setId(2L);
        assertThat(company1).isNotEqualTo(company2);
        company1.setId(null);
        assertThat(company1).isNotEqualTo(company2);
    }

    @Test
    @Transactional
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(CompanyDTO.class);
        CompanyDTO companyDTO1 = new CompanyDTO();
        companyDTO1.setId(1L);
        CompanyDTO companyDTO2 = new CompanyDTO();
        assertThat(companyDTO1).isNotEqualTo(companyDTO2);
        companyDTO2.setId(companyDTO1.getId());
        assertThat(companyDTO1).isEqualTo(companyDTO2);
        companyDTO2.setId(2L);
        assertThat(companyDTO1).isNotEqualTo(companyDTO2);
        companyDTO1.setId(null);
        assertThat(companyDTO1).isNotEqualTo(companyDTO2);
    }

    @Test
    @Transactional
    void testEntityFromId() {
        assertThat(companyMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(companyMapper.fromId(null)).isNull();
    }


    @Test
    @Transactional
    void createCompanyWithExistingEmail() throws Exception {

        companyRepository.saveAndFlush(company);

        Company updatedCompany = createEntity(em);

        // Create the Company with an existing ID
        updatedCompany.setEmail(DEFAULT_COMPANY_EMAIL);
        CompanyDTO companyDTO = companyMapper.toDto(updatedCompany);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize+1);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(updatedCompany);
    }

    @Test
    @Transactional
    void createCompanyWithExistingName() throws Exception {

        companyRepository.saveAndFlush(company);

        Company updatedCompany = createEntity(em);

        // Create the Company with an existing ID
        updatedCompany.setName(DEFAULT_NAME);
        CompanyDTO companyDTO = companyMapper.toDto(updatedCompany);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize+1);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(updatedCompany);
    }


    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        // set the field null
        company.setName(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkEmailIsRequired() throws Exception {
        // set the field null
        company.setEmail(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkPhoneIsRequired() throws Exception {
        // set the field null
        company.setPhone(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkAddressLine1IsRequired() throws Exception {
        // set the field null
        company.setAddressLine1(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkCityIsRequired() throws Exception {
        // set the field null
        company.setCity(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkCountryIsRequired() throws Exception {
        // set the field null
        company.setCountry(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void checkPostcodeIsRequired() throws Exception {
        // set the field null
        company.setPostcode(null);

        // Create the Company, which fails.
        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);
    }

    @Test
    @Transactional
    void getNonExistingCompany() throws Exception {
        // Get the company
        restCompanyMockMvc.perform(get("/api/companies/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    /**
     * Create an entity for this test.
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    static Company createEntity(EntityManager em) {
        Company company = new Company()
            .name(DEFAULT_NAME)
            .email(DEFAULT_COMPANY_EMAIL)
            .phone(DEFAULT_PHONE)
            .addressLine1(DEFAULT_ADDRESS_LINE_1)
            .addressLine2(DEFAULT_ADDRESS_LINE_2)
            .city(DEFAULT_CITY)
            .country(DEFAULT_COUNTRY)
            .postcode(DEFAULT_POSTCODE)
            .companyLogo(DEFAULT_COMPANY_LOGO)
            .companyLogoContentType(DEFAULT_COMPANY_LOGO_CONTENT_TYPE);
        // Add required entity
        Employee employee;
        if (TestUtil.findAll(em, Employee.class).isEmpty()) {
            employee = EmployeeTestIT.createEntity(em);
            em.persist(employee);
            em.flush();
        } else {
            employee = TestUtil.findAll(em, Employee.class).get(0);
        }
        company.getEmployees().add(employee);
        return company;
    }


    @Test
    @Transactional
    void createCompanyWithExistingId() throws Exception {

        // Create the Company with an existing ID
        company.setId(1L);
        CompanyDTO companyDTO = companyMapper.toDto(company);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.idexists")));

        //idexists

        // Validate the Company in the database
        List<Company> companyList = companyRepository.findAll();
        assertThat(companyList).hasSize(initialCompanyDatabaseSize);

        // Validate the Company in Elasticsearch
        verify(mockCompanySearchRepository, times(0)).save(company);
    }

    @Test
    @Transactional
    void createCompanyWithWrongRole() throws Exception {

        securityAwareMockMVC();
        User currentUser = UserResourceIT.createEntity(em);

        List<Authority> authorities = authorityRepository.findAll();

        String authorityName = authorities.stream()
            .map(Authority::getName)
            .filter(authority -> !authority.contains(AuthoritiesConstants.USER))
            .findAny()
            .orElse("");

        Authority manager = new Authority();
        manager.setName(authorityName);
        currentUser.getAuthorities().add(manager);

        em.persist(currentUser);
        em.flush();

        CompanyDTO companyDTO = companyMapper.toDto(company);

        restCompanyMockMvc.perform(post("/api/companies")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .with(user(currentUser.getLogin()))
            .content(TestUtil.convertObjectToJsonBytes(companyDTO)))
            .andExpect(status().isBadRequest());

    }

    @Test
    @Transactional
    void assertThatHireEmployeeBehavesAsIntended() throws Exception {
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

        // Only the manager or the admin can hire users.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany2.getId(), employee_user1.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.companyacceptforbiden")));

        // This application cannot be accepted. This employee is already in a company.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany2.getId(), employee.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.useralreadyinacompany")));

        // The manager cannot approve a application by a employee who is applying to a different company then his.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.cannotapproveemployeeifheapplyestoadiffcompany")));

        // employee_user2 hasn't sent any request to join updatedCompany
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany2.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasrequestlessthen14days")));

        notification1.setEmployee(manager);
        notification1.setSentDate(Instant.now().minus(15, ChronoUnit.DAYS));
        notification1.setFormat(REQUEST_TO_JOIN);
        notification1.setReferenced_user(employee_user2.getEmail());
        notification1.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification1);

        // employee_user2 has sent a request to join updatedCompany but it happened 15 days ago.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany2.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasrequestlessthen14days")));


        notification1.setSentDate(Instant.now().minus(13, ChronoUnit.DAYS));
        notification1.setReferenced_user(employee_user1.getEmail());
        notificationRepository.saveAndFlush(notification1);

        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail())).isPresent();
        Employee newEmployee = employeeRepository.findOneByEmail(employee_user1.getEmail()).get();
        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).doesNotContain(AuthoritiesConstants.EMPLOYEE);

        restCompanyMockMvc.perform(post("/api/companies/{companyId}/hire-employee/{employeeId}", updatedCompany2.getId(), employee_user1.getId()).
            with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());


        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).contains(AuthoritiesConstants.EMPLOYEE);
        assertThat(newEmployee.isHired()).isTrue();
        List<Notification> allNotificationsFromCompany2 = notificationRepository.findAllByCompany(updatedCompany2.getId());
        assertThat(allNotificationsFromCompany2.size()).isEqualTo(4);
        assertThat(notificationRepository.findAllByEmployee(employee_user1).size()).isEqualTo(2);

        assertThat(notificationRepository.findAllByEmployee(newEmployee).stream().map(notification -> notification.getFormat())).containsOnly(WELCOME, ACCEPT_REQUEST);
        assertThat(notificationRepository.findAllByEmployee(manager).stream().map(notification -> notification.getFormat())).containsOnly(REQUEST_TO_JOIN);
        assertThat(notificationRepository.findAllByEmployee(employee).stream().map(notification -> notification.getFormat())).containsOnly(NEW_EMPLOYEE);
        assertThat(notificationRepository.findAllByEmployee(newEmployee).stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(newEmployee.getCompany().getId()).isEqualTo(updatedCompany2.getId());
        List<Notification> allNot = notificationRepository.findAll();
        assertThat(allNot.size()).isEqualTo(initialNotificationsDatabaseSize+4);

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
    void assertThatRejectEmployeeBehavesAsIntended() throws Exception {

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

        // Only the manager or the admin can reject user's applications.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/reject-employee/{employeeId}", updatedCompany2.getId(), employee_user1.getId())
            .with(user(user_two.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.companyremoveforbiden")));

        // Only the manager or the admin can reject user's applications.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/reject-employee/{employeeId}", updatedCompany.getId(), employee_user1.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.cannotrejectemployeeifheapplyestoadiffcompany")));

        // employee_user2 hasn't sent any request to join updatedCompany
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/reject-employee/{employeeId}", updatedCompany2.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasrequestlessthen14days")));

        notification1.setEmployee(manager);
        notification1.setSentDate(Instant.now().minus(15, ChronoUnit.DAYS));
        notification1.setFormat(REQUEST_TO_JOIN);
        notification1.setReferenced_user(employee_user2.getEmail());
        notification1.setCompany(updatedCompany2.getId());
        notificationRepository.saveAndFlush(notification1);

        // employee_user2 has sent a request to join updatedCompany but it happened 15 days ago.
        restCompanyMockMvc.perform(post("/api/companies/{companyId}/reject-employee/{employeeId}", updatedCompany2.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message", Matchers.equalTo("error.hasrequestlessthen14days")));

        notification1.setSentDate(Instant.now().minus(13, ChronoUnit.DAYS));
        notification1.setReferenced_user(employee_user1.getEmail());
        notificationRepository.saveAndFlush(notification1);

        assertThat(employeeRepository.findOneByEmail(employee_user1.getEmail())).isPresent();
        Employee newEmployee = employeeRepository.findOneByEmail(employee_user2.getEmail()).get();
        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).doesNotContain(AuthoritiesConstants.EMPLOYEE);

        restCompanyMockMvc.perform(post("/api/companies/{companyId}/reject-employee/{employeeId}", updatedCompany2.getId(), employee_user2.getId())
            .with(user(user_manager.getLogin().toLowerCase()))
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        assertThat(newEmployee.getUser().getAuthorities().stream().map(Authority::getName)).doesNotContain(AuthoritiesConstants.EMPLOYEE);
        assertThat(newEmployee.isHired()).isFalse();
        List<Notification> allNotificationsFromCompany2 = notificationRepository.findAllByCompany(updatedCompany2.getId());
        assertThat(allNotificationsFromCompany2.size()).isEqualTo(2);

        assertThat( notificationRepository.findAllByEmployee(employee_user2).size()).isEqualTo(1);

        assertThat(notificationRepository.findAllByEmployee(newEmployee).stream().map(notification -> notification.getFormat())).containsOnly(REJECT_REQUEST);
        assertThat(notificationRepository.findAllByEmployee(manager).stream().map(notification -> notification.getFormat())).containsOnly(REQUEST_TO_JOIN);
        assertThat(notificationRepository.findAllByEmployee(employee).stream().map(notification -> notification.getFormat())).doesNotContain(NEW_EMPLOYEE);
        assertThat(notificationRepository.findAllByEmployee(newEmployee).stream().findFirst().get().getCompany()).isEqualTo(updatedCompany2.getId());
        assertThat(newEmployee.getCompany()).isNull();

        assertThat(notificationRepository.findAll().size()).isEqualTo(initialNotificationsDatabaseSize+2);

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
        restCompanyMockMvc = MockMvcBuilders
            .webAppContextSetup(context)
            .apply(springSecurity())
            .build();
    }

}
