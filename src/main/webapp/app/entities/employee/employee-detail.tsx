import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './employee.reducer';
import { IEmployee } from 'app/shared/model/employee.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IEmployeeDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EmployeeDetail extends React.Component<IEmployeeDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { employeeEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="cidApp.employee.detail.title">Employee</Translate> [<b>{employeeEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="login">
                <Translate contentKey="cidApp.employee.login">Login</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.login}</dd>
            <dt>
              <span id="firstName">
                <Translate contentKey="cidApp.employee.firstName">First Name</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.firstName}</dd>
            <dt>
              <span id="lastName">
                <Translate contentKey="cidApp.employee.lastName">Last Name</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.lastName}</dd>
            <dt>
              <span id="email">
                <Translate contentKey="cidApp.employee.email">Email</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.email}</dd>
            <dt>
              <span id="hired">
                <Translate contentKey="cidApp.employee.hired">Hired</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.hired ? 'true' : 'false'}</dd>
            <dt>
              <span id="language">
                <Translate contentKey="cidApp.employee.language">Language</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.language}</dd>
            <dt>
              <span id="image">
                <Translate contentKey="cidApp.employee.image">Image</Translate>
              </span>
            </dt>
            <dd>
              {employeeEntity.image ? (
                <div>
                  <a onClick={openFile(employeeEntity.imageContentType, employeeEntity.image)}>
                    <img src={`data:${employeeEntity.imageContentType};base64,${employeeEntity.image}`} style={{ maxHeight: '30px' }} />
                  </a>
                  <span>
                    {employeeEntity.imageContentType}, {byteSize(employeeEntity.image)}
                  </span>
                </div>
              ) : null}
            </dd>
            <dt>
              <Translate contentKey="cidApp.employee.user">User</Translate>
            </dt>
            <dd>{employeeEntity.userLogin ? employeeEntity.userLogin : ''}</dd>
            <dt>
              <Translate contentKey="cidApp.employee.company">Company</Translate>
            </dt>
            <dd>{employeeEntity.companyName ? employeeEntity.companyName : ''}</dd>
          </dl>
          <Button tag={Link} to="/entity/employee" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/employee/${employeeEntity.id}/edit`} replace color="primary">
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

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeEntity: employee.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(EmployeeDetail);
