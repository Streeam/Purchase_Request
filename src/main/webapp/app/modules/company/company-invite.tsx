import React, { useEffect, Fragment } from 'react';
import { connect } from 'react-redux';
import { Button, Table, ButtonGroup } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import moment from 'moment';
import { Link } from 'react-router-dom';

import { NOTIFICATIONS, AUTHORITIES } from '../../config/constants';
import { IRootState } from 'app/shared/reducers';
import { inviteEmployee, getCurrentEmployeeAsync, getAsyncEntities as getEmployees } from '../../entities/employee/employee.reducer';
import { employeeListWithout } from '../../shared/util/entity-utils';
import { getAsyncEntities as getNotifications } from '../../entities/notification/notification.reducer';
import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';

const companyInvite = props => {
  let _isMounted = false;
  const { currentEmployee, employeeList, notificationList, employeesLoading, notificationsLoading } = props;
  const isLoading = employeesLoading || notificationsLoading;

  useEffect(() => {
    _isMounted = true;
    props.getNotifications();
    props.getCurrentEmployeeAsync(_isMounted);
    props.getEmployees(_isMounted);
    return () => (_isMounted = false);
  }, [props.getNotifications, props.getCurrentEmployeeAsync, props.getEmployees, _isMounted]);

  const employeesWithoutAuth = employeeListWithout(employeeList, [AUTHORITIES.ADMIN, AUTHORITIES.EMPLOYEE, AUTHORITIES.MANAGER]);

  const invitees = employeesWithoutAuth
    ? employeesWithoutAuth.filter(employee => {
        const employeeNotifications = notificationList.filter(notification => notification.employeeId === employee.id);

        if (employeeNotifications && employeeNotifications.length === 0) {
          return true;
        }
        // ****** FIRE NOTIFICATIONS */
        const fired = employeeNotifications.filter((notification, i) => notification.format === NOTIFICATIONS.FIRED);
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
            return now - notificationDateTime > 720 ? true : false;
          });
          return validFired.length > 0 ? true : false;
        }
        // ****** REJECT NOTIFICATIONS */
        const rejected = employeeNotifications.filter((notification, i) => notification.format === NOTIFICATIONS.REJECT_REQUEST);
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
            return now - notificationDateTime > 72 ? true : false;
          });
          return validRejected.length > 0 ? true : false;
        }

        // ****** REQUEST NOTIFICATIONS */
        const request = employeeNotifications.filter((notification, i) => notification.format === NOTIFICATIONS.REQUEST_TO_JOIN);
        if (request.length > 0) {
          let notificationDate: Date = moment(request[0].sentDate).toDate();

          const validRejected = request.filter(requestedNotif => {
            const nextDate: Date = moment(requestedNotif.sentDate).toDate();
            if (nextDate > notificationDate) {
              notificationDate = moment(requestedNotif.sentDate).toDate();
            }
            const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
            const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

            // if latest request request is older then 72 hours (3 days) return false, true otherwise
            return now - notificationDateTime > 72 ? true : false;
          });
          return validRejected.length > 0 ? true : false;
        }
        // ****** INVITE NOTIFICATIONS */
        const inviteNotifications = employeeNotifications.filter((notification, i) => notification.format === NOTIFICATIONS.INVITATION);
        if (inviteNotifications.length > 0) {
          let notificationDate: Date = moment(inviteNotifications[0].sentDate).toDate();

          const validInvite = inviteNotifications.filter(inviteNotif => {
            const nextDate: Date = moment(inviteNotif.sentDate).toDate();
            if (nextDate > notificationDate) {
              notificationDate = moment(inviteNotif.sentDate).toDate();
            }
            const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
            const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

            // if latest request request is older then 72 hours (3 days) return false, true otherwise
            return now - notificationDateTime > 72 ? true : false;
          });
          return validInvite.length > 0 ? true : false;
        }
        return false;
      })
    : null;

  const tabContent = invitees ? (
    invitees.map((employee, i) => (
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
          <div className="table-button">
            <div className="btn-group flex-btn-group-container">
              <ButtonGroup>
                <Button tag={Link} to={`/entity/employee/${employee.id}`} size="sm">
                  <FontAwesomeIcon icon="eye" />{' '}
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.view">View</Translate>
                  </span>
                </Button>
                <Button
                  tag={Link}
                  to={`/company/invite/${currentEmployee.companyId}/invite-employee/${employee.email}`}
                  // color="primary"
                  size="sm"
                >
                  <FontAwesomeIcon icon="user" />{' '}
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.invite">Invite</Translate>
                  </span>
                </Button>
              </ButtonGroup>
            </div>
          </div>
        </td>
      </tr>
    ))
  ) : (
    <div>Loading...</div>
  );

  return isLoading ? (
    <LoadingModal />
  ) : (
    <Fragment>
      <h3>Hire Employees</h3>
      {invitees && invitees.length > 0 ? (
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
        <div className="alert alert-light">No Available employees</div>
      )}
    </Fragment>
  );
};

const mapStateToProps = ({ employee, notification }: IRootState) => ({
  employeeList: employee.entities,
  currentEmployee: employee.currentEmployeeEntity,
  notificationList: notification.entities,
  employeesLoading: employee.loading && employee.updating,
  notificationsLoading: notification.loading
});

const mapDispatchToProps = {
  inviteEmployee,
  getNotifications,
  getCurrentEmployeeAsync,
  getEmployees
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyInvite);
