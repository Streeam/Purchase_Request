import React from 'react';
import { connect } from 'react-redux';
import { RouteComponentProps } from 'react-router-dom';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { joinCompany } from '../../../entities/employee/employee.reducer';
import { getAsyncCurentEntities as getCurrentNotifications } from '../../../entities/notification/notification.reducer';

export interface ICompanyJoinDialogProps extends DispatchProps, RouteComponentProps<{ id: string }> {}

export class CompanyJoinDialog extends React.Component<ICompanyJoinDialogProps> {
  _isMounted = false;
  confirmJoin = event => {
    this.props.joinCompany(this._isMounted, this.props.match.params.id);
    this.handleClose(event);
  };
  componentDidMount() {
    this._isMounted = true;
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

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
          <Button color="secondary" outline size="sm" onClick={this.handleClose}>
            <FontAwesomeIcon icon="ban" />
            &nbsp;
            <Translate contentKey="entity.action.cancel">Cancel</Translate>
          </Button>
          <Button outline size="sm" id="jhi-confirm-join-company" color="success" onClick={this.confirmJoin}>
            <FontAwesomeIcon icon="file-signature" />
            &nbsp;
            <Translate contentKey="entity.action.join">Join</Translate>
          </Button>
        </ModalFooter>
      </Modal>
    );
  }
}

const mapDispatchToProps = { joinCompany, getCurrentNotifications };

type DispatchProps = typeof mapDispatchToProps;

export default connect(
  null,
  mapDispatchToProps
)(CompanyJoinDialog);
