import React, { useState } from 'react';
import { Table } from 'reactstrap';
import { AUTHORITIES } from '../../../app/config/constants';
import { connect } from 'react-redux';
import { Route, Redirect, RouteProps } from 'react-router-dom';
import { Translate } from 'react-jhipster';

import { IRootState } from 'app/shared/reducers';
import { hasAnyAuthority } from '../../shared/auth/private-route';

const companyEmployeeTab = props => {
  const isManager = (authorities, anyAuthority) => {
    const auth = authorities.map(authority => authority.name);

    return hasAnyAuthority(auth, anyAuthority) ? (
      <div>
        <img src={`content/images/check.png`} style={{ maxHeight: '20px' }} />
      </div>
    ) : (
      <div />
    );
  };
  const tabContent = props.employees
    ? props.employees.map((employee, i) => (
        <tr key={`entity-${i}`}>
          <td>
            {employee.image ? (
              <div>
                <img src={`data:${employee.imageContentType};base64,${employee.image}`} style={{ maxHeight: '30px' }} />
              </div>
            ) : (
              <div>
                <img src={`content/images/default_profile_icon.png`} style={{ maxHeight: '30px' }} />
              </div>
            )}
          </td>
          <td>{`${employee.firstName}` + ` ${employee.lastName}`}</td>
          <td>{employee.login}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.MANAGER])}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.EMPLOYEE])}</td>
        </tr>
      ))
    : null;

  return (
    <Table>
      <thead>
        <tr>
          <th />
          <th>Name</th>
          <th>Username</th>
          <th>Manager</th>
          <th>Employee</th>
        </tr>
      </thead>
      <tbody>{tabContent}</tbody>
    </Table>
  );
};

export default companyEmployeeTab;
