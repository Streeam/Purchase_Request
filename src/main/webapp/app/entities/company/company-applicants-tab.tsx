import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Link } from 'react-router-dom';
import { Translate } from 'react-jhipster';

import { NOTIFICATIONS } from '../../config/constants';
import { IRootState } from 'app/shared/reducers';
import { getCurrentUsersCompanyAsync as getCurrentUserEntity, hireEmployee, rejectEmployee } from './company.reducer';
import { getAsyncEntities as getEmployees } from '../employee/employee.reducer';

const companyApplicantsTab = props => {
  const { companyEntity, employeeList } = props;
  useEffect(() => {
    props.getEmployees();
  }, []);

  const handleAccept = (employeeId: Number) => {
    if (employeeId && companyEntity.id) {
      props.hireEmployee(companyEntity.id, employeeId);
      props.getCurrentUserEntity();
    }
  };

  const handleReject = (employeeId: Number) => {
    if (employeeId && companyEntity.id) {
      props.rejectEmployee(companyEntity.id, employeeId);
    }
  };
  const applicants = employeeList
    ? employeeList
        .filter(employee => employee.companyId !== companyEntity.id)
        .filter(employee => {
          if (employee.notifications && employee.notifications.length === 0) {
            return false;
          }
          const rejected = employee.notifications.filter((notification1, i) => notification1.format === NOTIFICATIONS.REJECT_REQUEST);
          if (rejected.length > 0) {
            return false;
          }
          const request = employee.notifications.filter((notification2, i) => notification2.format === NOTIFICATIONS.REQUEST_TO_JOIN);

          if (request.length > 0) {
            return true;
          }
          return false;
        })
    : null;
  // console.log(applicants);
  const tabContent = applicants ? (
    applicants.map((employee, i) => (
      <tr key={`entity-${i}`}>
        <td style={{ maxWidth: '10px' }}>
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
        <td>{employee.login ? employee.login : ''}</td>
        <td>{employee.firstName && employee.lastName ? `${employee.firstName}` + ` ${employee.lastName}` : ''}</td>
        <td>{employee.email ? employee.email : ''}</td>
        <td className="text-right">
          <div className="btn-group flex-btn-group-container">
            <Button
              tag={Link}
              to={`/entity/company/${companyEntity.id}/hire-employee/${employee.id}`}
              color="success"
              size="sm"
              // tslint:disable
              onClick={() => handleAccept(employee.id)}
            >
              <FontAwesomeIcon icon="check" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.accept">Accept</Translate>
              </span>
            </Button>
            <Button tag={Link} to={``} color="danger" size="sm" onClick={() => handleReject(employee.id)}>
              <FontAwesomeIcon icon="ban" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.reject">Reject(TODO)</Translate>
              </span>
            </Button>
          </div>
        </td>
      </tr>
    ))
  ) : (
    <div>Loading...</div>
  );

  return (
    <Table striped>
      <thead>
        <tr>
          <th />
          <th>Username</th>
          <th>Name</th>
          <th>Email</th>
          <th />
        </tr>
      </thead>
      <tbody>{tabContent}</tbody>
    </Table>
  );
};

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeList: employee.entities,
  currentEmployee: employee.currentEmployeeEntity
});

const mapDispatchToProps = {
  getEmployees,
  getCurrentUserEntity,
  hireEmployee,
  rejectEmployee
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyApplicantsTab);
