import React, { Fragment } from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { NavDropdown } from './menu-components';

export const CompanyMenu = props => {
  const { isUnemployed, isManager } = props;
  return (
    // tslint:disable-next-line:jsx-self-close
    <NavDropdown icon="th-list" name={'Company'} id="entity-menu">
      <Fragment>
        {!isUnemployed && (
          <MenuItem icon="asterisk" to="/company/company-status">
            My Company
          </MenuItem>
        )}
        {isManager && (
          <MenuItem icon="asterisk" to="/company/applicants">
            Applicants
          </MenuItem>
        )}
        {isManager && (
          <MenuItem icon="asterisk" to="/company/invite">
            Hire
          </MenuItem>
        )}
        {isUnemployed && (
          <MenuItem icon="asterisk" to="/company/join-company">
            Join Company
          </MenuItem>
        )}
        {isUnemployed && (
          <MenuItem icon="asterisk" to="/entity/company/new">
            Create Company
          </MenuItem>
        )}
      </Fragment>
    </NavDropdown>
  );
};

export default CompanyMenu;
