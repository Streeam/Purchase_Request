import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { openFile, Translate, translate, getSortState, IPaginationBaseState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities as getCompanies } from '../../entities/company/company.reducer';
// tslint:disable-next-line:no-unused-variable
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import '../../../content/css/grid.css';
import { getAsyncCurentEntities as getNotifications } from '../../entities/notification/notification.reducer';
import CustomButton from '../../shared/layout/custom-components/custom-status-button';
import { getCurrentEmployeeAsync } from '../../entities/employee/employee.reducer';

export interface ICompanyProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface ICompanyState extends IPaginationBaseState {
  search: string;
}

export const joinCompany = props => {
  let _isMounted = false;
  const { companyList, match, notifications, currentEmployee } = props;

  useEffect(() => {
    _isMounted = true;
    props.getCompanies(_isMounted);
    props.getNotifications(_isMounted);
    props.getCurrentEmployeeAsync(_isMounted);
    return () => (_isMounted = false);
  }, []);

  return (
    <div>
      <h2 id="company-heading">
        <Translate contentKey="cidApp.company.home.join">Join a Company</Translate>
      </h2>
      <div className="table-responsive">
        {companyList && companyList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th />
                <th className="hand">
                  <Translate contentKey="cidApp.company.name">Name</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.email">Email</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.phone">Phone</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.addressLine1">Address</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.city">City</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.country">Country</Translate>
                </th>
                <th className="hand">
                  <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {companyList.map((company, i) => (
                <tr key={`entity-${i}`}>
                  <td style={{ textAlign: 'center' }}>
                    {company.companyLogo ? (
                      <div>
                        <a onClick={openFile(company.companyLogoContentType, company.companyLogo)}>
                          <img src={`data:${company.companyLogoContentType};base64,${company.companyLogo}`} style={{ maxHeight: '30px' }} />
                          &nbsp;
                        </a>
                      </div>
                    ) : (
                      <div>
                        <img src={`content/images/company-logo.png`} style={{ maxHeight: '20px' }} />
                      </div>
                    )}
                  </td>
                  <td>{company.name}</td>
                  <td>{company.email}</td>
                  <td>{company.phone}</td>
                  <td>{company.addressLine1}</td>
                  <td>{company.city}</td>
                  <td>{company.country}</td>
                  <td>{company.postcode}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${company.id}`} color="info" size="sm">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <CustomButton
                        url={props.match.url}
                        currentEmployee={currentEmployee}
                        companyIndex={company.id}
                        notifications={...notifications}
                      />
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          <div className="alert alert-warning">
            <Translate contentKey="cidApp.company.home.notFound">No Companies found</Translate>
          </div>
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ company, notification, employee }: IRootState) => ({
  companyList: company.entities,
  notifications: notification.currentEntities,
  currentEmployee: employee.currentEmployeeEntity
});

const mapDispatchToProps = {
  getSearchEntities,
  getCompanies,
  getNotifications,
  getCurrentEmployeeAsync
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(joinCompany);
