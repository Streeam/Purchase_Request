package com.streeam.cims;

import com.streeam.cims.config.ApplicationProperties;
import com.streeam.cims.config.DefaultProfileUtil;

import io.github.jhipster.config.JHipsterConstants;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Collection;

@SpringBootApplication
@EnableConfigurationProperties({LiquibaseProperties.class, ApplicationProperties.class})
public class CidApp implements InitializingBean {

    //TODO  ****SUMMARY****
    //TODO 1. Create Company, Notification and Employee entities.
    //TODO 2. In the companyController API:
            // TODO - Creates a company (verifies if the company name and email haven't been used) and links the user(ROLE_MANAGER) to an employee and to that company
            // TODO - Cannot be manager at two distinct companies
            // TODO - Cannot be employed at two distinct companies
            // TODO - If you are a manager you cannot be an employee at another company
            // TODO - If you are a employee you cannot be an manager at another company
            // TODO - Create an API that send emails to people to  invite them to join the company
            // TODO - Create a notification entity linked to an employee and every time it sends an email sends a notifications as well
            // TODO - Have the option to leave a company as an employee and also delete a company as a manager
            // TODO - Request to join an existing company (if already in a company cannot see this feature)
            // TODO - Send email to the manager for approval. If approved obtains ROLE_EMPLOYEE and receives a link to signin (activated)

    //TODO  ****API'S****
        //TODO 1. api/companies
            //TODO POST Create a company and automatically become the manager (ROLE_MANAGER)
                    // * If the user is ROLE_MANAGER or ROLE_EMPLOYEE he cannot see this option otherwise he can
            //TODO GET List all the companies details (manager can see only his company, admin can see all companies)
            //TODO DELETE Delete a company  (manager can delete only his company, admin can delete any companies)
            //TODO PUT Update a company (manager his , admin any)
        //TODO 2. api/activate
                // GET When activating the user also create and link to an employee (NEEDS TEST)
        //TODO 2. api/all-companies (no restriction)
            //TODO GET List all the company names (this is so a user can find and join a company)
                // * If the user is ROLE_MANAGER and ROLE_EMPLOYEE he cannot see this option otherwise he can
        //TODO 3. api/request-to-join (no restriction)
            //TODO POST(companyName) Request to join a company
                // * If the user is ROLE_MANAGER and ROLE_EMPLOYEE he cannot see this option
                // * If the user is not ROLE_MANAGER nor ROLE_EMPLOYEE he can send a request to join the company
        //TODO 4. api/invite-to-join (Pre-Authorize ROLE_MANAGER)
            //TODO POST(email)
                // * If the user exists and is not ROLE_MANAGER and ROLE_EMPLOYEE sends a notification and an email to the user
                // * If the user don't exists send an email with link to registration page
    //TODO 4. api/approve-employee (employeeId)

    //TODO ***TESTS****

    private static final Logger log = LoggerFactory.getLogger(CidApp.class);

    private final Environment environment;

    public CidApp(Environment environment) {
        this.environment = environment;
    }

    /**
     * Initializes CID.
     * <p>
     * Spring profiles can be configured with a program argument --spring.profiles.active=your-active-profile
     * <p>
     * You can find more information on how profiles work with JHipster on <a href="https://www.jhipster.tech/profiles/">https://www.jhipster.tech/profiles/</a>.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        Collection<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_PRODUCTION)) {
            log.error("You have misconfigured your application! It should not run " +
                "with both the 'dev' and 'prod' profiles at the same time.");
        }
        if (activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT) && activeProfiles.contains(JHipsterConstants.SPRING_PROFILE_CLOUD)) {
            log.error("You have misconfigured your application! It should not " +
                "run with both the 'dev' and 'cloud' profiles at the same time.");
        }
    }

    /**
     * Main method, used to run the application.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(CidApp.class);
        DefaultProfileUtil.addDefaultProfile(app);
        Environment env = app.run(args).getEnvironment();
        logApplicationStartup(env);
    }

    private static void logApplicationStartup(Environment env) {
        String protocol = "http";
        if (env.getProperty("server.ssl.key-store") != null) {
            protocol = "https";
        }
        String serverPort = env.getProperty("server.port");
        String contextPath = env.getProperty("server.servlet.context-path");
        if (StringUtils.isBlank(contextPath)) {
            contextPath = "/";
        }
        String hostAddress = "localhost";
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("The host name could not be determined, using `localhost` as fallback");
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application '{}' is running! Access URLs:\n\t" +
                "Local: \t\t{}://localhost:{}{}\n\t" +
                "External: \t{}://{}:{}{}\n\t" +
                "Profile(s): \t{}\n----------------------------------------------------------",
            env.getProperty("spring.application.name"),
            protocol,
            serverPort,
            contextPath,
            protocol,
            hostAddress,
            serverPort,
            contextPath,
            env.getActiveProfiles());
    }
}
