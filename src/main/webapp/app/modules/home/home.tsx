import '../../app.scss';
import React from 'react';
import { NavLink as Link } from 'react-router-dom';
import { Row, Col, Button } from 'reactstrap';
import CompanyStatus from '../../entities/company/current-company-status';

export const home = props =>
  props.isUserOnly ? (
    <Row>
      <Col style={{ padding: '15% 0 0 0' }} sm="12" md={{ size: 6, offset: 3 }}>
        <Button tag={Link} to="/entity/company/join-company" className="Button" size="lg" block>
          JOIN COMPANY
        </Button>
      </Col>

      <Col style={{ padding: '10px 0 15% 0' }} sm="12" md={{ size: 6, offset: 3 }}>
        <Button tag={Link} to="/entity/company/new" className="Button" size="lg" block>
          CREATE COMPANY
        </Button>
      </Col>
    </Row>
  ) : (
    <CompanyStatus />
  );

export default home;
