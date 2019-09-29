import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Company from './company';
import JoinCompany from './join-companies';
import CurrentCompanyStatus from './current-company-status';
import CompanyUpdate from './company-update';
import CompanyCreate from './company-create';
import CompanyDeleteDialog from './company-delete-dialog';
import CompanyJoinDialog from './company-join-dialog';
import FireEmployeeDialog from './company-fire-dialog';
import CompanyStatus from './company-status';
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/:companyId/hire-employee/:employeeId`} component={CurrentCompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/join`} component={CompanyJoinDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id`} component={CompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CompanyCreate} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company`} component={JoinCompany} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CompanyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/company-status`} component={CurrentCompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/fire/:id`} component={FireEmployeeDialog} />
      <ErrorBoundaryRoute path={match.url} component={Company} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={CompanyDeleteDialog} />
  </>
);

export default Routes;
