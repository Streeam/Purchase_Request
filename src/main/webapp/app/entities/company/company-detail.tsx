import React from 'react';
import { Link } from 'react-router-dom';
import { Button, Row, Col, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import '../../app.scss';
import TabBar from 'app/entities/company/company-tabpane';

export const companyDetail = props => {
  const lableStyle = { color: 'black' };
  const { companyEntity } = props;
  return (
    <div>
      <Row>
        <Col sm="2">
          {companyEntity.companyLogo ? (
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
          <h1 style={lableStyle}>{companyEntity.name}</h1>
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
            <th />
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
            <td>
              <Button className="Button" tag={Link} to={`/entity/company/${companyEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.edit">Edit</Translate>
                </span>
              </Button>
            </td>
          </tr>
        </tbody>
      </Table>
      <br />
      <br />
      <TabBar {...props} />
      <br />
      <Button className="Button" tag={Link} to="/entity/company" replace color="info">
        <FontAwesomeIcon icon="arrow-left" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.back">Back</Translate>
        </span>
      </Button>
      &nbsp;
    </div>
  );
};

export default companyDetail;
