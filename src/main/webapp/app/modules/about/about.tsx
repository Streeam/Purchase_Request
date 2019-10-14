import React from 'react';

export class About extends React.Component {
  render() {
    return (
      <div className="container">
        <h2>About</h2>
        <div>
          <h5>Company Information Database</h5>
          <blockquote className="blockquote">
            <div style={{ fontSize: '1.1rem' }}>
              <p style={{ paddingLeft: '3em' }}>
                This is an end-to-end full stack monolithic application. The backend server is build using Java and Spring Boot. For
                authorization
              </p>
              <p>
                and authentication, I used Spring Security along with JWT authentication. For storage I used PostgreSQL a relational
                database management system. The front-end application is be built using ReactJS and TypeScript. Loag term goal is to turn
                this application into a gateway microservice and attach several microservices to it.
              </p>
              <p style={{ paddingLeft: '3em' }}>
                When a user first logs in, he is presented with two options. He can create his own company and become the manager of that
                company
              </p>
              <p>
                or he can request to join an existing company. If he creates his own company, he can invite existing users to join his
                company. He can also send emails to invite people who haven't yet registered to the application to sign up. When a user
                accepts an invitation, he becomes part of that company and is given the employee role. The manager can assign roles, he can
                hire or fire employee, he can send invitation and he can even dissolve the company. If the user decides to join a company,
                he can search through all companies and their internal structure. A user can request to join multiple companies, but he can
                only join one. Once the manager approves his request a notification is sent to him informing him that his request has been
                approved. Notifications are also sent to all the company's employees informing them that a new employee has join the
                company.
              </p>
              <p style={{ paddingLeft: '3em' }}>
                <span>There are 4 roles that a user can have:</span>
              </p>
              <ol style={{ fontSize: '1.1rem' }}>
                <li>
                  ROLE_USER - After registration every user is given this role. It can never be replaced. All other roles will be added on
                  top of this role.
                </li>
                <li>ROLE_ADMIN - Only one user can be in this role. This user can create, see, update and delete almost any entity.</li>
                <li>ROLE_MANAGER - A user is given this role when he creates his own company.</li>
                <li>
                  ROLE_EMPLOYEE - A user is given this role when he joins a company. A user cannot have both employee role and manager role
                  at the same time.
                </li>
              </ol>
            </div>
          </blockquote>
        </div>
      </div>
    );
  }
}

export default About;
