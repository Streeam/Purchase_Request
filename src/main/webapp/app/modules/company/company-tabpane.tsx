import React, { useState } from 'react';
import { TabContent, TabPane, Nav, NavItem, NavLink, Card, Button, Table, CardText, Row, Col } from 'reactstrap';

import classnames from 'classnames';
import '../../app.scss';
import InviteTab from '../company/company-invite-tab';

import CompanyEmployeeTab from './company-employee-tab';
import CompanyApplicantsTab from './company-applicants-tab';

const TabBar = props => {
  const [tabPane, setMenuOpen] = useState('1');
  const toggleTab = (tab: any): void => setMenuOpen(tab);
  const firstTab = () => toggleTab('1');
  const secondTab = () => toggleTab('2');
  // const thirdTab = () => toggleTab('3');
  return (
    <div id="app-header">
      <div>
        <Nav tabs>
          <NavItem>
            <NavLink className={classnames({ active: tabPane === '1' })} onClick={firstTab}>
              Company's Employees
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink className={classnames({ active: tabPane === '2' })} onClick={secondTab}>
              Applicants
            </NavLink>
          </NavItem>
          {/*<NavItem>
            <NavLink className={classnames({ active: tabPane === '3' })} onClick={thirdTab}>
              Invite
            </NavLink>
          </NavItem> */}
        </Nav>
        <TabContent activeTab={tabPane}>
          <TabPane tabId="1">
            <CompanyEmployeeTab {...props} />
          </TabPane>
          <TabPane tabId="2">
            <CompanyApplicantsTab {...props} />
          </TabPane>
          {/*<TabPane tabId="3">
            <InviteTab {...props} />
          </TabPane> */}
        </TabContent>
      </div>
    </div>
  );
};

export default TabBar;
