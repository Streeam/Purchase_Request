import './header.scss';

import React, { useState } from 'react';
import { Translate, Storage } from 'react-jhipster';
import { Navbar, Nav, NavbarToggler, Collapse } from 'reactstrap';

import LoadingBar from 'react-redux-loading-bar';

import { Home, Brand, About } from './header-components';
import { AdminMenu, EntitiesMenu, AccountMenu, LocaleMenu, CompanyMenu } from '../menus';

export interface IHeaderProps {
  isAuthenticated: boolean;
  isAdmin: boolean;
  isOnlyUser: boolean;
  isManager: boolean;
  ribbonEnv: string;
  isInProduction: boolean;
  isSwaggerEnabled: boolean;
  currentLocale: string;
  onLocaleChange: Function;
  account: any;
}

const Header = (props: IHeaderProps) => {
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLocaleChange = event => {
    const langKey = event.target.value;
    Storage.session.set('locale', langKey);
    props.onLocaleChange(langKey);
  };

  const renderDevRibbon = () =>
    props.isInProduction === false ? (
      <div className="ribbon dev">
        <a href="">
          <Translate contentKey={`global.ribbon.${props.ribbonEnv}`} />
        </a>
      </div>
    ) : null;

  const toggleMenu = () => setMenuOpen(!menuOpen);

  /* jhipster-needle-add-element-to-menu - JHipster will add new menu items here */

  return (
    <div id="app-header">
      {renderDevRibbon()}
      {/* <LoadingBar className="loading-bar" />*/}
      <Navbar light expand="sm" fixed="top" className="bg-light">
        <NavbarToggler aria-label="Menu" onClick={toggleMenu} />
        <Brand />
        <Collapse isOpen={menuOpen} navbar>
          <Nav id="header-tabs" className="ml-auto" navbar>
            <Home />
            {props.isAuthenticated && props.isAdmin && <EntitiesMenu {...props} />}
            {props.isAuthenticated && !props.isAdmin && <CompanyMenu isManager={props.isManager} isUnemployed={props.isOnlyUser} />}
            {props.isAuthenticated && props.isAdmin && (
              <AdminMenu showSwagger={props.isSwaggerEnabled} showDatabase={!props.isInProduction} />
            )}
            <LocaleMenu currentLocale={props.currentLocale} onClick={handleLocaleChange} />
            <AccountMenu isAuthenticated={props.isAuthenticated} account={props.account} />
            <About />
          </Nav>
        </Collapse>
      </Navbar>
    </div>
  );
};

export default Header;
