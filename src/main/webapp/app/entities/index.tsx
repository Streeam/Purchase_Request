import React from 'react';
import { Switch } from 'react-router-dom';

// tslint:disable-next-line:no-unused-variable
import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Company from './company';
import Notification from './notification';
import Employee from './employee';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <ErrorBoundaryRoute path={`${match.url}/company`} component={Company} />
      <ErrorBoundaryRoute path={`${match.url}/notification`} component={Notification} />
      <ErrorBoundaryRoute path={`${match.url}/employee`} component={Employee} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
