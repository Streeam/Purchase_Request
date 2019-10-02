import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate, ICrudGetAction, ICrudDeleteAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { ICompany } from 'app/shared/model/company.model';
import { IRootState } from 'app/shared/reducers';
import { getEntity } from './company.reducer';
import { joinCompany, getCurrentEmployeeEntity } from '../employee/employee.reducer';
import { getEntities as getNotifications } from '../notification/notification.reducer';

export interface ICompanyDeleteDialogProps extends DispatchProps, RouteComponentProps<{ id: string }> {}

export class CompanyDeleteDialog extends React.Component<ICompanyDeleteDialogProps> {
  confirmJoin = event => {
    this.props.joinCompany(this.props.match.params.id);
    this.handleClose(event);
  };

  handleClose = event => {
    event.stopPropagation();
    this.props.history.goBack();
  };

  render() {
    return (
      <Modal isOpen toggle={this.handleClose}>
        <ModalHeader toggle={this.handleClose}>
          <Translate contentKey="entity.join.title">Confirm join operation</Translate>
        </ModalHeader>
        <ModalBody id="cidApp.company.join.question">
          <Translate contentKey="cidApp.company.join.question" interpolate={{ name: this.props.match.params.id }}>
            Are you sure you want to join this Company?
          </Translate>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={this.handleClose}>
            <FontAwesomeIcon icon="ban" />
            &nbsp;
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button id="jhi-confirm-join-company" color="primary" onClick={this.confirmJoin}>
            <FontAwesomeIcon icon="file-signature" />
            &nbsp;
            <Translate contentKey="entity.action.join">Join</Translate>
          </Button>
        </ModalFooter>
      </Modal>
    );
  }
}

const mapDispatchToProps = { joinCompany, getNotifications, getCurrentEmployeeEntity };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(CompanyDeleteDialog);
