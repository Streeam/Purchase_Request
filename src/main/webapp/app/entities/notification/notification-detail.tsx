import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './notification.reducer';
import { INotification } from 'app/shared/model/notification.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface INotificationDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class NotificationDetail extends React.Component<INotificationDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { notificationEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="cidApp.notification.detail.title">Notification</Translate> [<b>{notificationEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="comment">
                <Translate contentKey="cidApp.notification.comment">Comment</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.comment}</dd>
            <dt>
              <span id="sentDate">
                <Translate contentKey="cidApp.notification.sentDate">Sent Date</Translate>
              </span>
            </dt>
            <dd>
              <TextFormat value={notificationEntity.sentDate} type="date" format={APP_DATE_FORMAT} />
            </dd>
            <dt>
              <span id="read">
                <Translate contentKey="cidApp.notification.read">Read</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.read ? 'true' : 'false'}</dd>
            <dt>
              <span id="format">
                <Translate contentKey="cidApp.notification.format">Format</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.format}</dd>
            <dt>
              <span id="company">
                <Translate contentKey="cidApp.notification.company">Company</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.company}</dd>
            <dt>
              <span id="referenced_user">
                <Translate contentKey="cidApp.notification.referenced_user">Referenced User</Translate>
              </span>
            </dt>
            <dd>{notificationEntity.referenced_user}</dd>
            <dt>
              <Translate contentKey="cidApp.notification.employee">Employee</Translate>
            </dt>
            <dd>{notificationEntity.employeeId ? notificationEntity.employeeId : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/notification" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/notification/${notificationEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
    );
  }
}

const mapStateToProps = ({ notification }: IRootState) => ({
  notificationEntity: notification.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(NotificationDetail);
