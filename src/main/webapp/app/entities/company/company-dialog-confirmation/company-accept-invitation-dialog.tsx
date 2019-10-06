import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Spinner } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { acceptCompanyInvitation } from '../../employee/employee.reducer';
import { getUser, getRoles, updateUser, reset as userReset } from '../../../modules/administration/user-management/user-management.reducer';
import { IRootState } from 'app/shared/reducers';
import { AUTHORITIES } from 'app/config/constants';

export interface ICompanyAcceptInvitationDialogProps extends DispatchProps, RouteComponentProps<{ id: string }> {}

export const companyAcceptInvitationDialog = props => {
  useEffect(() => {
    props.getUser(props.currentEmployee.user.login);
  }, []);

  const confirmAccept = event => {
    props.acceptCompanyInvitation(props.match.params.id);
    const updatedUser = { ...props.user };
    const authorities = [...updatedUser.authorities];
    authorities.push(AUTHORITIES.EMPLOYEE);
    updatedUser.authorities = authorities;
    props.updateUser(updatedUser);
    event.stopPropagation();
    props.history.push('/');
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
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-acceptinvitation-company" color="primary" onClick={confirmAccept}>
          <FontAwesomeIcon icon="check" />
          &nbsp;
          <Translate contentKey="entity.action.acceptinvitation">Join</Translate>
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
