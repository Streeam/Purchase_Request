import React from 'react';
import { Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';

import { AUTHORITIES } from '../../../app/config/constants';
import { hasAnyAuthority } from '../../shared/auth/private-route';

const companyEmployeeTab = props => {
  const { companyEntity } = props;
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
  const tabContent = companyEntity.employees
    ? companyEntity.employees.map((employee, i) => (
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
          <td>{employee.firstName && employee.lastName ? `${employee.firstName}` + ` ${employee.lastName}` : ''}</td>
          <td>{employee.login ? employee.login : ''}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.MANAGER])}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.EMPLOYEE])}</td>
          <td className="text-right">
            <div className="btn-group flex-btn-group-container">
              <Button tag={Link} to={`/entity/employee/${employee.id}`} color="info" size="sm">
                <FontAwesomeIcon icon="eye" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.view">View</Translate>
                </span>
              </Button>
              <Button tag={Link} to={``} color="danger" size="sm">
                <FontAwesomeIcon icon="trash" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.fire">Fire(TODO)</Translate>
                </span>
              </Button>
            </div>
          </td>
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
          <th />
        </tr>
      </thead>
      <tbody>{tabContent}</tbody>
    </Table>
  );
};

export default companyEmployeeTab;
