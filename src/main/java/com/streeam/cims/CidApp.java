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


    //                                                  ****API's****

        // 1. api/companies
            // TODO CREATE MORE TESTS FOR THE MAILSERVICE TEST CLASS
            // POST Create a company and automatically become the manager
                    // If the user is ROLE_MANAGER or ROLE_EMPLOYEE he cannot see this option otherwise he can
                    // Only the ROLE_USER can create a company
                    // He then automatically becomes MANAGER
            // GET List all the companies details
                      // manager and employee can see only his company, admin and user can see all companies
            // DELETE Delete a company
                    // Manager can delete only his company, admin can delete any company.
                    // Get all the employees from the company and remove the employee role. Remove the managers roles
                    // Delete the company update the employee company id
                    // Send notifications and emails to all employees that they all been fired
            // PUT Update a company (manager his , admin any)
                    // Only if you are a manager or admin you can update the company (except the company's email)
                    // The current user/employee needs to be in a company
                    // The manager can only update his company. The admin can updated any companies
                    // Neither the manager nor the admin can update can add nor remove the employees from this endpoint
        // 2. api/activate
                //  GET When activating the user also create and links to an employee
        // 3. api/employee/{employeeId}/request-to-join/{companyId}
            // POST Request to join a company
                //   If the user is ROLE_MANAGER and ROLE_EMPLOYEE he cannot see this option
                //   Sends a email to the company's manager to request to join the company
                //   Creates a notification and sends it to the manager
                //  A employee cannot apply to join the same company in a period less then 3 days.
                //TODO NEEDS TESTING
        // 4. POST api/employees/invite-to-join (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                //  The manager or the admin can see all the unemployed users.
                //  The logged user has to be either the admin or the manager of the company.
        // 5. POST api/employees/invite-to-join/{email} (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                //  The logged user has to be either the admin or the manager of the company.
                //  If the user exists and is not ROLE_MANAGER and ROLE_EMPLOYEE sends an invite notification and an email to the user.
                //  If the user doesn't exists sends a notification to the current user.
                //  When the users registers automatically activate the account and send an invite notification
                //  If the user was invited by multiple companies send an invite notification for each company.
        // 6. api/companies/{companyId}/approve-employee/{employeeId}(Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                //  The manager or the admin approves the users request. The manager can only approve employees that apply to join his company.
                //  Check to see if the employee is already taken by another company.
                //  The user gets the ROLE_EMPLOYEE and it is added to the company (save the user, employee and the company)
                //  Sends a email to the user to inform him that his request has been approved
                //  Also sends a notification to the user to inform him that his request has been approved.
                //TODO NEEDS TESTING
        // 7. api/companies/{companyId}/reject-employee/{employeeId}
                    //  Only the manager or the admin rejects the users request. The manager can only reject employees that apply to join his company.
                    //  The employee must not have the role of manager nor employee and he is must not be part of a company.
                    //  Sends a email to the user to inform him that his request has been rejected
                    //  Also sends a notification to the user to inform him that his request has been rejected.
                    //TODO NEEDS TESTING
        // 8. api/companies/{companyId}/fire/{employeeId}
                // (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
                    // The manager or the admin can fire an employee.
                    //  A manager cannot fire himself, but he can quit. If he quits the company is dissolved (see ../companies/delete/{companyId})
                    //  Removes all the employee's roles except the default ROLE_USER (update the user, the employee and the company)
                    //  Send a notification and a email to the user informing him that he got fired
                    //TODO NEEDS TESTING
        // 9. api/companies/{companyId}/leave-company (Pre-Authorize ROLE_EMPLOYEE)
            // POST leave a company.
                    //  Only an user with the employee role can access this endpoint.
                    // TODO The employee resigns from the company
                    // TODO Removes all the users roles except the default ROLE_USER (update the user, the employee and the company)
                    // TODO Send a notification to all the employees from the company to inform that he is leaving the company
                    // TODO Send email to manager to inform him that he left.
        // 10. api/users (Pre-Authorize ROLE_ADMIN and ROLE_MANAGER only for modifying the user's role )
                // TODO POST When admin creates a user also creates an employee
                // TODO DELETE when admin deletes an employee it also deletes the linked user and updates the company if he is in one
                // TODO GET Employees and Managers can see their roles in the company
                // PUT If you are a manager you can only modify the roles of users in your company (the email cannot be modified)
                    // TODO When user is updated the employee is updated as well.
        // 11. api/employees

                //  POST  No one can create an employee. An employee is created only when the user is activated
                //  GET (ADMIN can see all, the rest can only see their own account)
                //  DELETE  (Pre-Authorize ROLE_ADMIN) when admin deletes an employee it also deletes the linked user and updates the company if he is in one. Also delete all notification related to this employee
                //  PUT  (ADMIN can update all, Manager can update all from his company,  the rest can only update their own account)
                        //TODO When employee is updated the user is updated as well (no one can updated the email). The admin and managers can also update the employee roles



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
