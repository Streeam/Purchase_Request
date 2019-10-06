import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Button, Table, ButtonGroup } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import moment from 'moment';
import { Link } from 'react-router-dom';

import { NOTIFICATIONS, AUTHORITIES } from '../../config/constants';
import { IRootState } from 'app/shared/reducers';
import { inviteEmployee } from '../employee/employee.reducer';
import { employeeListWithout } from '../../shared/util/entity-utils';
import { getAsyncEntities as getNotifications } from '../notification/notification.reducer';

const companyInvite = props => {
  const { employeeList, notificationList } = props;

  useEffect(() => {
    props.getNotifications();
  }, []);

  const employeesWithoutAuth = employeeListWithout(employeeList, [AUTHORITIES.ADMIN, AUTHORITIES.MANAGER]);

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
            <ButtonGroup>
              <Button tag={Link} to={`/entity/employee/${employee.id}`} size="sm">
                <FontAwesomeIcon icon="eye" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.view">View</Translate>
                </span>
              </Button>
              <Button
                tag={Link}
                to={`/entity/company/invite/${employee.email}`}
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

const mapStateToProps = ({ employee, notification }: IRootState) => ({
  employeeList: employee.entities,
  currentEmployee: employee.currentEmployeeEntity,
  notificationList: notification.entities
});

const mapDispatchToProps = {
  inviteEmployee,
  getNotifications
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyInvite);
