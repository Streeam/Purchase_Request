package com.streeam.cims.service;

import com.streeam.cims.CidApp;
import com.streeam.cims.domain.Authority;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Integration tests for {@link CompanyService}, {@link EmployeeService}, {@link UserService}  .
 */
@SpringBootTest(classes = CidApp.class)
@Transactional
public class CompanyServiceTestIT {

    //**********COMPANY DEFAULT VALUES******************

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COMPANY_EMAIL = "dummy@localhost.com";
    private static final String UPDATED_EMAIL = "dummy2@localhost.co.uk";

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

    //***************EMPLOYEE DEFAULT VALUES*********
    private static final String DEFAULT_EMPLOYEE_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_EMPLOYEE_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMPLOYEE_EMAIL = "$+@#=.\"m";
    private static final String UPDATED_EMPLOYEE_EMAIL = "P^@v.g";

    private static final Boolean DEFAULT_HIRED = false;
    private static final Boolean UPDATED_HIRED = true;

    private static final byte[] DEFAULT_EMPLOYEE_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_EMPLOYEE_IMAGE = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_EMPLOYEE_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE = "image/png";


    //***************USER DEFAULT VALUES**************
    private static final String DEFAULT_USER_LOGIN = "johndoe";

    private static final String DEFAULT_USER_EMAIL = "johndoe@localhost.com";

    private static final String DEFAULT_USER_FIRSTNAME = "john";

    private static final String DEFAULT_USER_LASTNAME = "doe";

    private static final String DEFAULT_USER_IMAGEURL = "http://placehold.it/50x50";

    private static final String DEFAULT_USER_LANGKEY = "dummy";


    //***************USER2 DEFAULT VALUES**************
    private static final String DEFAULT_USER2_LOGIN = "legolas";

    private static final String DEFAULT_USER2_EMAIL = "aragors@localhost.com";

    private static final String DEFAULT_USER2_FIRSTNAME = "gimli";

    private static final String DEFAULT_USER2_LASTNAME = "frodo";

    private static final String DEFAULT_USER2_IMAGEURL = "http://placehold.it/50x50";

    private static final String DEFAULT_USER2_LANGKEY = "elvish";

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

    private Employee employee, employee2;

    private User user, user2;

    private Company company, company2;


    @BeforeEach
    public void init() {



        user = new User();
        user.setLogin(DEFAULT_USER_LOGIN);
        user.setPassword(RandomStringUtils.random(60));
        user.setActivated(true);
        user.setEmail(DEFAULT_USER_EMAIL);
        user.setFirstName(DEFAULT_USER_FIRSTNAME);
        user.setLastName(DEFAULT_USER_LASTNAME);
        user.setImageUrl(DEFAULT_USER_IMAGEURL);
        user.setLangKey(DEFAULT_USER_LANGKEY);
        allocateAuthority(AuthoritiesConstants.USER, user);


        user2 = new User();
        user2.setLogin(DEFAULT_USER2_LOGIN);
        user2.setPassword(RandomStringUtils.random(60));
        user2.setActivated(true);
        user2.setEmail(DEFAULT_USER2_EMAIL);
        user2.setFirstName(DEFAULT_USER2_FIRSTNAME);
        user2.setLastName(DEFAULT_USER2_LASTNAME);
        user2.setImageUrl(DEFAULT_USER2_IMAGEURL);
        user2.setLangKey(DEFAULT_USER2_LANGKEY);
        allocateAuthority(AuthoritiesConstants.USER, user2);

        when(dateTimeProvider.getNow()).thenReturn(Optional.of(LocalDateTime.now()));
        auditingHandler.setDateTimeProvider(dateTimeProvider);

        employee = new Employee()
            .login(DEFAULT_USER_LOGIN)
            .firstName(DEFAULT_EMPLOYEE_FIRST_NAME)
            .lastName(DEFAULT_EMPLOYEE_LAST_NAME)
            .email(DEFAULT_EMPLOYEE_EMAIL)
            .hired(DEFAULT_HIRED)
            .image(DEFAULT_EMPLOYEE_IMAGE)
            .imageContentType(DEFAULT_EMPLOYEE_IMAGE_CONTENT_TYPE)
            .user(user);

        employee2 = new Employee()
            .login(DEFAULT_USER2_LOGIN)
            .firstName(UPDATED_EMPLOYEE_FIRST_NAME)
            .lastName(UPDATED_EMPLOYEE_LAST_NAME)
            .email(DEFAULT_USER2_EMAIL)
            .hired(UPDATED_HIRED)
            .image(UPDATED_EMPLOYEE_IMAGE)
            .imageContentType(UPDATED_EMPLOYEE_IMAGE_CONTENT_TYPE)
            .user(user2);

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
            .addEmployees(employee)
            .addEmployees(employee2);
    }

