import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { inviteEmployee } from '../../../entities/employee/employee.reducer';

export interface ICompanyInviteDialogProps extends DispatchProps, RouteComponentProps<{ email: string; companyId: string }> {}

const CompanyInviteDialog = (props: ICompanyInviteDialogProps) => {
  let _isMounted = false;

  const { email, companyId } = props.match.params;

  useEffect(() => {
    _isMounted = true;
    return () => (_isMounted = false);
  }, []);

  const handleInvite = event => {
    if (props.match.params.email) {
      props.inviteEmployee(_isMounted, email, companyId);
      handleClose(event);
    }
  };

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.invite.title">Confirm invite operation</Translate>
      </ModalHeader>
      <ModalBody id="cidApp.company.invite.question">
        <Translate contentKey="cidApp.company.invite.question" interpolate={{ name: props.match.params.email }}>
          Are you sure you want to invite a new employee?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button outline size="sm" color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button outline size="sm" id="jhi-confirm-invite-company" color="success" onClick={handleInvite}>
          <FontAwesomeIcon icon="user" />
          &nbsp;
          <Translate contentKey="entity.action.invite">Invite</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapDispatchToProps = { inviteEmployee };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(CompanyInviteDialog);
