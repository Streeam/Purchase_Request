import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { fireEmployee, getCurrentUsersCompanyAsync } from '../../../entities/company/company.reducer';
import { RouteComponentProps } from 'react-router';

export interface ICompanyProps extends StateProps, DispatchProps, RouteComponentProps<{ companyId: string; employeeId: string }> {}

export const employeeFireDialog = (props: ICompanyProps) => {
  let _isMounted = false;
  const { companyId, employeeId } = props.match.params;

  useEffect(() => {
    _isMounted = true;
    return () => (_isMounted = false);
  }, []);

  const confirmFire = event => {
    props.fireEmployee(_isMounted, companyId, employeeId);
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
        <Translate contentKey="cidApp.company.fire.question" interpolate={{ name: employeeId }}>
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

type DispatchProps = typeof mapDispatchToProps;
type StateProps = ReturnType<typeof mapStateToProps>;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(employeeFireDialog);
