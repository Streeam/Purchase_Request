import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Spinner } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { rejectCompanyInvitation } from '../../employee/employee.reducer';
import { IRootState } from 'app/shared/reducers';

export interface ICompanyAcceptInvitationDialogProps extends DispatchProps, RouteComponentProps<{ id: string }> {}

export const companyAcceptInvitationDialog = props => {
  const rejectInvitation = event => {
    props.rejectCompanyInvitation(props.match.params.id);
    handleClose(event);
  };

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.rejectinvite.title">Reject the inviation</Translate>
      </ModalHeader>
      {props.updating ? (
        <Spinner color="primary" />
      ) : (
        <ModalBody id="cidApp.company.acceptinvitation.question">
          <Translate contentKey="cidApp.company.rejectinvitation.question">
            Are you sure you want to accept the companny's invitation?
          </Translate>
        </ModalBody>
      )}
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-acceptinvitation-company" color="danger" onClick={rejectInvitation}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.rejecteinvitation">Reject</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapDispatchToProps = { rejectCompanyInvitation };

const mapStateToProps = (storeState: IRootState) => ({
  updating: storeState.employee.updating
});

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyAcceptInvitationDialog);
