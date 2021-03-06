import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Spinner } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { acceptCompanyInvitation } from '../../../entities/employee/employee.reducer';
import { getUser, getRoles, updateUser, reset as userReset } from '../../administration/user-management/user-management.reducer';
import { IRootState } from 'app/shared/reducers';

export interface ICompanyAcceptInvitationDialogProps extends DispatchProps, RouteComponentProps<{ id: string }> {}

export const companyAcceptInvitationDialog = props => {
  let _isMounted = false;

  useEffect(() => {
    _isMounted = true;
    props.getUser(props.currentEmployee.user.login);
  }, []);

  const confirmAccept = event => {
    props.acceptCompanyInvitation(true, props.match.params.id);
    props.history.push('/company/company-status');
  };

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.acceptinvite.title">Confirm the inviation</Translate>
      </ModalHeader>
      {props.loading ? (
        <Spinner color="primary" />
      ) : (
        <ModalBody id="cidApp.company.acceptinvitation.question">
          <Translate contentKey="cidApp.company.acceptinvitation.question" interpolate={{ name: props.match.params.id }}>
            Are you sure you want to acceptinvitation Company?
          </Translate>
        </ModalBody>
      )}
      <ModalFooter>
        <Button color="secondary" outline size="sm" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-acceptinvitation-company" color="success" outline size="sm" onClick={confirmAccept}>
          <FontAwesomeIcon icon="check" />
          &nbsp;
          <Translate contentKey="entity.action.accept">Join</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapDispatchToProps = { acceptCompanyInvitation, getUser, getRoles, updateUser, userReset };

const mapStateToProps = (storeState: IRootState) => ({
  user: storeState.userManagement.user,
  roles: storeState.userManagement.authorities,
  loading: storeState.userManagement.loading,
  currentEmployee: storeState.employee.currentEmployeeEntity
});

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyAcceptInvitationDialog);
