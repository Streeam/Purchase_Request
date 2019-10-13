import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Row, Col } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Avatar from 'react-avatar-edit';
import { IRootState } from 'app/shared/reducers';
import { connect } from 'react-redux';
import { updateEntity, reset } from '../../../entities/employee/employee.reducer';
import { RouteComponentProps } from 'react-router-dom';

export interface IEmployeeUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

export const avatarModal = (props: IEmployeeUpdateProps) => {
  let _isMounted = false;

  const { employeeEntity } = props;

  const [src, setSrc] = useState('content/images/default_profile_icon.png');
  const [preview, setPreview] = useState(null);

  useEffect(() => {
    _isMounted = true;
    return () => (_isMounted = false);
  }, []);

  const onClose = () => {
    setPreview(null);
  };
  const onCrop = previewSrc => {
    setPreview(previewSrc);
  };

  const handleAccept = event => {
    const updatedEmployee = { ...employeeEntity };
    const image = preview.slice(22);
    const imageContentType = preview.slice(5, 14);
    updatedEmployee.image = image;
    updatedEmployee.imageContentType = imageContentType;
    props.updateEntity(true, updatedEmployee);
    handleClose();
  };

  const handleClose = () => {
    props.history.goBack();
  };

  return (
    <Modal size={'lg'} isOpen scrollable={'true'} toggle={handleClose}>
      <ModalHeader toggle={handleClose}>Update User's Profile Image</ModalHeader>
      <ModalBody id="cidApp.company.join.question">
        <Row>
          <Col md="6">
            <Avatar width={350} height={295} onCrop={onCrop} onClose={onClose} src={src} />
          </Col>
          <Col md="4">{preview ? <img src={preview} alt="Preview" /> : null}</Col>
        </Row>
      </ModalBody>
      <ModalFooter>
        <Button color="danger" onClick={handleClose}>
          <FontAwesomeIcon icon="ban" />
          &nbsp; Cancel
        </Button>
        <Button id="confirm-profile-update" color="success" onClick={handleAccept} disabled={preview === null}>
          <FontAwesomeIcon icon="check" />
          &nbsp; Ok
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({ employee }: IRootState) => ({
  employeeEntity: employee.currentEmployeeEntity
});

const mapDispatchToProps = {
  updateEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(avatarModal);
