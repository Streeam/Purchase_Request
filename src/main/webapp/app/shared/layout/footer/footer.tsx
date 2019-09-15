import './footer.scss';

import React from 'react';
import { Translate } from 'react-jhipster';
import { Col, Row } from 'reactstrap';

const Footer = props => (
  <div className="footer page-content">
    <Row>
      <Col md="8">
        <p>
          <a href="https://github.com/Streeam/Purchase_Request" target="_blank" rel="noopener noreferrer">
            https://github.com/Streeam/Purchase_Request
          </a>
        </p>
      </Col>
    </Row>
  </div>
);

export default Footer;
