import '../../app.scss';
import { getCurrentUsersCompanyAsync as getCurrentUserEntity } from '../../entities/company/company.reducer';
import React, { useState, useEffect } from 'react';
import { IRootState } from 'app/shared/reducers';
import { NavLink as Link } from 'react-router-dom';
import { connect } from 'react-redux';
import { Row, Col, Button } from 'reactstrap';
import CompanyStatus from '../../entities/company/current-company-status';
import { getCurrentEmployeeEntity } from '../../entities/employee/employee.reducer';
import { getSession } from 'app/shared/reducers/authentication';
import { getProfile } from 'app/shared/reducers/application-profile';
import { getEntities as getNotifications } from '../../entities/notification/notification.reducer';

export const Home = props => {
  useEffect(() => {
    props.getCurrentUserEntity();
    props.getCurrentEmployeeEntity();
    props.getSession();
    props.getProfile();
    props.getNotifications();
  }, []);
  return props.isUserOnly ? (
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
    <CompanyStatus {...props} />
  );
};

const mapStateToProps = ({ company, employee }: IRootState) => ({
  companyEntity: company.employeeEntity,
  currentEmployee: employee.currentEmployeeEntity
});

const mapDispatchToProps = {
  getCurrentUserEntity,
  getCurrentEmployeeEntity,
  getSession,
  getProfile,
  getNotifications
};

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(Home);
