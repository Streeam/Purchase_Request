import React from 'react';
import { Translate, translate } from 'react-jhipster';
import { Button, Modal, ModalHeader, ModalBody, ModalFooter, Label, Alert, Row, Col } from 'reactstrap';
import { AvForm, AvField, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { Link } from 'react-router-dom';

import '../../app.scss';

export interface ILoginModalProps {
  showModal: boolean;
  loginError: boolean;
  handleLogin: Function;
  handleClose: Function;
}

const loginModal = (props: ILoginModalProps) => {
  const handleSubmit = (event, errors, { username, password, rememberMe }) => {
    const { handleLogin } = props;
    handleLogin(username, password, rememberMe);
  };
  const { loginError, handleClose } = props;

  return (
    <Modal isOpen={props.showModal} toggle={handleClose} backdrop="static" id="login-page" autoFocus={false}>
      <AvForm onSubmit={handleSubmit}>
        <ModalHeader id="login-title" toggle={handleClose}>
          <Translate contentKey="login.title">Sign in</Translate>
        </ModalHeader>
        <ModalBody>
          <Row>
            <Col md="12">
              {loginError ? (
                <Alert color="danger">
                  <Translate contentKey="login.messages.error.authentication">
                    <strong>Failed to sign in!</strong> Please check your credentials and try again.
                  </Translate>
                </Alert>
              ) : null}
            </Col>
            <Col md="12">
              <AvField
                name="username"
                label={translate('global.form.username.label')}
                placeholder={translate('global.form.username.placeholder')}
                required
                errorMessage="Username cannot be empty!"
                autoFocus
              />
              <AvField
                name="password"
                type="password"
                label={translate('login.form.password')}
                placeholder={translate('login.form.password.placeholder')}
                required
                errorMessage="Password cannot be empty!"
              />
              <AvGroup check inline>
                <Label className="form-check-label">
                  <AvInput type="checkbox" name="rememberMe" /> <Translate contentKey="login.form.rememberme">Remember me</Translate>
                </Label>
              </AvGroup>
            </Col>
          </Row>
          <div className="mt-1">&nbsp;</div>
          <Alert color="light">
            <Link to="/reset/request">
              <Translate contentKey="login.password.forgot">Did you forget your password?</Translate>
            </Link>
          </Alert>
          <Alert color="light">
            <span>
              <Translate contentKey="global.messages.info.register.noaccount">You don't have an account yet?</Translate>
            </span>{' '}
            <Link to="/register">
              <Translate contentKey="global.messages.info.register.link">Register a new account</Translate>
            </Link>
          </Alert>
        </ModalBody>
        <ModalFooter>
          <Button onClick={handleClose} tabIndex="1">
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>{' '}
          <Button type="submit">
            <Translate contentKey="login.form.button">Sign in</Translate>
          </Button>
        </ModalFooter>
      </AvForm>
    </Modal>
  );
};

export default loginModal;
