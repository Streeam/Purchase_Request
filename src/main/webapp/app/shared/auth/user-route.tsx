import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Route, Redirect, RouteProps } from 'react-router-dom';
import { Translate } from 'react-jhipster';
import { IRootState } from 'app/shared/reducers';
import ErrorBoundary from 'app/shared/error/error-boundary';
import { AUTHORITIES } from 'app/config/constants';

export interface IPrivateRouteProps extends RouteProps, StateProps {}

export const UserPrivateRoute = ({
  component: Component,
  isAuthenticated,
  sessionHasBeenFetched,
  isAuthorized,
  ...rest
}: IPrivateRouteProps) => {
  const checkAuthorities = props => (
    <ErrorBoundary>
      <Component isUserOnly={isAuthorized} {...props} />
    </ErrorBoundary>
  );

  const renderRedirect = props => {
    if (!sessionHasBeenFetched) {
      return <div />;
    } else {
      return isAuthenticated ? checkAuthorities(props) : <div>Home unauthorize</div>;
    }
  };

  if (!Component) throw new Error(`A component needs to be specified for private route for path ${(rest as any).path}`);

  return <Route {...rest} render={renderRedirect} />;
};

export const hasOnlyUserRole = (authorities: string[]) => {
  if (authorities && authorities.length === 1) {
    return authorities.includes(AUTHORITIES.USER);
  }
  return false;
};

const mapStateToProps = ({ authentication: { isAuthenticated, account, sessionHasBeenFetched } }: IRootState) => ({
  isAuthenticated,
  isAuthorized: hasOnlyUserRole(account.authorities),
  sessionHasBeenFetched
});

type StateProps = ReturnType<typeof mapStateToProps>;

/**
 * A route wrapped in an authentication check so that routing happens only when you are authenticated.
 * Accepts same props as React router Route.
 * The route also checks for authorization if hasAnyAuthorities is specified.
 */
export const UserRoute = connect<StateProps, undefined>(
  mapStateToProps,
  null,
  null,
  { pure: false }
)(UserPrivateRoute);

export default UserRoute;
