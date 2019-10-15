import React, { useState } from 'react';
import { TabContent, Row, Col, Nav, NavLink, NavItem, TabPane, Collapse, Button, CardBody, Card } from 'reactstrap';
import classnames from 'classnames';

export const about = () => {
  const [activeTab, setActiveTab] = useState('1');
  const toggle = tab => {
    if (activeTab !== tab) setActiveTab(tab);
  };
  const firstTab = () => toggle('1');
  const secondTab = () => toggle('2');

  const [collapse, setCollapse] = useState(false);

  const colapsToggle = () => setCollapse(!collapse);

  return (
    <div>
      <Nav tabs>
        <NavItem>
          <NavLink className={classnames({ active: activeTab === '1' })} onClick={firstTab}>
            About
          </NavLink>
        </NavItem>
        <NavItem>
          <NavLink className={classnames({ active: activeTab === '2' })} onClick={secondTab}>
            Api Documentation
          </NavLink>
        </NavItem>
      </Nav>
      <TabContent activeTab={activeTab}>
        <TabPane tabId="1">
          <Row>
            <Col sm="12">
              <br />
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
                        database management system. The front-end application is be built using ReactJS and TypeScript. Loag term goal is to
                        turn this application into a gateway microservice and attach several microservices to it.
                      </p>
                      <p style={{ paddingLeft: '3em' }}>
                        When a user first logs in, he is presented with two options. He can create his own company and become the manager of
                        that company
                      </p>
                      <p>
                        or he can request to join an existing company. If he creates his own company, he can invite existing users to join
                        his company. He can also send emails to invite people who haven't yet registered to the application to sign up. When
                        a user accepts an invitation, he becomes part of that company and is given the employee role. The manager can assign
                        roles, he can hire or fire employee, he can send invitation and he can even dissolve the company. If the user
                        decides to join a company, he can search through all companies and their internal structure. A user can request to
                        join multiple companies, but he can only join one. Once the manager approves his request a notification is sent to
                        him informing him that his request has been approved. Notifications are also sent to all the company's employees
                        informing them that a new employee has join the company.
                      </p>
                      <p style={{ paddingLeft: '3em' }}>
                        <span>There are 4 roles that a user can have:</span>
                      </p>
                      <ol style={{ fontSize: '1.1rem' }}>
                        <li>
                          ROLE_USER - After registration every user is given this role. It can never be replaced. All other roles will be
                          added on top of this role.
                        </li>
                        <li>
                          ROLE_ADMIN - Only one user can be in this role. This user can create, see, update and delete almost any entity.
                        </li>
                        <li>ROLE_MANAGER - A user is given this role when he creates his own company.</li>
                        <li>
                          ROLE_EMPLOYEE - A user is given this role when he joins a company. A user cannot have both employee role and
                          manager role at the same time.
                        </li>
                      </ol>
                    </div>
                  </blockquote>
                </div>
              </div>
            </Col>
          </Row>
        </TabPane>
        <TabPane tabId="2">
          <Row>
            <Col>
              <br />
              <Button color="info" onClick={colapsToggle} style={{ marginBottom: '1rem' }}>
                api/companies
              </Button>
              <Collapse isOpen={collapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}>
                        POST <u>api/companies</u>
                      </p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>Create a company and automatically become the manager</li>
                        <li>Manager and employees can see only their company, admin and user can see all companies.</li>
                        <li>If the user has ROLE_MANAGER or ROLE_EMPLOYEE he cannot see this option otherwise he can.</li>
                        <li>Only the ROLE_USER can create a company.</li>
                        <li>He then automatically becomes the manager of the company.</li>
                      </ul>
                    </div>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}>
                        GET <u>api/companies</u>
                      </p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>List all the companies and its details.</li>
                        <li>Manager and employees can see only their company, admin and user can see all companies.</li>
                      </ul>
                    </div>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}>
                        DELETE <u>api/companies/companyId</u>
                      </p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>Manager can delete only his company, admin can delete any company.</li>
                        <li>Get all the employees from the company and remove the ROLE_EMPLOYEE.</li>
                        <li>Remove the ROLE_MANAGER from the company's manager.</li>
                        <li>Send notifications and emails to all employees that they all been fired</li>
                      </ul>
                    </div>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}>
                        {' '}
                        PUT <u>api/companies/companyId</u>
                      </p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li> Manager can only update his company. The admin can update any companies. </li>
                        <li> Only if you are a manager or an admin you can update the company.</li>
                        <li> Neither the manager nor the admin can update, add nor remove the employees from this endpoint </li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button color="info" onClick={colapsToggle} style={{ marginBottom: '1rem' }}>
                api/ activate{' '}
              </Button>
              <Collapse isOpen={collapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}>
                        {' '}
                        GET <u>api/ activate </u>
                      </p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li> When the user is activated it also creates and links him to an employee.</li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
        </TabPane>
      </TabContent>
    </div>
  );
};

export default about;
