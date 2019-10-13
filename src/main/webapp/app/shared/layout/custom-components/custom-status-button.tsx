import React, { useState } from 'react';
import { Button, Tooltip, ButtonGroup, Spinner } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router-dom';
import { INotification } from 'app/shared/model/notification.model';
import moment from 'moment';
import { NOTIFICATIONS } from '../../../config/constants';
import '../../../app.scss';

const customButton = props => {
  const [tooltipOpen, setTooltipOpen] = useState(false);

  const toggle = () => {
    setTooltipOpen(!tooltipOpen);
  };

  const tooltipMessageApplyAgain = 'You can apply again in';
  const tooltipMessageExpiresIn = 'This invitation expires in';

  const buttonTooltipMessageApplyAgain = (tooltipMessagePrefix, numberOfDaysAgo: Date, sentDate: Date): String => {
    const threeDaysTimeStamp = new Date().getTime() - numberOfDaysAgo.getTime();
    const timeLeft = new Date().getTime() - sentDate.getTime();
    const timeBeforeCanApplyAgain = Math.ceil((threeDaysTimeStamp - timeLeft) / (1000 * 60 * 60));
    const days =
      Math.round(timeBeforeCanApplyAgain / 24) === 1
        ? `${Math.round(timeBeforeCanApplyAgain / 24)} day`
        : `${Math.round(timeBeforeCanApplyAgain / 24)} days`;
    const hours =
      timeBeforeCanApplyAgain % 24 === 1
        ? `and ${timeBeforeCanApplyAgain % 24} hour`
        : timeBeforeCanApplyAgain % 24 === 0
        ? ''
        : `and ${timeBeforeCanApplyAgain % 24} hours`;
    const tooltipMessage: String = `${tooltipMessagePrefix} ${days} ${hours}`;
    return tooltipMessage;
  };

  const submittedButton = (): JSX.Element => (
    <Button color="primary" size="sm" disabled>
      <FontAwesomeIcon icon="file-signature" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.submitted">Application Submitted</Translate>
      </span>
    </Button>
  );

  const rejectedButton = (tooltipMessage: String): JSX.Element => (
    <span id="rejectButton">
      <Button color="danger" size="sm" disabled>
        <FontAwesomeIcon icon="ban" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.rejected">Application Rejected</Translate>
        </span>
      </Button>
      <Tooltip placement="top" isOpen={tooltipOpen} target="rejectButton" toggle={toggle}>
        {tooltipMessage}
      </Tooltip>
    </span>
  );

  const rejectedInviteButton = (tooltipMessage: String): JSX.Element => (
    <span id="rejectInviteButton">
      <Button color="danger" size="sm" disabled>
        <FontAwesomeIcon icon="ban" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.rejected">Invitation Rejected</Translate>
        </span>
      </Button>
      <Tooltip placement="top" isOpen={tooltipOpen} target="rejectInviteButton" toggle={toggle}>
        {tooltipMessage}
      </Tooltip>
    </span>
  );

  const firedButton = (tooltipMessage: String): JSX.Element => (
    <span id="firedButton">
      <Button color="danger" size="sm" disabled>
        <FontAwesomeIcon icon="ban" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.fired">Fired</Translate>
        </span>
      </Button>
      <Tooltip placement="top" isOpen={tooltipOpen} target="firedButton" toggle={toggle}>
        {tooltipMessage}
      </Tooltip>
    </span>
  );

  const joinButton = (companyId: Number): JSX.Element => (
    <Button id="joinButton" tag={Link} to={`${props.url}/${companyId}/join`} color="primary" size="sm">
      <FontAwesomeIcon icon="file-signature" />{' '}
      <span className="d-none d-md-inline">
        <Translate contentKey="entity.action.join">Request to Join</Translate>
      </span>
    </Button>
  );

  const acceptButton = (tooltipMessage: String, companyId: Number): JSX.Element => (
    <div className="btn-group flex-btn-group-container" id="acceptAndRejectButton">
      <Button tag={Link} to={`${props.url}/${companyId}/accept-invitation`} color="success" size="sm">
        <FontAwesomeIcon icon="check" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.accept">Accept Invitation</Translate>
        </span>
      </Button>
      <Button tag={Link} to={`${props.url}/${companyId}/reject-invitation`} color="danger" size="sm">
        <FontAwesomeIcon icon="ban" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.reject">Invitation Rejected</Translate>
        </span>
      </Button>
      <Tooltip placement="top" isOpen={tooltipOpen} target="acceptAndRejectButton" toggle={toggle}>
        {tooltipMessage}
      </Tooltip>
    </div>
  );

  const requestToJoinPending = (): JSX.Element => {
    if (props.notifications) {
      const employeeNotifications: INotification[] = props.notifications.filter(value => value.company === props.companyIndex);
      const requestedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.REQUEST_TO_JOIN === value.format);
      const rejectedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.REJECT_REQUEST === value.format);
      const firedNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.FIRED === value.format);
      const inviteNotifications: INotification[] = employeeNotifications.filter(value => NOTIFICATIONS.INVITATION === value.format);
      const rejectedInviteNotifications: INotification[] = employeeNotifications.filter(
        value => NOTIFICATIONS.REJECT_INVITE === value.format
      );
      const now1 = new Date();
      now1.setDate(now1.getDate() - 3);
      const threeDaysAgo = new Date(now1); // Three days ago
      const now2 = new Date();
      now2.setDate(now2.getDate() - 30); // Thirty days ago
      const thirtyDaysAgo = new Date(now2);

      if (
        rejectedNotifications.length === 0 &&
        requestedNotifications.length === 0 &&
        firedNotifications.length === 0 &&
        inviteNotifications.length === 0 &&
        rejectedInviteNotifications.length === 0
      ) {
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
          const tooltipMessage: String = buttonTooltipMessageApplyAgain(tooltipMessageApplyAgain, thirtyDaysAgo, notificationDate);
          return firedButton(tooltipMessage);
        }
      }

      if (rejectedInviteNotifications.length > 0) {
        let notificationDate: Date = moment(rejectedInviteNotifications[0].sentDate).toDate();

        rejectedInviteNotifications.forEach(nextNotification => {
          const nextDate: Date = moment(nextNotification.sentDate).toDate();
          if (nextDate > notificationDate) {
            notificationDate = moment(nextNotification.sentDate).toDate();
          }
        });

        if (notificationDate > threeDaysAgo) {
          const tooltipMessage: String = buttonTooltipMessageApplyAgain(tooltipMessageApplyAgain, threeDaysAgo, notificationDate);
          const rejectMessage: String = 'You have rejected the invitation. ' + tooltipMessage;
          return rejectedInviteButton(rejectMessage);
        }
      }

      if (inviteNotifications.length > 0) {
        let notificationDate: Date = moment(inviteNotifications[0].sentDate).toDate();

        inviteNotifications.forEach(nextNotification => {
          const nextDate: Date = moment(nextNotification.sentDate).toDate();
          if (nextDate > notificationDate) {
            notificationDate = moment(nextNotification.sentDate).toDate();
          }
        });

        if (notificationDate > thirtyDaysAgo) {
          const tooltipMessage: String = buttonTooltipMessageApplyAgain(tooltipMessageExpiresIn, thirtyDaysAgo, notificationDate);
          return acceptButton(tooltipMessage, props.companyIndex);
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
          const tooltipMessage: String = buttonTooltipMessageApplyAgain(tooltipMessageApplyAgain, threeDaysAgo, firstDate);
          return rejectedButton(tooltipMessage);
        } else {
          return joinButton(props.companyIndex);
        }
      } else {
        if (firstDate > threeDaysAgo) {
          return submittedButton();
        } else {
          return joinButton(props.companyIndex);
        }
      }
    } else {
      return <Spinner />;
    }
  };

  return requestToJoinPending();
};

export default customButton;
