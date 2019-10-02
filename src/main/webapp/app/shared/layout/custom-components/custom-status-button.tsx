import React from 'react';
import { Button } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router-dom';
import { INotification } from 'app/shared/model/notification.model';
import moment from 'moment';
import { NOTIFICATIONS } from '../../../config/constants';

const customButton = props => {
  const joinButton = (companyId: Number): JSX.Element => (
    <Button tag={Link} to={`${props.url}/${companyId}/join`} color="primary" size="sm">
      <FontAwesomeIcon icon="file-signature" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.join">Join</Translate>
      </span>
    </Button>
  );

  const submittedButton = (): JSX.Element => (
    <Button color="primary" size="sm" disabled>
      <FontAwesomeIcon icon="file-signature" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.submitted">Application Submitted</Translate>
      </span>
    </Button>
  );

  const rejectedButton = (): JSX.Element => (
    <Button color="danger" size="sm" disabled>
      <FontAwesomeIcon icon="ban" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.rejected">Application Rejected</Translate>
      </span>
    </Button>
  );

  const firedButton = (): JSX.Element => (
    <Button color="danger" size="sm" disabled>
      <FontAwesomeIcon icon="ban" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.fired">Fired</Translate>
      </span>
    </Button>
  );

  const requestToJoinPending = (): JSX.Element => {
    if (props.notifications) {
      const employeeNotifications: INotification[] = props.notifications.filter(value => value.company === props.companyIndex);
      const requestedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.REQUEST_TO_JOIN === value.format);
      const rejectedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.REJECT_REQUEST === value.format);
      const firedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.FIRED === value.format);
      const now1 = new Date();
      now1.setDate(now1.getDate() - 3);
      const threeDaysAgo = new Date(now1); // Three days ago
      const now2 = new Date();
      now2.setDate(now2.getDate() - 30); // Thirty days ago
      const thirtyDaysAgo = new Date(now2);
      if (rejectedNotifications.length === 0 && requestedNotifications.length === 0 && firedNotifications.length === 0) {
        return joinButton(props.companyIndex);
      }
      if (firedNotifications.length > 0) {
        let notificationDate: Date = moment(firedNotifications[0].sentDate).toDate();

        firedNotifications.forEach(nextNotification => {
          const nextDate: Date = moment(nextNotification.sentDate).toDate();
          if (nextDate > notificationDate) {
            notificationDate = moment(nextNotification.sentDate).toDate();
          }
        });

        if (notificationDate > thirtyDaysAgo) {
          return firedButton();
        }
      }

      const requestAndRejectNotifications: INotification[] = [...rejectedNotifications, ...requestedNotifications];
      let firstNotification: INotification = requestAndRejectNotifications[0];
      let firstDate: Date = moment(firstNotification.sentDate).toDate();

      requestAndRejectNotifications.forEach(nextNotification => {
        const nextDate: Date = moment(nextNotification.sentDate).toDate();
        if (nextDate > firstDate) {
          firstNotification = nextNotification;
          firstDate = moment(firstNotification.sentDate).toDate();
        }
      });

      if (firstNotification.format === NOTIFICATIONS.REJECT_REQUEST) {
        if (firstDate > threeDaysAgo) {
          return rejectedButton();
        } else {
          return joinButton(props.companyIndex);
        }
      } else {
        if (firstDate > threeDaysAgo) {
          return rejectedButton();
        } else {
          return joinButton(props.companyIndex);
        }
      }
    } else {
      return <div>Loading...</div>;
    }
  };

  return requestToJoinPending();
};

export default customButton;
