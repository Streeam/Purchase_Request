package com.streeam.cims.web.rest;

import com.streeam.cims.CidApp;
import com.streeam.cims.domain.Company;
import com.streeam.cims.repository.AuthorityRepository;
import com.streeam.cims.repository.CompanyRepository;
import com.streeam.cims.repository.EmployeeRepository;
import com.streeam.cims.repository.UserRepository;
import com.streeam.cims.repository.search.CompanySearchRepository;
import com.streeam.cims.service.CompanyService;
import com.streeam.cims.service.EmployeeService;
import com.streeam.cims.service.MailService;
import com.streeam.cims.service.mapper.CompanyMapper;
import com.streeam.cims.web.rest.errors.ExceptionTranslator;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.Validator;
import org.springframework.web.context.WebApplicationContext;

import javax.persistence.EntityManager;

import static com.streeam.cims.web.rest.TestUtil.createFormattingConversionService;

/**
 * Integration tests for the {@link CompanyResource} REST controller.
 */
@SpringBootTest(classes = CidApp.class)
class CompanyResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "c1@t.<$";
    private static final String UPDATED_EMAIL = "7@*L.Wn";

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

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private  AuthorityRepository authorityRepository;

    @Autowired
    private MailService mailService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private CompanyService companyService;

    @Autowired
    private EmployeeService employeeService;

    /**
     * This repository is mocked in the com.streeam.cims.repository.search test package.
     *
     * @see com.streeam.cims.repository.search.CompanySearchRepositoryMockConfiguration
     */
    @Autowired
    private CompanySearchRepository mockCompanySearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restCompanyMockMvc;

    private Company company;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Mock
    private CompanyRepository mockCompanyRepository;


    @BeforeEach
    void setup() {
        MockitoAnnotations.initMocks(this);

        final CompanyResource companyResource = new CompanyResource(companyService, authorityRepository, mailService);
        this.restCompanyMockMvc = MockMvcBuilders.standaloneSetup(companyResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }











}