    @Test
    @Transactional
    public void assertThatCompanyEmailAlreadyExists() {

        userRepository.saveAndFlush(user);
        userRepository.saveAndFlush(user2);

        employeeRepository.saveAndFlush(employee);
        employeeRepository.saveAndFlush(employee2);


        companyRepository.saveAndFlush(company);



        assertThat(companyService.companyEmailAlreadyExists(DEFAULT_COMPANY_EMAIL)).isTrue();
        assertThat(companyService.companyNameAlreadyExists(DEFAULT_NAME)).isTrue();

        userRepository.delete(user);
        employeeRepository.delete(employee);
        companyRepository.delete(company);



    }

    @Test
    @Transactional
    public void assertThatCreateEmployeeFromUser() {
        userRepository.saveAndFlush(user);

        Employee employee = employeeService.createEmployeeFromUser(user);
        assertThat(employee.getLogin()).isEqualTo(DEFAULT_USER_LOGIN);
        assertThat(employee.isHired()).isFalse();
        assertThat(employeeService.findOneByLogin(user.getLogin())).isPresent();

        verify(mockEmployeeSearchRepository, times(1)).save(employee);

        employeeService.delete(employee.getId());

        verify(mockEmployeeSearchRepository, times(1)).deleteById(employee.getId());
    }

    @Test
    @Transactional
    public void assertThatSaveWithCompany() {
        company.setEmployees(null);
        userRepository.saveAndFlush(user);
        companyRepository.saveAndFlush(company);

        employeeService.saveWithCompany(employee, company);
        Optional<Company> maybeCompany =companyRepository.findOneByEmployees(Collections.singleton(employee));
        assertThat(maybeCompany).isPresent();
        assertThat(maybeCompany.get()).isEqualTo(company);

        verify(mockEmployeeSearchRepository, times(1)).save(employee);

        employeeService.delete(employee.getId());
        companyRepository.delete(company);
    }

    @Test
    @Transactional
    public void assertThatCheckIfUserHasRoles() {

        allocateAuthority(AuthoritiesConstants.MANAGER, user);

        assertThat(userService.checkIfUserHasRoles(user , AuthoritiesConstants.MANAGER,  AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user ,  AuthoritiesConstants.EMPLOYEE)).isFalse();

        assertThat(userService.checkIfUserHasRoles(user , AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user , AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2 , AuthoritiesConstants.USER)).isTrue();
    }


    //void delete(Long id)

    @Test
    @Transactional
    public void assertThatWhenCompanyIsRemovedItRemovesTheUsersRoles() {
        int initialEmployeesInACompany = company.getEmployees().size();

        allocateAuthority(AuthoritiesConstants.MANAGER, user);

        userRepository.saveAndFlush(user);

        allocateAuthority(AuthoritiesConstants.EMPLOYEE, user2);

        userRepository.saveAndFlush(user2);
        employeeRepository.saveAndFlush(employee2);
        employeeRepository.saveAndFlush(employee);
        companyRepository.saveAndFlush(company);

        assertThat(companyRepository.findOneById(company.getId()).get().getEmployees().size()).isEqualTo(initialEmployeesInACompany);
        assertThat(companyRepository.findOneById(company.getId()).get().getEmployees().stream().findAny()).isPresent();
        assertThat(userRepository.findOneByLogin(employee.getLogin())).isPresent();

        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.EMPLOYEE)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user, AuthoritiesConstants.MANAGER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user, AuthoritiesConstants.USER)).isTrue();

        companyService.delete(company.getId());

        assertThat(userService.checkIfUserHasRoles(user, AuthoritiesConstants.MANAGER)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.EMPLOYEE)).isFalse();
        assertThat(userService.checkIfUserHasRoles(user2, AuthoritiesConstants.USER)).isTrue();
        assertThat(userService.checkIfUserHasRoles(user, AuthoritiesConstants.USER)).isTrue();



        employeeService.delete(employee.getId());
        employeeService.delete(employee2.getId());
    }


    private Set<Authority> allocateAuthority(String role, User user) {
        Set<Authority> authorities  = new HashSet<>();
        Authority authority = new Authority();
        authority.setName(role);
        authorities.add(authority);
        user.getAuthorities().add(authority);
        return authorities;
    }
}
