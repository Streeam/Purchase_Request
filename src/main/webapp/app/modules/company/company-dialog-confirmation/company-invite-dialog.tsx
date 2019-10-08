import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { inviteEmployee } from '../../../entities/employee/employee.reducer';

export interface ICompanyInviteDialogProps extends DispatchProps, RouteComponentProps<{ email: string }> {}

export class CompanyInviteDialog extends React.Component<ICompanyInviteDialogProps> {
  handleInvite = event => {
    if (this.props.match.params.email) {
      this.props.inviteEmployee(this.props.match.params.email);
      this.handleClose(event);
    }
  };

  handleClose = event => {
    event.stopPropagation();
    this.props.history.goBack();
  };

  render() {
    return (
      <Modal isOpen toggle={this.handleClose}>
        <ModalHeader toggle={this.handleClose}>
          <Translate contentKey="entity.invite.title">Confirm invite operation</Translate>
        </ModalHeader>
        <ModalBody id="cidApp.company.invite.question">
          <Translate contentKey="cidApp.company.invite.question" interpolate={{ name: this.props.match.params.email }}>
            Are you sure you want to invite a new employee?
          </Translate>
        </ModalBody>
        <ModalFooter>
          <Button color="secondary" onClick={this.handleClose}>
            <FontAwesomeIcon icon="ban" />
            &nbsp;
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button id="jhi-confirm-invite-company" color="primary" onClick={this.handleInvite}>
            <FontAwesomeIcon icon="user" />
            &nbsp;
            <Translate contentKey="entity.action.invite">Invite</Translate>
          </Button>
        </ModalFooter>
      </Modal>
    );
  }
}

const mapDispatchToProps = { inviteEmployee };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(CompanyInviteDialog);
