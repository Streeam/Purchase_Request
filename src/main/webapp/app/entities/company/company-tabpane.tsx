import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { getEntity } from './company.reducer';
import { Translate, Storage } from 'react-jhipster';
import { TabContent, TabPane, Nav, NavItem, NavLink, Card, Button, Table, CardText, Row, Col } from 'reactstrap';
import classnames from 'classnames';
import '../../app.scss';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link, RouteComponentProps } from 'react-router-dom';
import LoadingBar from 'react-redux-loading-bar';

import { AUTHORITIES } from '../../config/constants';
import { hasAnyAuthority } from '../../shared/auth/private-route';
import { IRootState } from '../../shared/reducers';

const TabBar = props => {
  const [tabPane, setMenuOpen] = useState('1');
  const toggleTab = (tab: any): void => setMenuOpen(tab);

  const firstTab = () => toggleTab('1');
  const secondTab = () => toggleTab('2');

  return (
    <div id="app-header">
      <LoadingBar className="loading-bar" />
      <div>
        <Nav tabs>
          <NavItem>
            <NavLink className={classnames({ active: tabPane === '1' })} onClick={firstTab}>
              Company Structure
            </NavLink>
          </NavItem>
          <NavItem>
            <NavLink className={classnames({ active: tabPane === '2' })} onClick={secondTab}>
              Applicants
            </NavLink>
          </NavItem>
        </Nav>
        <TabContent activeTab={tabPane}>
          <TabPane tabId="1">{props.children}</TabPane>
          <TabPane tabId="2" />
        </TabContent>
      </div>
    </div>
  );
};

export default TabBar;
