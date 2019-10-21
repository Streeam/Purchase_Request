import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Button, Label, Row, Col } from 'reactstrap';
import { AvForm, AvGroup, AvInput, AvField, AvFeedback } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { getEntity } from '../../../entities/employee/employee.reducer';
import { updateUser, reset } from './user-management.reducer';
import { IRootState } from 'app/shared/reducers';
import { AUTHORITIES } from 'app/config/constants';
import { IEmployee } from 'app/shared/model/employee.model';
import PopoverInfo from 'app/shared/layout/custom-components/popover-info/popover-info';

export interface IUserManagementUpdateProps extends StateProps, DispatchProps {
  employeeEntity: IEmployee;
}

export const UserManagementUpdate = (props: IUserManagementUpdateProps) => {
  const employeeAuthorities = props.employeeEntity && props.employeeEntity.user ? props.employeeEntity.user.authorities : null;
  const [employeeAuthoritiesArray, setEmployeeAuthoritiesArray] = useState([]);

  useEffect(() => {
    setEmployeeAuthoritiesArray(employeeAuthorities ? employeeAuthorities.map(auth => auth.name) : []);
    return () => props.reset();
  }, []);

  const upperCaseFirst = text =>
    text
      .toLowerCase()
      .split(' ')
      .map(s => s.charAt(0).toUpperCase() + s.substring(1))
      .join(' ');

  const visibleRoles = [
    ...Object.values(AUTHORITIES).filter(role => {
      if (role === 'ROLE_ADMIN') {
        return false;
      }
      if (role === 'ROLE_USER') {
        return false;
      }
      if (role === 'ROLE_MANAGER') {
        return false;
      }
      if (role === 'ROLE_EMPLOYEE') {
        return false;
      }
      return true;
    })
  ];

  const popupBody = `Select from the list above what rols to assign to the employee. In order to select multiple roles hold down Ctrl
key while clicking on each role you want to assign.`;
  const popupTitle = `Role Assigment`;

  const { employeeEntity, loading, updating, isCurrentUserManager } = props;

  const saveUser = (event, values) => {
    const selectedRoles = values.authorities;
    const newRoles = employeeAuthoritiesArray.concat(selectedRoles);
    const uniqueNewRoles = newRoles.filter((value, index) => newRoles.indexOf(value) === index);
    const user = { ...employeeEntity.user };
    user.authorities = uniqueNewRoles;
    props.updateUser(user);
    setEmployeeAuthoritiesArray(uniqueNewRoles);
    event.persist();
  };

  return (
    <Row className="justify-content-center">
      <Col>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <AvForm onValidSubmit={saveUser}>
            <AvGroup>
              <AvInput
                type="select"
                className="form-control"
                name="authorities"
                value={employeeAuthoritiesArray}
                multiple
                disabled={!isCurrentUserManager}
              >
                {visibleRoles.map(role => (
                  <option value={role} key={role}>
                    {upperCaseFirst(role.replace(/_/g, ' ').slice(5))}
                  </option>
                ))}
              </AvInput>
            </AvGroup>
            <Button outline size="sm" tag={Link} to="/company/company-status" replace color="info">
              <FontAwesomeIcon icon="arrow-left" />
              &nbsp;
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.back">Back</Translate>
              </span>
            </Button>
            &nbsp;
            {isCurrentUserManager && (
              <Button outline size="sm" color="primary" type="submit" disabled={updating} title={'Update Roles'}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            )}
          </AvForm>
        )}
        <PopoverInfo popupBody={popupBody} popupTitle={popupTitle} />
      </Col>
    </Row>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  isCurrentUserManager: storeState.authentication.isCurrentUserManager,
  loading: storeState.userManagement.loading,
  updating: storeState.userManagement.updating
});

const mapDispatchToProps = { updateUser, reset, getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(UserManagementUpdate);
