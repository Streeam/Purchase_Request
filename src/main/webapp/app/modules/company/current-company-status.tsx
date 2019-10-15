import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Button, Row, Col, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';
import { getCurrentUsersCompanyAsync as getCurrentCompany } from '../../entities/company/company.reducer';
import '../../app.scss';
import { getSession } from '../../shared/reducers/authentication';
import { getCurrentEmployeeAsync } from '../../entities/employee/employee.reducer';
import CompanyStructure from './company-employee';
import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';
import PopoverInfo from '../../shared/layout/custom-components/popover-info/popover-info';

export interface ICompanyProps extends StateProps, DispatchProps {}

export const companyDetail = (props: ICompanyProps) => {
  let _isMounted = false;
  const lableStyle = { color: 'black' };
  const {
    companyEntity,
    isCurrentUserManager,
    companiesAreLoading,
    notificationsAreLoading,
    currentEmployeeIsLoading,
    acceptOrDeclineRequestUpdating,
    userAccountIsLoading,
    companyIsUpdating
  } = props;

  const isLoading =
    companiesAreLoading ||
    notificationsAreLoading ||
    currentEmployeeIsLoading ||
    acceptOrDeclineRequestUpdating ||
    userAccountIsLoading ||
    companyIsUpdating;

  useEffect(() => {
    _isMounted = true;
    props.getCurrentCompany(_isMounted);
    props.getCurrentEmployeeAsync(_isMounted);
    return () => (_isMounted = false);
  }, [props.getCurrentCompany, props.getCurrentEmployeeAsync, _isMounted]);

  return isLoading ? (
    <LoadingModal />
  ) : (
    <div>
      <div>
        <div
          style={{
            display: 'inline-block',
            textAlign: 'left',
            width: '50%'
          }}
        >
          <div style={{ display: 'inline-block' }}>
            <h1 style={lableStyle}>{companyEntity.name}</h1>
          </div>
          <div style={{ display: 'inline-block' }}>
            {isCurrentUserManager ? (
              <Button tag={Link} to={`/entity/company/${companyEntity.id}/edit`} replace color="link" title="Edit Company">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
              </Button>
            ) : (
              <div />
            )}
          </div>
        </div>
        <div
          style={{
            display: 'inline-block',
            textAlign: 'right',
            width: '50%'
          }}
        >
          {companyEntity && companyEntity.companyLogo ? (
            <div>
              <a onClick={openFile(companyEntity.companyLogoContentType, companyEntity.companyLogo)}>
                <img
                  src={`data:${companyEntity.companyLogoContentType};base64,${companyEntity.companyLogo}`}
                  style={{
                    maxHeight: '70px',
                    borderRadius: '5%'
                  }}
                />
              </a>
            </div>
          ) : (
            <div>
              <img src={`content/images/company-logo.png`} style={{ maxHeight: '50px' }} />
            </div>
          )}
        </div>
      </div>
      <br />
      <h4>Company Details</h4>
      <Table>
        <thead>
          <tr>
            <th>
              <Translate contentKey="cidApp.company.email">Email</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.phone">Phone</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.addressLine1">Address</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.city">City</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.country">Country</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{companyEntity.email}</td>
            <td>{companyEntity.phone}</td>
            <td>{companyEntity.addressLine1}</td>
            <td>{companyEntity.city}</td>
            <td>{companyEntity.country}</td>
            <td>{companyEntity.postcode}</td>
          </tr>
        </tbody>
      </Table>
      <br />
      <br />
      <br />
      <h4>Company Structure</h4>
      <CompanyStructure {...props} />
      <br />
      <br />
      <PopoverInfo />
    </div>
  );
};

const mapStateToProps = ({ company, employee, authentication, notification }: IRootState) => ({
  companyEntity: company.employeeEntity,
  curentEmployee: employee.currentEmployeeEntity,
  isCurrentUserManager: authentication.isCurrentUserManager,
  companiesAreLoading: company.loading,
  notificationsAreLoading: notification.loading,
  currentEmployeeIsLoading: employee.loading,
  acceptOrDeclineRequestUpdating: employee.updating,
  userAccountIsLoading: authentication.loading,
  companyIsUpdating: company.updating
});

const mapDispatchToProps = { getCurrentCompany, getSession, getCurrentEmployeeAsync };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(React.memo(companyDetail));
