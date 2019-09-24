import '../../app.scss';

import React from 'react';
import { NavLink as Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Row, Col, Button } from 'reactstrap';

export const Home = () => (
  <Row>
    <Col style={{ padding: '15% 0 0 0' }} sm="12" md={{ size: 6, offset: 3 }}>
      <Button className="Button" size="lg" block>
        JOIN COMPANY
      </Button>
    </Col>

    <Col style={{ padding: '10px 0 15% 0' }} sm="12" md={{ size: 6, offset: 3 }}>
      <Button tag={Link} to="/entity/company/new" className="Button" size="lg" block>
        CREATE COMPANY
      </Button>
    </Col>
  </Row>
);

export default Home;
