import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link } from 'react-router-dom';
import { Button, Row, Col, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';
import { getCurrentUsersCompanyAsync } from './company.reducer';
import '../../app.scss';
import TabBar from './company-tabpane';
import { getSession } from '../../shared/reducers/authentication';
import { getCurrentEmployeeAsync } from '../employee/employee.reducer';
import CompanyEmployeeTab from './company-employee-tab';

export const companyDetail = props => {
  const lableStyle = { color: 'black' };
  const { companyEntity, isCurrentUserManager } = props;

  useEffect(() => {
    props.getCurrentUsersCompanyAsync();
    props.getCurrentEmployeeAsync();
  }, []);

  return (
    <div>
      <Row>
        <Col sm="2">
          {companyEntity && companyEntity.companyLogo ? (
            <div>
              <a onClick={openFile(companyEntity.companyLogoContentType, companyEntity.companyLogo)}>
                <img
                  src={`data:${companyEntity.companyLogoContentType};base64,${companyEntity.companyLogo}`}
                  style={{
                    maxHeight: '50px',
                    borderRadius: '50%'
                  }}
                />
              </a>
            </div>
          ) : (
            <div>
              <img src={`content/images/company-logo.png`} style={{ maxHeight: '50px' }} />
            </div>
          )}
        </Col>
        <Col md="8">
          <div style={{ display: 'inline-block' }}>
            <h1 style={lableStyle}>{companyEntity.name}</h1>
          </div>
          <div style={{ display: 'inline-block' }}>
            {isCurrentUserManager ? (
              <Button tag={Link} to={`/entity/company/${companyEntity.id}/edit`} replace color="link" title="Edit Company">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
              </Button>
            ) : (
              <div />
            )}
          </div>
        </Col>
      </Row>
      <br />
      <Table>
        <thead>
          <tr>
            <th>
              <Translate contentKey="cidApp.company.email">Email</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.phone">Phone</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.addressLine1">Address</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.city">City</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.country">Country</Translate>
            </th>
            <th>
              <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr>
            <td>{companyEntity.email}</td>
            <td>{companyEntity.phone}</td>
            <td>{companyEntity.addressLine1}</td>
            <td>{companyEntity.city}</td>
            <td>{companyEntity.country}</td>
            <td>{companyEntity.postcode}</td>
          </tr>
        </tbody>
      </Table>
      <br />
      <br />
      {isCurrentUserManager ? <TabBar {...props} /> : <CompanyEmployeeTab {...props} />}
      <br />
    </div>
  );
};

const mapStateToProps = ({ company, employee }: IRootState) => ({
  companyEntity: company.employeeEntity,
  curentEmployee: employee.currentEmployeeEntity
});

const mapDispatchToProps = { getCurrentUsersCompanyAsync, getSession, getCurrentEmployeeAsync };

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyDetail);
