package com.streeam.cims.service;

import com.streeam.cims.CidApp;
import com.streeam.cims.domain.Company;
import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.User;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.repository.CompanyRepository;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.UserRepository;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.repository.search.UserSearchRepository;
import com.streeam.cims.security.AuthoritiesConstants;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.service.mapper.EmployeeMapper;
import com.streeam.cims.web.rest.TestUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for {@link CompanyService}, {@link EmployeeService}, {@link UserService}  .
 */
@SpringBootTest(classes = CidApp.class)
@Transactional
class CompanyEmployeeServiceTestIT {

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

    private static final String DEFAULT_EMPLOYEE_EMAIL = "$+@#=.\"m";
    private static final String UPDATED_EMPLOYEE_EMAIL = "P^@v.g";
    private static final String DEFAULT_EMPLOYEE2_EMAIL = "$++&@#=.\"m";
    private static final String UPDATED_EMPLOYEE2_EMAIL = "P^IU@v.g";

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

    private Employee employee1, employee2, employee3, employee4;

    private User user1, user2, user3, user4;

    private Company company, company2;


    @BeforeEach
    void init() {



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
    void assertThatCompanyEmailAlreadyExists() {

        userRepository.saveAndFlush(user1);
        userRepository.saveAndFlush(user2);

        employeeRepository.saveAndFlush(employee1);
        employeeRepository.saveAndFlush(employee2);


        companyRepository.saveAndFlush(company);



        assertThat(companyService.companyEmailAlreadyExists(DEFAULT_COMPANY_EMAIL)).isTrue();
        assertThat(companyService.companyNameAlreadyExists(DEFAULT_NAME)).isTrue();

        userRepository.delete(user1);
        employeeRepository.delete(employee1);
        companyRepository.delete(company);



    }

    @Test
    @Transactional
    void assertThatCreateEmployeeFromUser() {
        userRepository.saveAndFlush(user1);

        Employee employee = employeeService.createEmployeeFromUser(user1);
        assertThat(employee.getLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(employee.isHired()).isFalse();
        assertThat(employeeService.findOneByLogin(user1.getLogin())).isPresent();

        verify(mockEmployeeSearchRepository, times(1)).save(employee);

        employeeService.delete(employee.getId());

        verify(mockEmployeeSearchRepository, times(1)).deleteById(employee.getId());
    }

    @Test
    @Transactional
    void assertThatSaveWithCompany() {
        company.setEmployees(null);
        userRepository.saveAndFlush(user1);
        companyRepository.saveAndFlush(company);

        employeeService.saveWithCompany(employee1, company);
        Optional<Company> maybeCompany =companyRepository.findOneByEmployees(Collections.singleton(employee1));
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

        assertThat(userService.checkIfUserHasRoles(user1 , AuthoritiesConstants.MANAGER,  AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1 ,  AuthoritiesConstants.EMPLOYEE)).isFalse();

        assertThat(userService.checkIfUserHasRoles(user1 , AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1 , AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2 , AuthoritiesConstants.USER)).isTrue();
    }

    @Test
    @Transactional
    void assertThatWhenCompanyIsRemovedItRemovesTheUsersRoles() {

        int initialEmployeesInACompany = company.getEmployees().size();

        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);

        userRepository.saveAndFlush(user1);

        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);

        userRepository.saveAndFlush(user2);
        employeeRepository.saveAndFlush(employee2);

        assertThat(companyRepository.findOneById(company.getId()).get().getEmployees().size()).isEqualTo(initialEmployeesInACompany);
        assertThat(companyRepository.findOneById(company.getId()).get().getEmployees().stream().findAny()).isPresent();
        assertThat(userRepository.findOneByLogin(employee1.getLogin())).isPresent();

        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.USER)).isTrue();

        companyService.delete(company.getId());

        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.MANAGER)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.EMPLOYEE)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user1, AuthoritiesConstants.USER)).isTrue();



        employeeService.delete(employee1.getId());
        employeeService.delete(employee2.getId());
    }

    @Test
    @Transactional
    void assertThatUsersCanOnlySeeOwnCompany() {
        int initialCompanies = companyRepository.findAll().size();

        userService.allocateAuthority(AuthoritiesConstants.MANAGER, user1);
        userRepository.saveAndFlush(user1);
        userService.allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);
        userRepository.saveAndFlush(user2);
        userRepository.saveAndFlush(user3);
        userRepository.saveAndFlush(user4);

        employeeRepository.saveAndFlush(employee1); //ROLE_MANAGER
        employeeRepository.saveAndFlush(employee2);//ROLE_EMPLOYEE
        employeeRepository.saveAndFlush(employee3);//ROLE_ADMIN
        employeeRepository.saveAndFlush(employee4);//ROLE_USER

        companyRepository.saveAndFlush(company); // employee1 & employee2
        companyRepository.saveAndFlush(company2);// employee3 & employee4

        assertThat(companyService.findCompanyWithCurrentUser(user1)).size().isEqualTo(1);
        assertThat(companyService.findCompanyWithCurrentUser(user2)).size().isEqualTo(1);
        assertThat(companyService.findCompanyWithCurrentUser(user1).get().collect(Collectors.toList()).get(0).getName()).isEqualTo(DEFAULT_NAME);
        assertThat(companyService.findCompanyWithCurrentUser(user2).get().collect(Collectors.toList()).get(0).getName()).isEqualTo(DEFAULT_NAME);



        companyRepository.delete(company);
        companyRepository.delete(company2);

        employeeRepository.delete(employee1); //ROLE_MANAGER
        employeeRepository.delete(employee2);//ROLE_EMPLOYEE
        employeeRepository.delete(employee3);//ROLE_ADMIN
        employeeRepository.delete(employee4);//ROLE_USER

        userRepository.delete(user1);
        userRepository.delete(user2);
        userRepository.delete(user3);
        userRepository.delete(user4);
    }


}
