import React, { Fragment } from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { NavDropdown } from './menu-components';

export const CompanyMenu = props => {
  const { isUserOnly } = props;
  let contentMenu = (
    <Fragment>
      <MenuItem icon="asterisk" to="/company/company-status">
        My Company
      </MenuItem>
      <MenuItem icon="asterisk" to="/company/invite">
        Invite
      </MenuItem>
    </Fragment>
  );
  if (isUserOnly) {
    contentMenu = (
      <Fragment>
        <MenuItem icon="asterisk" to="/company/join-company">
          Join Company
        </MenuItem>
        <MenuItem icon="asterisk" to="/entity/company/new">
          Create Company
        </MenuItem>
      </Fragment>
    );
  }
  return (
    // tslint:disable-next-line:jsx-self-close
    <NavDropdown icon="th-list" name={'Company'} id="entity-menu">
      {contentMenu}
    </NavDropdown>
  );
};
