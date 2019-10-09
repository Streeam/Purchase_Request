import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate, ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IEmployee } from 'app/shared/model/employee.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity, deleteEntity } from './employee.reducer';

export interface IEmployeeDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const employeeDeleteDialog = (props: IEmployeeDeleteDialogProps) => {
  let _isMounted = false;
  const { employeeEntity } = props;

  useEffect(() => {
    _isMounted = true;
    props.getEntity(_isMounted, props.match.params.id);
    return () => (_isMounted = false);
  }, []);

  const confirmDelete = event => {
    props.deleteEntity(_isMounted, employeeEntity.id);
    handleClose(event);
  };

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.delete.title">Confirm delete operation</Translate>
      </ModalHeader>
      <ModalBody id="cidApp.employee.delete.question">
        <Translate contentKey="cidApp.employee.delete.question" interpolate={{ id: employeeEntity.id }}>
          Are you sure you want to delete Employee?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-delete-employee" color="danger" onClick={confirmDelete}>
          <FontAwesomeIcon icon="trash" />
          &nbsp;
          <Translate contentKey="entity.action.delete">Delete</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeEntity: employee.entity
});

const mapDispatchToProps = { getEntity, deleteEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(employeeDeleteDialog);
