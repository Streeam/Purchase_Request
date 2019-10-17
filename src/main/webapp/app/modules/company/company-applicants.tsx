import React, { Fragment } from 'react';
import { connect } from 'react-redux';
import { Button, Table } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { Translate } from 'react-jhipster';
import { Link } from 'react-router-dom';

import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';
import { IRootState } from 'app/shared/reducers';
import { hireEmployee } from '../../entities/company/company.reducer';
import { IEmployee } from 'app/shared/model/employee.model';
import { INotification } from 'app/shared/model/notification.model';
import { applicantsEmployees } from '../../shared/util/entity-utils';

const companyApplicants = props => {
  const { companyEntity, employeeList, companyLoading, companyIsUpdating } = props;
  const isLoading = companyLoading || companyIsUpdating;

  const handleAccept = (employeeId: Number) => {
    if (employeeId && companyEntity.id) {
      props.hireEmployee(companyEntity.id, employeeId, true);
    }
  };

  // To be replaced with a sql query
  const employeeWithNotificationsAddressedToThisCompany: IEmployee[] = employeeList.filter(employee => {
    const notificationsAddressedToThisCompany: INotification[] = employee.notifications.filter(
      notification => notification.company === companyEntity.id
    );
    return notificationsAddressedToThisCompany.length > 0;
  });

  const applicants = applicantsEmployees(employeeWithNotificationsAddressedToThisCompany);

  const tabContent = applicants ? (
    applicants.map((employee, i) => (
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
          <div className="btn-group flex-btn-group-container">
            <Button
              color="success"
              size="sm"
              // tslint:disable
              onClick={() => handleAccept(employee.id)}
            >
              <FontAwesomeIcon icon="check" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.accept">Accept</Translate>
              </span>
            </Button>
            <Button color="danger" size="sm" tag={Link} to={`${props.match.url}/${companyEntity.id}/reject-employee/${employee.id}`}>
              <FontAwesomeIcon icon="ban" />{' '}
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.reject">Reject</Translate>
              </span>
            </Button>
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
      <h3>Applicants</h3>
      {applicants && applicants.length > 0 ? (
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
        <div className="alert alert-light">No Applicants</div>
      )}
    </Fragment>
  );
};

const mapStateToProps = ({ employee, company }: IRootState) => ({
  companyEntity: company.employeeEntity,
  employeeList: employee.entities,
  companyLoading: company.loading,
  companyIsUpdating: company.updating
});

const mapDispatchToProps = {
  hireEmployee
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyApplicants);
