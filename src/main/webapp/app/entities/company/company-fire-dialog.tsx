import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate, ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { fireEmployee, getCurrentUsersCompanyAsync } from './company.reducer';
import { getCurrentEmployeeAsync as getCurrentEmployeeEntity } from '../employee/employee.reducer';

export const employeeFireDialog = props => {
  const { companyEntity } = props;

  useEffect(() => {
    props.getCurrentEmployeeEntity();
    props.getCurrentUsersCompanyAsync();
  }, []);

  const confirmFire = event => {
    props.fireEmployee(companyEntity.id, props.match.params.id);
    props.getCurrentUsersCompanyAsync();
    handleClose(event);
  };

  const handleClose = event => {
    event.stopPropagation();
    props.history.goBack();
  };

  return (
    <Modal isOpen toggle={handleClose}>
      <ModalHeader toggle={handleClose}>
        <Translate contentKey="entity.fire.title">Confirm firing employee</Translate>
      </ModalHeader>
      <ModalBody id="cidApp.company.join.question">
        <Translate contentKey="cidApp.company.fire.question" interpolate={{ name: props.match.params.id }}>
          Are you sure you want to fire the employee?
        </Translate>
      </ModalBody>
      <ModalFooter>
        <Button color="secondary" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.cancel">Cancel</Translate>
        </Button>
        <Button id="jhi-confirm-join-company" color="danger" onClick={confirmFire}>
          <FontAwesomeIcon icon="ban" />
          &nbsp;
          <Translate contentKey="entity.action.fire">Fire</Translate>
        </Button>
      </ModalFooter>
    </Modal>
  );
};
const mapStateToProps = ({ company }: IRootState) => ({
  companyEntity: company.employeeEntity
});

const mapDispatchToProps = { fireEmployee, getCurrentUsersCompanyAsync };

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(employeeFireDialog);
