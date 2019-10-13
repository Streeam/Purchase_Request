import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Spinner } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { rejectEmployee } from '../../../entities/company/company.reducer';

export interface ICompanyRejectRequestDialogProps extends DispatchProps, RouteComponentProps<{ companyId: string; employeeId: string }> {}

export const companyAcceptRequestDialog = (props: ICompanyRejectRequestDialogProps) => {
  let _isMounted = false;
  const { companyId, employeeId } = props.match.params;
  useEffect(() => {
    _isMounted = true;
    return () => (_isMounted = false);
  }, []);

  const handleReject = event => {
    if (employeeId && companyId) {
      props.rejectEmployee(_isMounted, companyId, employeeId);
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
        <Translate contentKey="entity.rejectrequest.title">Confirm the inviation</Translate>
      </ModalHeader>
      <ModalBody id="cidApp.company.rejectrequest.question">
        <Translate contentKey="cidApp.company.rejectrequest.question">
          Are you sure you want to reject the employee's application?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-rejectrequest-company" color="danger" onClick={handleReject}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.reject">Reject</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapDispatchToProps = { rejectEmployee };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(companyAcceptRequestDialog);
