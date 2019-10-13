import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './employee.reducer';
// tslint:disable-next-line:no-unused-variable

export interface IEmployeeDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class EmployeeDetail extends React.Component<IEmployeeDetailProps> {
  _isMounted = false;

  componentDidMount() {
    this._isMounted = true;
    this.props.getEntity(this._isMounted, this.props.match.params.id);
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  render() {
    const { employeeEntity } = this.props;
    return (
      <Row className="justify-content-center">
        <Col>
          {employeeEntity.image ? (
            <div>
              <a>
                <img
                  src={`data:${employeeEntity.imageContentType};base64,${employeeEntity.image}`}
                  style={{
                    maxHeight: '100px',
                    borderRadius: '50%'
                  }}
                />
              </a>
            </div>
          ) : (
            <div>
              <img
                src={`content/images/company-logo.png`}
                style={{
                  maxHeight: '100px',
                  borderRadius: '50%'
                }}
              />
            </div>
          )}
          <h2>{employeeEntity.login}</h2>
          <dl className="jh-entity-details">
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
              <Translate contentKey="cidApp.employee.company">Company</Translate>
            </dt>
            <dd>{employeeEntity.company && employeeEntity.company.name ? employeeEntity.company.name : ''}</dd>
          </dl>
          <Button tag={Link} to="/company/company-status" replace>
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
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
