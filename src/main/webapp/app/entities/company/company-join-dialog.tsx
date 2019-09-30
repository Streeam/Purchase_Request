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

export interface ICompanyDeleteDialogProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CompanyDeleteDialog extends React.Component<ICompanyDeleteDialogProps> {
  componentDidMount() {
    // this.props.getEntity(this.props.match.params.id);
  }

  confirmJoin = event => {
    this.props.joinCompany(this.props.companyEntity.id);
    // this.props.getEntity(this.props.match.params.id);
    // this.props.getNotifications();
    this.handleClose(event);
  };

  handleClose = event => {
    event.stopPropagation();
    this.props.history.goBack();
  };

  render() {
    const { companyEntity } = this.props;
    return (
      <Modal isOpen toggle={this.handleClose}>
        <ModalHeader toggle={this.handleClose}>
          <Translate contentKey="entity.join.title">Confirm join operation</Translate>
        </ModalHeader>
        <ModalBody id="cidApp.company.join.question">
          <Translate contentKey="cidApp.company.join.question" interpolate={{ name: companyEntity.name }}>
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

const mapStateToProps = ({ company }: IRootState) => ({
  companyEntity: company.entity
});

const mapDispatchToProps = { getEntity, joinCompany, getNotifications, getCurrentEmployeeEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CompanyDeleteDialog);
