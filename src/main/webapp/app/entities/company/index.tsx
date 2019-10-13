import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Company from './company';
import CompanyUpdate from './company-update';
import CompanyDeleteDialog from './company-delete-dialog';
import CompanyLogoChangeDialog from '../../modules/account/settings/company-logo-change-dialog';

const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CompanyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit/company-icon`} component={CompanyLogoChangeDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CompanyUpdate} />
      <ErrorBoundaryRoute path={match.url} component={Company} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={CompanyDeleteDialog} />
  </>
);

export default Routes;
