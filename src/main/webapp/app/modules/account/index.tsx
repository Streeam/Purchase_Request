import React from 'react';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Settings from './settings/settings';
import Password from './password/password';
import AvatarModal from './settings/avatar-change-dialog';

const Routes = ({ match }) => (
  <div>
    <ErrorBoundaryRoute exact path={`${match.url}/settings/profile-icon`} component={AvatarModal} />
    <ErrorBoundaryRoute exact path={`${match.url}/settings`} component={Settings} />
    <ErrorBoundaryRoute path={`${match.url}/password`} component={Password} />
  </div>
);

export default Routes;
