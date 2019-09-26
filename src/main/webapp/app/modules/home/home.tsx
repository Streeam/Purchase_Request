import '../../app.scss';
import { getCurrentUserEntity } from '../../entities/company/company.reducer';
import React, { useState, useEffect } from 'react';
import { IRootState } from 'app/shared/reducers';
import { NavLink as Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Row, Col, Button } from 'reactstrap';
import CompanyStatus from '../../entities/company/company-detail';

export const Home = props => {
  useEffect(() => {
    props.getCurrentUserEntity(props.email);
  }, []);

  return props.isUserOnly ? (
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
  ) : (
    <CompanyStatus {...props} />
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  companyEntity: storeState.company.employeeEntity
});

const mapDispatchToProps = {
  getCurrentUserEntity
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Home);
