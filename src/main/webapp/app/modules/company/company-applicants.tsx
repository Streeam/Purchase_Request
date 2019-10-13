import React, { useEffect, Fragment } from 'react';
import { connect } from 'react-redux';
import { Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import moment from 'moment';
import { Link } from 'react-router-dom';

import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';
import { NOTIFICATIONS } from '../../config/constants';
import { IRootState } from 'app/shared/reducers';
import { getAsyncEntities as getEmployees } from '../../entities/employee/employee.reducer';
import { getCurrentUsersCompanyAsync as getCurrentUserEntity, hireEmployee } from '../../entities/company/company.reducer';

const companyApplicants = props => {
  let _isMounted = false;
  const { companyEntity, employeeList, companyLoading, rejectingOrAcceptingRequestLoading } = props;
  const isLoading = companyLoading;

  useEffect(() => {
    _isMounted = true;
    props.getEmployees(_isMounted);
    props.getCurrentUserEntity(_isMounted);
    return () => (_isMounted = false);
  }, []);

  const handleAccept = (employeeId: Number) => {
    if (employeeId && companyEntity.id) {
      props.hireEmployee(companyEntity.id, employeeId, _isMounted);
    }
  };

  const applicants = employeeList
    ? employeeList
        .filter(employee => employee.companyId === null)
        .filter(employee => {
          if (!employee.notifications || employee.notifications.length === 0) {
            return false;
          }

          const notificationsAddressedToThisCompany = employee.notifications.filter(
            notification0 => notification0.company === companyEntity.id
          );

          // ****** FIRE NOTIFICATIONS */

          const fired = notificationsAddressedToThisCompany.filter((notification1, i) => notification1.format === NOTIFICATIONS.FIRED);
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
          const rejected = notificationsAddressedToThisCompany.filter(
            (notification1, i) => notification1.format === NOTIFICATIONS.REJECT_REQUEST
          );
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

          const request = notificationsAddressedToThisCompany.filter(
            (notification1, i) => notification1.format === NOTIFICATIONS.REQUEST_TO_JOIN
          );
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
    : [];

  const tabContent = applicants.map((employee, i) => (
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
          <Button color="danger" size="sm" tag={Link} to={`${props.match.url}/${companyEntity.id}/reject-employee/${employee.id}`}>
            <FontAwesomeIcon icon="ban" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.reject">Reject</Translate>
            </span>
          </Button>
        </div>
      </td>
    </tr>
  ));

  return isLoading ? (
    <LoadingModal />
  ) : (
    <Fragment>
      <h3>Applicants</h3>
      {applicants && applicants.length > 0 ? (
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
      ) : (
        <div className="alert alert-light">No Applicants</div>
      )}
    </Fragment>
  );
};

const mapStateToProps = ({ employee, company }: IRootState) => ({
  companyEntity: company.employeeEntity,
  employeeList: employee.entities,
  companyLoading: company.loading
});

const mapDispatchToProps = {
  getCurrentUserEntity,
  hireEmployee,
  getEmployees
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyApplicants);
