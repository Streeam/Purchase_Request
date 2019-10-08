import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import moment from 'moment';

import { NOTIFICATIONS } from '../../config/constants';
import { IRootState } from 'app/shared/reducers';
import { getCurrentUsersCompanyAsync as getCurrentUserEntity, hireEmployee, rejectEmployee } from '../../entities/company/company.reducer';
import { getAsyncEntities as getEmployees } from '../../entities/employee/employee.reducer';

const companyApplicantsTab = props => {
  const { companyEntity, employeeList } = props;
  useEffect(() => {
    props.getEmployees();
    props.getCurrentUserEntity();
  }, []);

  const handleAccept = (employeeId: Number) => {
    if (employeeId && companyEntity.id) {
      props.hireEmployee(companyEntity.id, employeeId);
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
          if (!employee.notifications && employee.notifications.length === 0) {
            return false;
          }
          // ****** FIRE NOTIFICATIONS */
          const fired = employee.notifications.filter((notification1, i) => notification1.format === NOTIFICATIONS.FIRED);
          if (fired.length > 0) {
            let notificationDate: Date = moment(fired[0].sentDate).toDate();

            const validFired = fired.filter(firedNotif => {
              const nextDate: Date = moment(firedNotif.sentDate).toDate();
              if (nextDate > notificationDate) {
                notificationDate = moment(firedNotif.sentDate).toDate();
              }
              const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
              const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

              // if latest rejected request is older then 720 hours (30 days) return true, false otherwise
              return now - notificationDateTime > 720;
            });
            return validFired.length > 0;
          }
          // ****** REJECT NOTIFICATIONS */
          const rejected = employee.notifications.filter((notification1, i) => notification1.format === NOTIFICATIONS.REJECT_REQUEST);
          if (rejected.length > 0) {
            let notificationDate: Date = moment(rejected[0].sentDate).toDate();

            const validRejected = rejected.filter(rejectedNotif => {
              const nextDate: Date = moment(rejectedNotif.sentDate).toDate();
              if (nextDate > notificationDate) {
                notificationDate = moment(rejectedNotif.sentDate).toDate();
              }
              const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
              const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

              // if latest rejected request is older then 72 hours (3 days) return true, false otherwise
              return now - notificationDateTime > 72;
            });
            return validRejected.length > 0;
          }

          // ****** REQUEST NOTIFICATIONS */
          const request = employee.notifications.filter((notification1, i) => notification1.format === NOTIFICATIONS.REQUEST_TO_JOIN);
          if (request.length > 0) {
            let notificationDate: Date = moment(request[0].sentDate).toDate();

            const validRequested = request.filter(requestedNotif => {
              const nextDate: Date = moment(requestedNotif.sentDate).toDate();
              if (nextDate > notificationDate) {
                notificationDate = moment(requestedNotif.sentDate).toDate();
              }
              const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
              const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

              // if latest request request is older then 72 hours (3 days) return false, true otherwise
              return now - notificationDateTime < 72;
            });
            return validRequested.length > 0;
          }
          return false;
        })
    : null;

  const tabContent = applicants ? (
    applicants.map((employee, i) => (
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
        <td>{employee.login ? employee.login : ''}</td>
        <td>{employee.firstName && employee.lastName ? `${employee.firstName}` + ` ${employee.lastName}` : ''}</td>
        <td>{employee.email ? employee.email : ''}</td>
        <td className="text-right">
          <div className="btn-group flex-btn-group-container">
            <Button
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
            <Button color="danger" size="sm" onClick={() => handleReject(employee.id)}>
              <FontAwesomeIcon icon="ban" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.reject">Reject</Translate>
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
