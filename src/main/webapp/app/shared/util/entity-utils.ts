import pick from 'lodash/pick';
import { IEmployee } from '../model/employee.model';
import { hasAnyAuthority } from '../auth/private-route';
import { AUTHORITIES, NOTIFICATIONS } from 'app/config/constants';
import moment from 'moment';
import notification from 'app/entities/notification/notification';
import { INotification } from '../model/notification.model';

/**
 * Removes fields with an 'id' field that equals ''.
 * This function was created to prevent entities to be sent to
 * the server with relationship fields with an empty id and thus
 * resulting in a 500.
 *
 * @param entity Object to clean.
 */
export const cleanEntity = entity => {
  const keysToKeep = Object.keys(entity).filter(k => !(entity[k] instanceof Object) || (entity[k]['id'] !== '' && entity[k]['id'] !== -1));

  return pick(entity, keysToKeep);
};

/**
 * Simply map a list of element to a list a object with the element as id.
 *
 * @param idList Elements to map.
 * @returns The list of objects with mapped ids.
 */
export const mapIdList = (idList: ReadonlyArray<any>) =>
  idList.filter((entityId: any) => entityId !== '').map((entityId: any) => ({ id: entityId }));

export const employeeListWithout = (employeeList: ReadonlyArray<IEmployee>, hasAnyAuthorities: string[]): ReadonlyArray<IEmployee> =>
  employeeList.filter(employee => {
    if (employee.user.authorities.length === 0) {
      return false;
    }
    const authorities: string[] = [];
    employee.user.authorities.map(auth => authorities.push(auth.name));
    return !hasAnyAuthority(authorities, hasAnyAuthorities);
  });

export const employeesToHire = (employeeList: IEmployee[]): IEmployee[] => {
  const employeesWithoutAuth = employeeListWithout(employeeList, [AUTHORITIES.ADMIN, AUTHORITIES.EMPLOYEE, AUTHORITIES.MANAGER]);
  return employeesWithoutAuth
    ? employeesWithoutAuth.filter(employee => {
        const employeeNotifications = employee.notifications;
        if (employeeNotifications && employeeNotifications.length === 0) {
          return true;
        }
        // ****** FIRE NOTIFICATIONS */
        if (isLatestFiredNotificationValid(employee.notifications)) {
          return false;
        }
        // ****** REJECT INVITATION NOTIFICATIONS */
        if (isLatestRejectedInviteNotificationValid(employee.notifications)) {
          return false;
        }

        // ****** REJECT REQUEST NOTIFICATIONS */
        if (isLatestRejectedRequestNotificationValid(employee.notifications)) {
          return false;
        }
        // ****** REQUEST NOTIFICATIONS */
        const request = employeeNotifications.filter(
          (requestNotification, i) => requestNotification.format === NOTIFICATIONS.REQUEST_TO_JOIN
        );
        if (request.length > 0) {
          let notificationDate: Date = moment(request[0].sentDate).toDate();

          const validRequest = request.filter(requestedNotif => {
            const nextDate: Date = moment(requestedNotif.sentDate).toDate();
            if (nextDate > notificationDate) {
              notificationDate = moment(requestedNotif.sentDate).toDate();
            }
            const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
            const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

            // if latest request request is older then 360 hours (14 days) return false, true otherwise
            return now - notificationDateTime < 360;
          });
          if (validRequest.length > 0) {
            return false;
          }
        }
        // ****** INVITE NOTIFICATIONS */
        const inviteNotifications = employeeNotifications.filter(
          (inviteNotification, i) => inviteNotification.format === NOTIFICATIONS.INVITATION
        );
        if (inviteNotifications.length > 0) {
          let notificationDate: Date = moment(inviteNotifications[0].sentDate).toDate();
          const validInvite = inviteNotifications.filter(inviteNotif => {
            const nextDate: Date = moment(inviteNotif.sentDate).toDate();
            if (nextDate > notificationDate) {
              notificationDate = moment(inviteNotif.sentDate).toDate();
            }
            const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
            const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));
            // if latest invitation is older then 360 hours (14 days) return false, true otherwise
            return now - notificationDateTime < 360;
          });
          if (validInvite.length > 0) {
            return false;
          }
        }
        return true;
      })
    : null;
};

export const applicantsEmployees = (employeeList: IEmployee[]): IEmployee[] => {
  const employeesWithoutAuth = employeeListWithout(employeeList, [AUTHORITIES.ADMIN, AUTHORITIES.EMPLOYEE, AUTHORITIES.MANAGER]);

  return employeesWithoutAuth
    .filter(employee => employee.companyId === null)
    .filter(employee => {
      if (!employee.notifications || employee.notifications.length === 0) {
        return false;
      }

      if (isLatestFiredNotificationValid(employee.notifications)) {
        return false;
      }

      if (isLatestRejectedInviteNotificationValid(employee.notifications)) {
        return false;
      }

      if (isLatestRejectedRequestNotificationValid(employee.notifications)) {
        return false;
      }

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

          // if latest request is older then 360 hours (14 days) return false, true otherwise
          return now - notificationDateTime < 360;
        });
        return validRequested.length > 0;
      }
      return false;
    });
};

const isLatestFiredNotificationValid = (employeeFiredNotifications: INotification[]) => {
  const fired = employeeFiredNotifications.filter((notification1, i) => notification1.format === NOTIFICATIONS.FIRED);
  if (fired.length > 0) {
    let notificationDate: Date = moment(fired[0].sentDate).toDate();

    const validFired = fired.filter(firedNotif => {
      const nextDate: Date = moment(firedNotif.sentDate).toDate();
      if (nextDate > notificationDate) {
        notificationDate = moment(firedNotif.sentDate).toDate();
      }
      const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
      const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

      // if latest rejected request is older then 360 hours (14 days) return true, false otherwise
      return now - notificationDateTime > 360;
    });
    return validFired.length === 0;
  }
};

const isLatestRejectedInviteNotificationValid = (employeeRejectedInvitationNotifications: INotification[]) => {
  const rejectedInvitations = employeeRejectedInvitationNotifications.filter(
    (notification1, i) => notification1.format === NOTIFICATIONS.REJECT_INVITE
  );
  if (rejectedInvitations.length > 0) {
    let notificationDate: Date = moment(rejectedInvitations[0].sentDate).toDate();

    const validRejectedInvitations = rejectedInvitations.filter(rejectInviteNotif => {
      const nextDate: Date = moment(rejectInviteNotif.sentDate).toDate();
      if (nextDate > notificationDate) {
        notificationDate = moment(rejectInviteNotif.sentDate).toDate();
      }
      const now = Math.ceil(new Date().getTime() / (1000 * 60 * 60));
      const notificationDateTime = Math.ceil(notificationDate.getTime() / (1000 * 60 * 60));

      // if latest rejected invitation is older then 360 hours (14 days) return true, false otherwise
      return now - notificationDateTime > 360;
    });
    return validRejectedInvitations.length === 0;
  }
};

const isLatestRejectedRequestNotificationValid = (employeeRejectedNotifications: INotification[]) => {
  const rejected = employeeRejectedNotifications.filter(
    (rejectNotification, i) => rejectNotification.format === NOTIFICATIONS.REJECT_REQUEST
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
    return validRejected.length === 0;
  }
};
