import React, { Fragment } from 'react';
import { connect } from 'react-redux';
import { Button, Table, ButtonGroup } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router-dom';

import { IRootState } from 'app/shared/reducers';
import { inviteEmployee } from '../../entities/employee/employee.reducer';
import { employeesToHire } from '../../shared/util/entity-utils';
import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';
import PopoverInfo from 'app/shared/layout/custom-components/popover-info/popover-info';

const companyInvite = props => {
  const { currentEmployee, employeeList, employeesLoading, employeesUpdating } = props;
  const isLoading = employeesLoading || employeesUpdating;

  // A list with only the available users to hire
  const invitees = employeesToHire(employeeList);

  const popupBody = `Invite an employee to join your company. The invitation expires in 14 days`;
  const popupTitle = `Invite Employee`;

  const tabContent = invitees ? (
    invitees.map((employee, i) => (
      <tr key={`entity-${i}`}>
        <td style={{ maxWidth: '10px' }}>
          {employee.image ? (
            <div>
              <img
                src={`data:${employee.imageContentType};base64,${employee.image}`}
                style={{
                  maxHeight: '30px',
                  borderRadius: '50%'
                }}
              />
            </div>
          ) : (
            <div>
              <img
                src={`content/images/default_profile_icon.png`}
                style={{
                  maxHeight: '30px',
                  borderRadius: '50%'
                }}
              />
            </div>
          )}
        </td>
        <td>{employee.login ? employee.login : ''}</td>
        <td>{employee.firstName && employee.lastName ? `${employee.firstName}` + ` ${employee.lastName}` : ''}</td>
        <td>{employee.email ? employee.email : ''}</td>
        <td className="text-right">
          <div className="table-button">
            <div className="btn-group flex-btn-group-container">
              <ButtonGroup>
                <Button tag={Link} to={`/entity/employee/${employee.id}`} size="sm" outline color="info" title="View Employee Details">
                  <FontAwesomeIcon icon="eye" />{' '}
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.view">View</Translate>
                  </span>
                </Button>
                <Button
                  tag={Link}
                  to={`/company/invite/${currentEmployee.companyId}/invite-employee/${employee.email}`}
                  size="sm"
                  outline
                  color="success"
                  title="Invite Employee"
                >
                  <FontAwesomeIcon icon="user" />{' '}
                  <span className="d-none d-md-inline">
                    <Translate contentKey="entity.action.invite">Invite</Translate>
                  </span>
                </Button>
              </ButtonGroup>
            </div>
          </div>
        </td>
      </tr>
    ))
  ) : (
    <div>Loading...</div>
  );

  return isLoading ? (
    <LoadingModal />
  ) : (
    <Fragment>
      <h3>Hire Employees</h3>
      {invitees && invitees.length > 0 ? (
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
      ) : (
        <div className="alert alert-light">No Available employees</div>
      )}
      <PopoverInfo popupBody={popupBody} popupTitle={popupTitle} />
    </Fragment>
  );
};

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeList: employee.entities,
  currentEmployee: employee.currentEmployeeEntity,
  employeesLoading: employee.loading,
  employeesUpdating: employee.updating
});

const mapDispatchToProps = {
  inviteEmployee
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyInvite);
