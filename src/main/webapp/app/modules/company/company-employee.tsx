import React, { useEffect } from 'react';
import { Button, Table } from 'reactstrap';
import { connect } from 'react-redux';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';

import { AUTHORITIES } from '../../config/constants';
import { hasAnyAuthority } from '../../shared/auth/private-route';
import { IRootState } from 'app/shared/reducers';
import { getCurrentUsersCompanyAsync } from '../../entities/company/company.reducer';
import { IEmployee } from 'app/shared/model/employee.model';

const companyEmployeeTab = props => {
  const { companyEntity, isCurrentUserManager } = props;

  const isManager = authorities => {
    const auth = authorities.map(authority => authority.name);
    return hasAnyAuthority(auth, [AUTHORITIES.MANAGER]);
  };

  const isManagerCheck = (manager: boolean): JSX.Element => {
    return manager ? (
      <div>
        <img src={`content/images/check.png`} style={{ maxHeight: '20px' }} />
      </div>
    ) : (
      <div />
    );
  };

  const fireAndViewButtons = (employee: IEmployee): JSX.Element => {
    return isCurrentUserManager ? (
      <div className="btn-group flex-btn-group-container">
        <Button tag={Link} to={`/entity/employee/${employee.id}`} color="info" size="sm">
          <FontAwesomeIcon icon="eye" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.view">View</Translate>
          </span>
        </Button>
        <Button
          tag={Link}
          to={`/entity/employee/${employee.id}/edit-roles`}
          color="primary"
          size="sm"
          // disabled={isManager(employee.user.authorities)}
          title="Edit Employee Roles"
        >
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
        <Button
          tag={Link}
          to={`/company/company-status/${companyEntity.id}/fire/${employee.id}`}
          color="danger"
          size="sm"
          disabled={isManager(employee.user.authorities)}
          title="Fire Employee"
        >
          <FontAwesomeIcon icon="ban" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.fire">Fire</Translate>
          </span>
        </Button>
      </div>
    ) : (
      <div />
    );
  };

  const tabContent = companyEntity.employees
    ? companyEntity.employees.map((employee, i) => (
        <tr key={`entity-${i}`}>
          <td style={{ maxWidth: '10px' }}>
            {employee.image ? (
              <div>
                <img
                  src={`data:${employee.imageContentType};base64,${employee.image}`}
                  style={{
                    maxHeight: '30px',
                    borderRadius: '50%'
                  }}
                />
              </div>
            ) : (
              <div>
                <img
                  src={`content/images/default_profile_icon.png`}
                  style={{
                    maxHeight: '30px',
                    borderRadius: '50%'
                  }}
                />
              </div>
            )}
          </td>
          <td>{employee.user.firstName && employee.user.lastName ? `${employee.user.firstName}` + ` ${employee.user.lastName}` : ''}</td>
          <td>{employee.login ? employee.login : ''}</td>
          <td>{isManagerCheck(isManager(employee.user.authorities))}</td>
          <td>{isManagerCheck(!isManager(employee.user.authorities))}</td>
          <td className="text-right">{fireAndViewButtons(employee)}</td>
        </tr>
      ))
    : null;

  return (
    <Table>
      <thead>
        <tr>
          <th />
          <th>Employee's Name</th>
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

const mapStateToProps = ({ company }: IRootState) => ({
  companyEntity: company.employeeEntity
});

const mapDispatchToProps = {
  getCurrentUsersCompanyAsync
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyEmployeeTab);
