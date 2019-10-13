import React, { useState, useEffect } from 'react';
import { Modal, ModalHeader, ModalBody, ModalFooter, Button, Row, Col } from 'reactstrap';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import Avatar from 'react-avatar-edit';
import { IRootState } from 'app/shared/reducers';
import { connect } from 'react-redux';
import { updateEntity, reset, getCurrentUsersCompanyAsync as getCurrentCompany } from '../../../entities/company/company.reducer';
import { RouteComponentProps } from 'react-router-dom';

export interface ILogoUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{}> {}

export const companyLogoModal = (props: ILogoUpdateProps) => {
  let _isMounted = false;

  const { currentCompany } = props;

  const [src, setSrc] = useState('');
  const [preview, setPreview] = useState(null);

  useEffect(() => {
    _isMounted = true;
    props.getCurrentCompany(_isMounted);
    return () => (_isMounted = false);
  }, []);

  const onClose = () => {
    setPreview(null);
  };
  const onCrop = previewSrc => {
    setPreview(previewSrc);
  };

  const handleAccept = event => {
    const updatedCompany = { ...currentCompany };
    const image = preview.slice(22);
    const imageContentType = preview.slice(5, 14);
    updatedCompany.companyLogo = image;
    updatedCompany.companyLogoContentType = imageContentType;
    // console.log(updatedCompany);
    props.updateEntity(_isMounted, updatedCompany);
    handleClose();
  };

  const handleClose = () => {
    props.history.goBack();
  };

  return (
    <Modal size={'lg'} isOpen scrollable={'true'} toggle={handleClose}>
      <ModalHeader toggle={handleClose}>Update Company Logo</ModalHeader>
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
        <Button id="confirm-logo-update" color="success" onClick={handleAccept} disabled={preview === null}>
          <FontAwesomeIcon icon="check" />
          &nbsp; Ok
        </Button>
      </ModalFooter>
    </Modal>
  );
};

const mapStateToProps = ({ company }: IRootState) => ({
  currentCompany: company.employeeEntity
});

const mapDispatchToProps = {
  updateEntity,
  reset,
  getCurrentCompany
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyLogoModal);
