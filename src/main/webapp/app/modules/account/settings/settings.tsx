import React, { useEffect, Fragment } from 'react';
import { Button, Col, Alert, Row } from 'reactstrap';
import { connect } from 'react-redux';
import { Translate, translate } from 'react-jhipster';
import { AvForm, AvField } from 'availity-reactstrap-validation';

import { locales, languages } from 'app/config/translation';
import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';
import { saveAccountSettings, reset } from './settings.reducer';
import { getCurrentEmployeeEntity } from '../../../entities/employee/employee.reducer';
import '../../../app.scss';
import { RouteComponentProps, Link } from 'react-router-dom';
import ProfileIcon from '../../../shared/layout/custom-components/avatar/avatar';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import LoadingModal from '../../../shared/layout/custom-components/loading-modal/loading-modal';

export interface IUserSettingsProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

export const SettingsPage = (props: IUserSettingsProps) => {
  let _isMounted = false;

  const { currentEmployee, updatingUser, updatingEmployee } = props;

  const isLoading = updatingUser || updatingEmployee;

  useEffect(() => {
    _isMounted = true;
    // props.getSession();
    // props.getCurrentEmployeeEntity(_isMounted);
    return () => {
      _isMounted = false;
      props.reset();
    };
  }, []);

  const handleValidSubmit = (event, values) => {
    const account = {
      ...props.account,
      ...values
    };
    props.saveAccountSettings(account);
    event.persist();
  };

  return isLoading ? (
    <LoadingModal />
  ) : (
    <Fragment>
      <ProfileIcon
        imageContentType={currentEmployee.imageContentType}
        image={currentEmployee.image}
        maxHeight="100px"
        round
        url={`${props.match.url}/profile-icon`}
        defaultSrc="content/images/default_profile_icon.png"
      />
      <Row className="justify-content-center">
        <Col md="7">
          <h2 id="settings-title">
            <Translate contentKey="settings.title" interpolate={{ username: props.account.login }}>
              User settings for {props.account.login}
            </Translate>
          </h2>
          <AvForm id="settings-form" onValidSubmit={handleValidSubmit}>
            {/* First name */}
            <AvField
              className="form-control"
              name="firstName"
              label={translate('settings.form.firstname')}
              id="firstName"
              placeholder={translate('settings.form.firstname.placeholder')}
              validate={{
                required: { value: true, errorMessage: translate('settings.messages.validate.firstname.required') },
                minLength: { value: 1, errorMessage: translate('settings.messages.validate.firstname.minlength') },
                maxLength: { value: 50, errorMessage: translate('settings.messages.validate.firstname.maxlength') }
              }}
              value={props.account.firstName}
            />
            {/* Last name */}
            <AvField
              className="form-control"
              name="lastName"
              label={translate('settings.form.lastname')}
              id="lastName"
              placeholder={translate('settings.form.lastname.placeholder')}
              validate={{
                required: { value: true, errorMessage: translate('settings.messages.validate.lastname.required') },
                minLength: { value: 1, errorMessage: translate('settings.messages.validate.lastname.minlength') },
                maxLength: { value: 50, errorMessage: translate('settings.messages.validate.lastname.maxlength') }
              }}
              value={props.account.lastName}
            />
            {/* Language key */}
            <AvField
              type="select"
              id="langKey"
              name="langKey"
              className="form-control"
              label={translate('settings.form.language')}
              value={props.account.langKey ? props.account.langKey : 'en'}
            >
              {locales.map(locale => (
                <option value={locale} key={locale}>
                  {languages[locale].name}
                </option>
              ))}
            </AvField>
            <Button outline color="info" size="sm" tag={Link} id="cancel-save" to="/" replace>
              <FontAwesomeIcon icon="arrow-left" />
              &nbsp;
              <span className="d-none d-md-inline">
                <Translate contentKey="entity.action.back">Back</Translate>
              </span>
            </Button>
            &nbsp;
            <Button outline color="primary" size="sm" id="save-entity" type="submit" disabled={updatingUser}>
              <FontAwesomeIcon icon="save" />
              &nbsp;
              <Translate contentKey="entity.action.save">Save</Translate>
            </Button>
          </AvForm>
        </Col>
      </Row>
    </Fragment>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  account: storeState.authentication.account,
  currentEmployee: storeState.employee.currentEmployeeEntity,
  updatingUser: storeState.authentication.loading,
  updatingEmployee: storeState.employee.updating
});

const mapDispatchToProps = { getSession, saveAccountSettings, reset, getCurrentEmployeeEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SettingsPage);
