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


    //TODO                                                  ****API's****
        //TODO 1. api/companies
            //TODO POST Create a company and automatically become the manager
                    //TODO If the user is ROLE_MANAGER or ROLE_EMPLOYEE he cannot see this option otherwise he can
                    // Only the ROLE_USER can create a company
                    // He then automatically becomes MANAGER
            //TODO GET List all the companies details
                      //TODO manager and employee can see only his company, admin and user can see all companies
            //TODO DELETE Delete a company
                    //TODO Manager can delete only his company, admin can delete any company.
                    // Get all the employees from the company and remove the employee role. Remove the managers roles
                    // Delete the company update the employee company id
            //TODO PUT Update a company (manager his , admin any)
                    //TODO Only if you are a manager or admin you can update the company (except the company's email)
                    //TODO The current user/employee needs to be in a company
                    //TODO The manager can only update his company. The admin can updated any companies
                    //TODO Neither the manager nor the admin can update, add nor remove the employees from this endpoint
        //TODO 2. api/activate
                // GET When activating the user also create and links to an employee (NEEDS TEST)
        //TODO 3. api/employee/employeeId/request-to-join
            //TODO POST(companyName) Request to join a company
                // * If the user is ROLE_MANAGER and ROLE_EMPLOYEE he cannot see this option
                // * Sends a email to the company's manager to request to join the company
                // * Creates a notification and sends it to the manager
        //TODO 4. api/invite-to-join (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
            //TODO POST(email)
                // * If the user exists and is not ROLE_MANAGER and ROLE_EMPLOYEE sends a notification and an email to the user
                // * If the user don't exists send an email with link to registration page
        //TODO 5. api/employees/{employeeId}/approve-employee (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                // * The manager or the admin approves the users request.
                // * The user gets the ROLE_EMPLOYEE and it is added to the company (save the user, employee and the company)
                // * Sends a email to the user to inform him that his request has been approved
                // * Also sends a notification to the user to inform him that his request has been approved.
        //TODO 5. api/employees/{employeeId}/reject-employee
                    // * The manager or the admin rejects the users request.
                    // * Sends a email to the user to inform him that his request has been rejected
                    // * Also sends a notification to the user to inform him that his request has been rejected.
        //TODO 6. api/employees/{employeeId}/fire-employee (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                    // The manager or the admin can fire an employee.
                    // Removes all the users roles except the default ROLE_USER (update the user, the employee and the company)
                    // Send a notification to the user informing him that he got fired
        //TODO 7. api/employees/{employeeId}/leave-company (Pre-Authorize ROLE_EMPLOYEE)
                    // The employee resigns from the company
                    // Removes all the users roles except the default ROLE_USER (update the user, the employee and the company)
                    // Send a notification to all the employees from the company to inform that he is leaving the company
        //TODO 8. api/users (Pre-Authorize ROLE_ADMIN and ROLE_MANAGER only for modifying the user's role )
                // POST When admin creates a user also creates an employee
                // DELETE when admin deletes an employee it also deletes the linked user and updates the company if he is in one
                // TODO PUT If you are a manager you can only modify the roles of users in your company (the email cannot be modified)
                    //When user is updated the employee is updated as well.
        //TODO 9. api/employees

                // TODO POST  No one can create an employee. An employee is created only when the user is activated
                // TODO GET (ADMIN can see all, the rest can only see their own account)
                // TODO DELETE  (Pre-Authorize ROLE_ADMIN) when admin deletes an employee it also deletes the linked user and updates the company if he is in one. Also delete all notification related to this employee
                // TODO PUT  (ADMIN can update all, Manager can all from his company,  the rest can only update their own account)
                        // When employee is updated the user is updated as well (no one can updated the email). The admin and managers can also update the employee roles



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
