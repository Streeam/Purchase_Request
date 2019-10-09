import React, { useEffect } from 'react';
import { Button, Col, Alert, Row } from 'reactstrap';
import { connect } from 'react-redux';
import { Translate, translate, setFileData, byteSize } from 'react-jhipster';
import { AvForm, AvField } from 'availity-reactstrap-validation';

import { locales, languages } from 'app/config/translation';
import { IRootState } from 'app/shared/reducers';
import { getSession } from 'app/shared/reducers/authentication';
import { saveAccountSettings, reset as accountReset } from './settings.reducer';
import { getCurrentEmployeeEntity, updateEntity, setBlob, reset as employeeReset } from '../../../entities/employee/employee.reducer';
import '../../../app.scss';
import { RouteComponentProps } from 'react-router-dom';
import Avatar from '../../../shared/layout/custom-components/avatar/avatar';

export interface IUserSettingsProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

export const SettingsPage = (props: IUserSettingsProps) => {
  let _isMounted = false;

  const { currentEmployee } = props;

  useEffect(() => {
    _isMounted = true;
    props.getSession();
    props.getCurrentEmployeeEntity(_isMounted);
    return () => {
      props.accountReset();
      _isMounted = false;
    };
  }, []);

  const handleValidSubmit = (event, error, values) => {
    const account = {
      ...props.account,
      ...values
    };
    props.saveAccountSettings(account);
    event.persist();
  };

  const handleClose = () => {
    props.history.push('/');
  };

  return (
    <div>
      <Avatar imageContentType={currentEmployee.imageContentType} image={currentEmployee.image} maxHeight="100px" round />
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
            {/* Email */}
            <AvField
              name="email"
              label={translate('global.form.email.label')}
              placeholder={translate('global.form.email.placeholder')}
              type="email"
              validate={{
                required: { value: true, errorMessage: translate('global.messages.validate.email.required') },
                minLength: { value: 5, errorMessage: translate('global.messages.validate.email.minlength') },
                maxLength: { value: 254, errorMessage: translate('global.messages.validate.email.maxlength') }
              }}
              value={props.account.email}
            />
            {/* Language key */}
            <AvField
              type="select"
              id="langKey"
              name="langKey"
              className="form-control"
              label={translate('settings.form.language')}
              value={props.account.langKey}
            >
              {locales.map(locale => (
                <option value={locale} key={locale}>
                  {languages[locale].name}
                </option>
              ))}
            </AvField>
            <Button className="Button" type="submit">
              <Translate contentKey="settings.form.button">Save</Translate>
            </Button>
          </AvForm>
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  account: storeState.authentication.account,
  isAuthenticated: storeState.authentication.isAuthenticated,
  currentEmployee: storeState.employee.currentEmployeeEntity
});

const mapDispatchToProps = { getSession, saveAccountSettings, accountReset, getCurrentEmployeeEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(SettingsPage);
