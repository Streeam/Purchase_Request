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

  const [firstCollapse, setFirstCollapse] = useState(false);
  const collapsFirstToggle = () => setFirstCollapse(!firstCollapse);
  const [secondCollapse, setSecondCollapse] = useState(false);
  const collapsSecondToggle = () => setSecondCollapse(!secondCollapse);
  const [thirdCollapse, setThirdCollapse] = useState(false);
  const collapsThirdToggle = () => setThirdCollapse(!thirdCollapse);
  const [fourthCollapse, setFourthCollapse] = useState(false);
  const collapsFourthtoggle = () => setFourthCollapse(!fourthCollapse);
  const [fifthCollapse, setFifthCollapse] = useState(false);
  const collapsFifthtoggle = () => setFifthCollapse(!fifthCollapse);
  const [sixthCollapse, setSixthCollapse] = useState(false);
  const collapsSixthtoggle = () => setSixthCollapse(!sixthCollapse);
  const [seventhCollapse, setSeventhCollapse] = useState(false);
  const collapsSeventhtoggle = () => setSeventhCollapse(!seventhCollapse);
  const [eighthCollapse, setEighthCollapse] = useState(false);
  const collapsEighthtoggle = () => setEighthCollapse(!eighthCollapse);
  const [ninethCollapse, settNinehCollapse] = useState(false);
  const collapsNinethtoggle = () => settNinehCollapse(!ninethCollapse);
  const [tenthCollapse, settTenthCollapse] = useState(false);
  const collapsTenthtoggle = () => settTenthCollapse(!tenthCollapse);
  const [eleventhCollapse, settEleventhCollapse] = useState(false);
  const collapsEleventhtoggle = () => settEleventhCollapse(!eleventhCollapse);

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
                  &nbsp;
                  <h4>Company Information Database</h4>
                  <blockquote className="blockquote">
                    <div style={{ fontSize: '1.1rem' }}>
                      <p>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This is an end-to-end full stack monolithic application. The backend server is build
                        using Java and Spring Boot. For authorization and authentication, I used Spring Security along with JWT
                        authentication. For storage I used PostgreSQL a relational database management system. The front-end application is
                        be built using ReactJS and TypeScript. Loag term goal is to turn this application into a gateway microservice and
                        attach several microservices to it.
                      </p>
                      &nbsp;
                      <p>
                        &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;When a user first logs in, he is presented with two options. He can create his own
                        company and become the manager of that company or he can request to join an existing company. If he creates his own
                        company, he can invite existing users to join his company. He can also send emails to invite people who haven't yet
                        registered to the application to sign up. When a user accepts an invitation, he becomes part of that company and is
                        given the employee role. The manager can assign roles, he can hire or fire employee, he can send invitation and he
                        can even dissolve the company. If the user decides to join a company, he can search through all companies and their
                        internal structure. A user can request to join multiple companies, but he can only join one. Once the manager
                        approves his request a notification is sent to him informing him that his request has been approved. Notifications
                        are also sent to all the company's employees informing them that a new employee has join the company.
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
          <br />
          <Row>
            <Col>
              <br />
              <Button
                color="light"
                onClick={collapsFirstToggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/companies
              </Button>
              <Collapse isOpen={firstCollapse}>
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
                        DELETE{' '}
                        <u>
                          api/companies/{'{'}companyId{'}'}
                        </u>
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
                        PUT{' '}
                        <u>
                          api/companies/{'{'}companyId{'}'}
                        </u>
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
              <Button
                color="light"
                onClick={collapsSecondToggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/ activate{' '}
              </Button>
              <Collapse isOpen={secondCollapse}>
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
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsThirdToggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employee/{'{'}employeeId{'}'}/request-to-join/{'{'}companyId{'}'}
              </Button>
              <Collapse isOpen={thirdCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> POST</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>The users with the roles ROLE_MANAGER and ROLE_EMPLOYEE are restricted from using this endpoint.</li>
                        <li>Sends a email to the company's manager to request to join the company.</li>
                        <li>Also creates a notification and sends it to the manager.</li>
                        <li>
                          If the the employee's request has been rejected he cannot apply to join the same company in a period less then 3
                          days.
                        </li>
                        <li>The employee can request to join more then one company.</li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsFourthtoggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employees/unemployed
              </Button>
              <Collapse isOpen={fourthCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> GET</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>List all unemployed employees.</li>
                        <li>The manager or the admin can see all the unemployed users from all companies.</li>
                        <li>The logged user has to be either the admin or the manager of the company in order to access this endpoint.</li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsFifthtoggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employees/invite-to-join/{'{'}email{'}'}
              </Button>
              <Collapse isOpen={fifthCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> POST</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>The manager of a company invites an user to join his company.</li>
                        <li>Only the manager or the admin can access this endpoint.</li>
                        <li>If the user exists and he/she is unemployeed sends an invite notification and an email to the user.</li>
                        <li>If the user never registered with the app only sends the invitation via email. </li>
                        <li>
                          When the users registers automatically activate the account (skiping the verification process) and send an invite
                          notification
                        </li>
                        <li>
                          An employee who has rejected an invitation cannot be invited again in less then three day from the last rejection
                        </li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsSixthtoggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employees/{'{'}employeeId{'}'}/approve-request/{'{'}companyId{'}'}
              </Button>
              <Collapse isOpen={sixthCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> POST</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>Approve a request to join a company</li>
                        <li>The logged user has to be unemployed.</li>
                        <li>
                          An email and a notification is sent to the company's manager informing him that the employee has accepted the
                          invitation.
                        </li>
                        <li>
                          Verify if the company has sent an invitation to this user in the last 14 days. If it has the user can join the
                          company.
                        </li>
                        <li>The user is added to the company and given the emplopyee role.</li>
                        <li>
                          A notification is sent to all the company's employees informing them that a new employee has joined the company.
                        </li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsSeventhtoggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employees/{'{'}employeeId{'}'}/decline-request/{'{'}companyId{'}'}
              </Button>
              <Collapse isOpen={seventhCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> POST</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>Decline a request to join a company</li>
                        <li>The logged user has to be an unemployed user.</li>
                        <li>
                          An email and a notification is sent to the company's manager informing him that the employee has declined the
                          invitation.
                        </li>
                      </ul>
                    </div>
                  </CardBody>
                </Card>
              </Collapse>
            </Col>
          </Row>
          <Row>
            <Col>
              <Button
                color="light"
                onClick={collapsSeventhtoggle}
                style={{
                  marginBottom: '1rem',
                  width: '100%',
                  textAlign: 'left'
                }}
              >
                api/employees/{'{'}employeeId{'}'}/decline-request/{'{'}companyId{'}'}
              </Button>
              <Collapse isOpen={seventhCollapse}>
                <Card>
                  <CardBody>
                    <strong>
                      <p style={{ paddingLeft: '2em' }}> POST</p>
                    </strong>
                    <div style={{ paddingLeft: '3em' }}>
                      <ul>
                        <li>Decline a request to join a company</li>
                        <li>The logged user has to be an unemployed user.</li>
                        <li>
                          An email and a notification is sent to the company's manager informing him that the employee has declined the
                          invitation.
                        </li>
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
