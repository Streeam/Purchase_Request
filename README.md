# Company Information Database

This is a monolithic application where a user can choose to either to create his own company or join an existing one. This application can easily be turned into
a gateway microservice.
#####There are 4 roles that a user can have:

1. ROLE_USER - Every user after registration is given the this role. This role can never be replaced. All other roles will be added on top of this role.
2. ROLE_ADMIN - Only one user can be in this role. This user can create, see, update and delete almost anything.
3. ROLE_MANAGER - A user is given this role when he creates his own company.
4. ROLE_EMPLOYEE - A user is given this role when he joins a company. A user cannot have the ROLE_EMPLOYEE
   and ROLE_MANAGER at the same time.

## Endpoints

         1. api/companies
             POST api/companies
                     Create a company and automatically become the manager
                     If the user has ROLE_MANAGER or ROLE_EMPLOYEE he cannot see this option otherwise he can.
                     Only the ROLE_USER can create a company.
                     He then automatically becomes the manager of the company.
             GET api/companies
                     List all the companies and its details.
                     Manager and employees can see only their company, admin and user can see all companies.
             DELETE api/companies/{companyId}
                     Delete a company
                     Manager can delete only his company, admin can delete any company.
                     Get all the employees from the company and remove the ROLE_EMPLOYEE. Remove the ROLE_MANAGER from the company's manager.
                     Delete the company update the employee company id
                     Send notifications and emails to all employees that they all been fired
             PUT api/companies/{companyId}
                     Update a company. Manager can only update his company. The admin can update any companies.
                     Only if you are a manager or an admin you can update the company.
                     Neither the manager nor the admin can update, add nor remove the employees from this endpoint
         2. api/activate
             GET When the user is activated it also creates and links to an employee.
         3. api/employee/{employeeId}/request-to-join/{companyId}
             POST Request to join a company
                   The users with the roles ROLE_MANAGER and ROLE_EMPLOYEE are restricted from using this endpoint.
                   Sends a email to the company's manager to request to join the company.
                   Also creates a notification and sends it to the manager.
                   A employee cannot apply to join the same company in a period less then 3 days.
                   The employee can request to join more then one company.
                   TODO FRONTEND
         4. api/employees/unemployed (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
             GET List all unemployed
                  The manager or the admin can see all the unemployed users from all companies.
                  The logged user has to be either the admin or the manager of the company.
                  TODO NEEDS TESTING
                  TODO FRONTEND
         5. api/employees/invite-to-join/{email} (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
             POST Invite join a company
                  The logged user has to be either the admin or the manager of the company.
                  If the user exists and is not ROLE_MANAGER nor ROLE_EMPLOYEE sends an invite notification and an email to the user.
                  If the user doesn't exists sends a notification to the current user(manager or admin).
                  When the users registers automatically activate the account and send an invite notification
                  If the user was invited by multiple companies send an invite notification for each company.
                  TODO FRONTEND
         6. api/companies/{companyId}/approve-employee/{employeeId}(Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
             POST approve a request from an employee
                  The manager or the admin approves the users request. The manager can only approve employees that apply to join his company.
                  Check to see if the employee is already taken by another company.
                  The user is given the ROLE_EMPLOYEE and it is added to the company.
                  Sends a email and a notification to the user to inform him that his request has been approved.
                  TODO NEEDS TESTING
                  TODO FRONTEND
         7. api/companies/{companyId}/reject-employee/{employeeId}
             POST reject a request from an employee
                  Only the manager or the admin rejects the users request. The manager can only reject employees that apply to join his company.
                  The user must not have the role of manager nor employee and he must not be part of a company.
                  Sends an email and a notification to the user to inform him that his request has been rejected
                  TODO NEEDS TESTING
                  TODO FRONTEND
         8. api/companies/{companyId}/fire/{employeeId}  (Pre-Authorize ROLE_MANAGER or ROLE_ADMIN)
             POST Fire a employee from a specific company
                  Only the manager or the admin can fire an employee.
                  A manager cannot fire himself, but he can quit. If he quits, the company is dissolved (see ../companies/delete/{companyId})
                  Removes all the employee's roles except the default ROLE_USER.
                  Send a notification and a email to the user informing him that he got fired.
                  TODO NEEDS TESTING
                  TODO FRONTEND
         9. api/companies/{companyId}/leave-company (Pre-Authorize ROLE_EMPLOYEE)
             POST Employee resigns from his company
                  Only an user with the employee role can access this endpoint.
                  The employee resigns from the company
                  Removes all the users roles except the default ROLE_USER (update the user, the employee and the company)
                  TODO Send a notification to all the employees from the company to inform that he is leaving the company
                  Send email to manager to inform him that he left.
                  TODO NEEDS TESTING
                  TODO FRONTEND
         10. api/users (Pre-Authorize ROLE_ADMIN and ROLE_MANAGER only for modifying the user's role )
             TODO POST When admin creates a user also creates an employee
             TODO DELETE when admin deletes an employee it also deletes the linked user and updates the company if he is in one
             TODO GET Employees and Managers can see their roles in the company
             PUT If you are a manager you can only modify the roles of users in your company (the email cannot be modified)
                  TODO When user is updated the employee is updated as well.
         11. api/employees
              POST  No one can create an employee. An employee is created only when the user is activated.
              GET ADMIN can see all employees, the rest can only see their own account.
              DELETE  (Pre-Authorize ROLE_ADMIN) when admin deletes an employee it also deletes the linked user and updates the company if he is in one. Also delete all notification related to this employee
              PUT  ADMIN can update all employees, Manager can update all employees from his company,  the rest can only update their own account.
                   TODO When employee is updated the user is updated as well (no one can updated the employee's or the user's email). The admin and managers can also update the employee roles.

## User Interface Wireframe

![Image](./src/main/webapp/content/images/ui.jpg?raw=true 'Title')

## Development

Before you can build this project, you must install and configure the following dependencies on your machine:

1. [Node.js][]: We use Node to run a development web server and build the project.
   Depending on your system, you can install Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools.
You will only need to run this command when dependencies change in [package.json](package.json).

    npm install

We use npm scripts and [Webpack][] as our build system.

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./gradlew
    npm start / yarn start

Npm is also used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in [package.json](package.json). You can also run `npm update` and `npm install` to manage dependencies.
Add the `help` flag on any command to see how you can use it. For example, `npm help update`.

The `npm run` command will list all of the scripts available to run for this project.

### PWA Support

JHipster ships with PWA (Progressive Web App) support, and it's disabled by default. One of the main components of a PWA is a service worker.

The service worker initialization code is commented out by default. To enable it, uncomment the following code in `src/main/webapp/index.html`:

```html
<script>
  if ('serviceWorker' in navigator) {
    navigator.serviceWorker.register('./service-worker.js').then(function() {
      console.log('Service Worker Registered');
    });
  }
</script>
```

Note: [Workbox](https://developers.google.com/web/tools/workbox/) powers JHipster's service worker. It dynamically generates the `service-worker.js` file.

### Managing dependencies

For example, to add [Leaflet][] library as a runtime dependency of your application, you would run following command:

    npm install --save --save-exact leaflet

To benefit from TypeScript type definitions from [DefinitelyTyped][] repository in development, you would run following command:

    npm install --save-dev --save-exact @types/leaflet

Then you would import the JS and CSS files specified in library's installation instructions so that [Webpack][] knows about them:
Note: There are still a few other things remaining to do for Leaflet that we won't detail here.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].

## Building for production

### Packaging as jar

To build the final jar and optimize the CID application for production, run:

    ./gradlew -Pprod clean bootJar

This will concatenate and minify the client CSS and JavaScript files. It will also modify `index.html` so it references these new files.
To ensure everything worked, run:

    java -jar build/libs/*.jar

Then navigate to [http://localhost:8080](http://localhost:8080) in your browser.

Refer to [Using JHipster in production][] for more details.

### Packaging as war

To package your application as a war in order to deploy it to an application server, run:

    ./gradlew -Pprod -Pwar clean bootWar

## Testing

To launch your application's tests, run:

    ./gradlew test integrationTest

### Client tests

Unit tests are run by [Jest][] and written with [Jasmine][]. They're located in [src/test/javascript/](src/test/javascript/) and can be run with:

    npm test

For more information, refer to the [Running tests page][].

### Code quality

Sonar is used to analyse code quality. You can start a local Sonar server (accessible on http://localhost:9001) with:

```
docker-compose -f src/main/docker/sonar.yml up -d
```

You can run a Sonar analysis with using the [sonar-scanner](https://docs.sonarqube.org/display/SCAN/Analyzing+with+SonarQube+Scanner) or by using the gradle plugin.

Then, run a Sonar analysis:

```
./gradlew -Pprod clean check sonarqube
```

For more information, refer to the [Code quality page][].

## Using Docker to simplify development (optional)

You can use Docker to improve your JHipster development experience. A number of docker-compose configuration are available in the [src/main/docker](src/main/docker) folder to launch required third party services.

For example, to start a postgresql database in a docker container, run:

    docker-compose -f src/main/docker/postgresql.yml up -d

To stop it and remove the container, run:

    docker-compose -f src/main/docker/postgresql.yml down

You can also fully dockerize your application and all the services that it depends on.
To achieve this, first build a docker image of your app by running:

    ./gradlew bootJar -Pprod jibDockerBuild

Then run:

    docker-compose -f src/main/docker/app.yml up -d

For more information refer to [Using Docker and Docker-Compose][], this page also contains information on the docker-compose sub-generator (`jhipster docker-compose`), which is able to generate docker configurations for one or several JHipster applications.

## Continuous Integration (optional)

To configure CI for your project, run the ci-cd sub-generator (`jhipster ci-cd`), this will let you generate configuration files for a number of Continuous Integration systems. Consult the [Setting up Continuous Integration][] page for more information.

[jhipster homepage and latest documentation]: https://www.jhipster.tech
[jhipster 6.2.0 archive]: https://www.jhipster.tech/documentation-archive/v6.2.0
[using jhipster in development]: https://www.jhipster.tech/documentation-archive/v6.2.0/development/
[using docker and docker-compose]: https://www.jhipster.tech/documentation-archive/v6.2.0/docker-compose
[using jhipster in production]: https://www.jhipster.tech/documentation-archive/v6.2.0/production/
[running tests page]: https://www.jhipster.tech/documentation-archive/v6.2.0/running-tests/
[code quality page]: https://www.jhipster.tech/documentation-archive/v6.2.0/code-quality/
[setting up continuous integration]: https://www.jhipster.tech/documentation-archive/v6.2.0/setting-up-ci/
[node.js]: https://nodejs.org/
[yarn]: https://yarnpkg.org/
[webpack]: https://webpack.github.io/
[angular cli]: https://cli.angular.io/
[browsersync]: http://www.browsersync.io/
[jest]: https://facebook.github.io/jest/
[jasmine]: http://jasmine.github.io/2.0/introduction.html
[protractor]: https://angular.github.io/protractor/
[leaflet]: http://leafletjs.com/
[definitelytyped]: http://definitelytyped.org/
