import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IEmployee } from 'app/shared/model/employee.model';
import { getEntities as getEmployees } from 'app/entities/employee/employee.reducer';
import { getEntity, updateEntity, createEntity, reset } from './notification.reducer';
import { INotification } from 'app/shared/model/notification.model';
// tslint:disable-next-line:no-unused-variable
import { convertDateTimeFromServer, convertDateTimeToServer } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface INotificationUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface INotificationUpdateState {
  isNew: boolean;
  employeeId: string;
}

export class NotificationUpdate extends React.Component<INotificationUpdateProps, INotificationUpdateState> {
  constructor(props) {
    super(props);
    this.state = {
      employeeId: '0',
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id);
    }

    this.props.getEmployees();
  }

  saveEntity = (event, errors, values) => {
    values.sentDate = convertDateTimeToServer(values.sentDate);

    if (errors.length === 0) {
      const { notificationEntity } = this.props;
      const entity = {
        ...notificationEntity,
        ...values
      };

      if (this.state.isNew) {
        this.props.createEntity(entity);
      } else {
        this.props.updateEntity(entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/entity/notification');
  };

  render() {
    const { notificationEntity, employees, loading, updating } = this.props;
    const { isNew } = this.state;

    return (
      <div>
        <Row className="justify-content-center">
          <Col md="8">
            <h2 id="cidApp.notification.home.createOrEditLabel">
              <Translate contentKey="cidApp.notification.home.createOrEditLabel">Create or edit a Notification</Translate>
            </h2>
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            {loading ? (
              <p>Loading...</p>
            ) : (
              <AvForm model={isNew ? {} : notificationEntity} onSubmit={this.saveEntity}>
                {!isNew ? (
                  <AvGroup>
                    <Label for="notification-id">
                      <Translate contentKey="global.field.id">ID</Translate>
                    </Label>
                    <AvInput id="notification-id" type="text" className="form-control" name="id" required readOnly />
                  </AvGroup>
                ) : null}
                <AvGroup>
                  <Label id="commentLabel" for="notification-comment">
                    <Translate contentKey="cidApp.notification.comment">Comment</Translate>
                  </Label>
                  <AvField id="notification-comment" type="text" name="comment" />
                </AvGroup>
                <AvGroup>
                  <Label id="sentDateLabel" for="notification-sentDate">
                    <Translate contentKey="cidApp.notification.sentDate">Sent Date</Translate>
                  </Label>
                  <AvInput
                    id="notification-sentDate"
                    type="datetime-local"
                    className="form-control"
                    name="sentDate"
                    placeholder={'YYYY-MM-DD HH:mm'}
                    value={isNew ? null : convertDateTimeFromServer(this.props.notificationEntity.sentDate)}
                    validate={{
                      required: { value: true, errorMessage: translate('entity.validation.required') }
                    }}
                  />
                </AvGroup>
                <AvGroup>
                  <Label id="readLabel" check>
                    <AvInput id="notification-read" type="checkbox" className="form-control" name="read" />
                    <Translate contentKey="cidApp.notification.read">Read</Translate>
                  </Label>
                </AvGroup>
                <AvGroup>
                  <Label id="formatLabel" for="notification-format">
                    <Translate contentKey="cidApp.notification.format">Format</Translate>
                  </Label>
                  <AvInput
                    id="notification-format"
                    type="select"
                    className="form-control"
                    name="format"
                    value={(!isNew && notificationEntity.format) || 'INVITATION'}
                  >
                    <option value="INVITATION">{translate('cidApp.NotificationType.INVITATION')}</option>
                    <option value="WELCOME">{translate('cidApp.NotificationType.WELCOME')}</option>
                    <option value="NEW_EMPLOYEE">{translate('cidApp.NotificationType.NEW_EMPLOYEE')}</option>
                    <option value="FIRED">{translate('cidApp.NotificationType.FIRED')}</option>
                    <option value="ACCEPT_INVITE">{translate('cidApp.NotificationType.ACCEPT_INVITE')}</option>
                    <option value="REJECT_REQUEST">{translate('cidApp.NotificationType.REJECT_REQUEST')}</option>
                    <option value="REQUEST_TO_JOIN">{translate('cidApp.NotificationType.REQUEST_TO_JOIN')}</option>
                    <option value="LEFT_COMPANY">{translate('cidApp.NotificationType.LEFT_COMPANY')}</option>
                    <option value="COMPANY_DELETED">{translate('cidApp.NotificationType.COMPANY_DELETED')}</option>
                    <option value="ACCEPT_REQUEST">{translate('cidApp.NotificationType.ACCEPT_REQUEST')}</option>
                    <option value="REJECT_REQUEST">{translate('cidApp.NotificationType.REJECT_REQUEST')}</option>
                    <option value="OTHERS">{translate('cidApp.NotificationType.OTHERS')}</option>
                  </AvInput>
                </AvGroup>
                <AvGroup>
                  <Label id="companyLabel" for="notification-company">
                    <Translate contentKey="cidApp.notification.company">Company</Translate>
                  </Label>
                  <AvField id="notification-company" type="string" className="form-control" name="company" />
                </AvGroup>
                <AvGroup>
                  <Label id="referenced_userLabel" for="notification-referenced_user">
                    <Translate contentKey="cidApp.notification.referenced_user">Referenced User</Translate>
                  </Label>
                  <AvField id="notification-referenced_user" type="text" name="referenced_user" />
                </AvGroup>
                <AvGroup>
                  <Label for="notification-employee">
                    <Translate contentKey="cidApp.notification.employee">Employee</Translate>
                  </Label>
                  <AvInput id="notification-employee" type="select" className="form-control" name="employeeId" required>
                    {employees
                      ? employees.map(otherEntity => (
                          <option value={otherEntity.id} key={otherEntity.id}>
                            {otherEntity.id}
                          </option>
                        ))
                      : null}
                  </AvInput>
                  <AvFeedback>
                    <Translate contentKey="entity.validation.required">This field is required.</Translate>
                  </AvFeedback>
                </AvGroup>
                <Button tag={Link} id="cancel-save" to="/entity/notification" replace color="info">
                  <FontAwesomeIcon icon="arrow-left" />
                  &nbsp;
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.back">Back</Translate>
                  </span>
                </Button>
                &nbsp;
                <Button color="primary" id="save-entity" type="submit" disabled={updating}>
                  <FontAwesomeIcon icon="save" />
                  &nbsp;
                  <Translate contentKey="entity.action.save">Save</Translate>
                </Button>
              </AvForm>
            )}
          </Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  employees: storeState.employee.entities,
  notificationEntity: storeState.notification.entity,
  loading: storeState.notification.loading,
  updating: storeState.notification.updating,
  updateSuccess: storeState.notification.updateSuccess
});

const mapDispatchToProps = {
  getEmployees,
  getEntity,
  updateEntity,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(NotificationUpdate);
