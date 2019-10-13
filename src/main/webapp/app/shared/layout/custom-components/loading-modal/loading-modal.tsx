import React from 'react';
import './loading-modal.css';

import { Modal } from 'reactstrap';

const loadingModal = () => (
  <Modal isOpen fade={false} centered size={'sm'} contentClassName="loading-modal">
    Loading...
  </Modal>
);

export default loadingModal;
