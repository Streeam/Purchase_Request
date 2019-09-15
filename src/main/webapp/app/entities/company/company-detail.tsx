import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { TabContent, TabPane, Nav, NavItem, NavLink, Card, Button, CardTitle, CardText, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './company.reducer';
import { ICompany } from 'app/shared/model/company.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import '../../app.scss';
import TabBar from 'app/entities/company/company-tabpane';

export interface ICompanyDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CompanyDetail extends React.Component<ICompanyDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { companyEntity } = this.props;
    const lableStyle = { color: 'black' };
    const fieldStyle = { color: '#666666' };
    return (
      <div>
        <Row>
          <Col sm="1">
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
            ) : null}
          </Col>
          <Col md="8">
            <h1 style={lableStyle}>{companyEntity.name}</h1>
          </Col>
        </Row>
        <br />
        <Row>
          <Col>
            <span style={lableStyle} id="email">
              <Translate contentKey="cidApp.company.email">Email</Translate>
            </span>
          </Col>
          <Col>
            <span style={lableStyle} id="phone">
              <Translate contentKey="cidApp.company.phone">Phone</Translate>
            </span>
          </Col>
          <Col>
            <span style={lableStyle} id="addressLine1">
              <Translate contentKey="cidApp.company.addressLine1">Address</Translate>
            </span>
          </Col>
          <Col>
            <span style={lableStyle} id="city">
              <Translate contentKey="cidApp.company.city">City</Translate>
            </span>
          </Col>
          <Col>
            <span style={lableStyle} id="country">
              <Translate contentKey="cidApp.company.country">Country</Translate>
            </span>
          </Col>
          <Col>
            <span style={lableStyle} id="postcode">
              <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
            </span>
          </Col>
        </Row>
        <Row>
          <Col>
            <span style={fieldStyle}>{companyEntity.email}</span>
          </Col>
          <Col>
            <span style={fieldStyle}>{companyEntity.phone}</span>
          </Col>
          <Col>
            <span style={fieldStyle}>{companyEntity.addressLine1}</span>
          </Col>
          <Col>
            <span style={fieldStyle}>{companyEntity.city}</span>
          </Col>
          <Col>
            <span style={fieldStyle}>{companyEntity.country}</span>
          </Col>
          <Col>
            <span style={fieldStyle}>{companyEntity.postcode}</span>
          </Col>
        </Row>
        <br />
        <Row>
          <Col>
            <div className="float-right">
              <Button className="Button" tag={Link} to="/entity/company" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button className="Button" tag={Link} to={`/entity/company/${companyEntity.id}/edit`} replace color="primary">
                <FontAwesomeIcon icon="pencil-alt" />{' '}
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.edit">Edit</Translate>
                </span>
              </Button>
            </div>
          </Col>
        </Row>
        <TabBar {...this.props} />
      </div>
    );
  }
}

const mapStateToProps = ({ company }: IRootState) => ({
  companyEntity: company.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CompanyDetail);
