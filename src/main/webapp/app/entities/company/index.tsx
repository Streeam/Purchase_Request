import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Company from './company-crud/company';
import JoinCompany from './join-companies';
import CurrentCompanyStatus from './current-company-status';
import CompanyUpdate from './company-crud/company-update';
import CompanyCreate from './company-crud/company-create';
import CompanyDeleteDialog from './company-dialog-confirmation/company-delete-dialog';
import CompanyJoinDialog from './company-dialog-confirmation/company-join-dialog';
import FireEmployeeDialog from './company-dialog-confirmation/company-fire-dialog';
import InviteEmployeeDialog from './company-dialog-confirmation/company-invite-dialog';
import AcceptInvitationDialog from './company-dialog-confirmation/company-accept-invitation-dialog';
import RejectInvitationDialog from './company-dialog-confirmation/company-reject-invitation-dialog';
import CompanyStatus from './company-status';
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/join`} component={CompanyJoinDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/accept-invitation`} component={AcceptInvitationDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/reject-invitation`} component={RejectInvitationDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id`} component={CompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company`} component={JoinCompany} />
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CompanyCreate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CompanyUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/company-status`} component={CurrentCompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/fire/:id`} component={FireEmployeeDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/invite/:email`} component={InviteEmployeeDialog} />
      <ErrorBoundaryRoute path={match.url} component={Company} />
    </Switch>
    <ErrorBoundaryRoute path={`${match.url}/:id/delete`} component={CompanyDeleteDialog} />
  </>
);

export default Routes;
