import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import JoinCompany from './join-companies';
import CurrentCompanyStatus from './current-company-status';
import CompanyJoinDialog from './company-dialog-confirmation/company-join-dialog';
import FireEmployeeDialog from './company-dialog-confirmation/company-fire-dialog';
import HireEmployee from './company-invite';
import InviteEmployeeDialog from './company-dialog-confirmation/company-invite-dialog';
import AcceptInvitationDialog from './company-dialog-confirmation/company-accept-invitation-dialog';
import RejectInvitationDialog from './company-dialog-confirmation/company-reject-invitation-dialog';
import CompanyStatus from './company-status';
import Applicants from './company-applicants';
import RejectRequestDialog from './company-dialog-confirmation/company-reject-request-dialog';
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/join`} component={CompanyJoinDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/accept-invitation`} component={AcceptInvitationDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id/reject-invitation`} component={RejectInvitationDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company/:id`} component={CompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/join-company`} component={JoinCompany} />
      <ErrorBoundaryRoute exact path={`${match.url}/company-status`} component={CurrentCompanyStatus} />
      <ErrorBoundaryRoute exact path={`${match.url}/invite`} component={HireEmployee} />
      <ErrorBoundaryRoute exact path={`${match.url}/applicants/:companyId/reject-employee/:employeeId`} component={RejectRequestDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/company-status/:companyId/fire/:employeeId`} component={FireEmployeeDialog} />
      <ErrorBoundaryRoute exact path={`${match.url}/applicants`} component={Applicants} />
      <ErrorBoundaryRoute exact path={`${match.url}/invite/:companyId/invite-employee/:email`} component={InviteEmployeeDialog} />
    </Switch>
  </>
);

export default Routes;
