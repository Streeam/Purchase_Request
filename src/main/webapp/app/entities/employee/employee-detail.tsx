import React, { Fragment, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Spinner } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './employee.reducer';
import RolesUpdate from '../../modules/administration/user-management/user-management-update-roles';
// tslint:disable-next-line:no-unused-variable

export interface IEmployeeDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const employeeDetail = (props: IEmployeeDetailProps) => {
  let _isMounted = false;
  const { employeeEntity } = props;

  useEffect(() => {
    _isMounted = true;
    props.getEntity(_isMounted, props.match.params.id);
    return () => (_isMounted = false);
  }, []);

  return employeeEntity && employeeEntity.user ? (
    <Fragment>
      <Row className="justify-content-center">
        <Col>
          {employeeEntity.image ? (
            <div style={{ padding: '10px' }}>
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
                src={`content/images/default_profile_icon.png`}
                style={{
                  maxHeight: '100px',
                  borderRadius: '50%'
                }}
              />
            </div>
          )}
        </Col>
        <Col>
          <dl className="jh-entity-details">
            <dt>
              <span id="login">
                <Translate contentKey="cidApp.employee.login">Login</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.user.login}</dd>
            <dt>
              <span id="firstName">
                <Translate contentKey="cidApp.employee.firstName">Full Name</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.user.firstName}</dd>
            <dt>
              <span id="jobTitle">
                <Translate contentKey="cidApp.employee.lastName">Job Title</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.user.lastName}</dd>
          </dl>
        </Col>
        <Col>
          <dl>
            <dt>
              <span id="email">
                <Translate contentKey="cidApp.employee.email">Email</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.email}</dd>
            <dt>
              <span id="language">
                <Translate contentKey="cidApp.employee.language">Language</Translate>
              </span>
            </dt>
            <dd>{employeeEntity.user.langKey}</dd>
            <dt>
              <Translate contentKey="cidApp.employee.company">Company</Translate>
            </dt>
            <dd>{employeeEntity.company && employeeEntity.company.name ? employeeEntity.company.name : ''}</dd>
          </dl>
          &nbsp;&nbsp;
        </Col>
      </Row>
      <Row>
        <Col>
          <h4>Assign Roles</h4>
          <RolesUpdate employeeEntity={employeeEntity} employeeId={props.match.params.id} />
          &nbsp;&nbsp;
        </Col>
      </Row>
    </Fragment>
  ) : (
    <p>Loading...</p>
  );
};

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeEntity: employee.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(employeeDetail);
