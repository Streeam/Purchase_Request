import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col md="12">
        <p>
          <strong>Company Information Database</strong>
          {'  '}|{' '}
          <a href="https://github.com/Streeam/Purchase_Request" target="_blank">
            GitHub
          </a>
          {'  '}| An application developed by{' '}
          <a href="linkedin.com/in/bogdan-mihoci-729561188" target="_blank">
            Bogdan Mihoci
          </a>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
