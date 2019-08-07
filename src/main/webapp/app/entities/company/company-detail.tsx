import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './company.reducer';
import { ICompany } from 'app/shared/model/company.model';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICompanyDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export class CompanyDetail extends React.Component<ICompanyDetailProps> {
  componentDidMount() {
    this.props.getEntity(this.props.match.params.id);
  }

  render() {
    const { companyEntity } = this.props;
    return (
      <Row>
        <Col md="8">
          <h2>
            <Translate contentKey="cidApp.company.detail.title">Company</Translate> [<b>{companyEntity.id}</b>]
          </h2>
          <dl className="jh-entity-details">
            <dt>
              <span id="name">
                <Translate contentKey="cidApp.company.name">Name</Translate>
              </span>
            </dt>
            <dd>{companyEntity.name}</dd>
            <dt>
              <span id="email">
                <Translate contentKey="cidApp.company.email">Email</Translate>
              </span>
            </dt>
            <dd>{companyEntity.email}</dd>
            <dt>
              <span id="phone">
                <Translate contentKey="cidApp.company.phone">Phone</Translate>
              </span>
            </dt>
            <dd>{companyEntity.phone}</dd>
            <dt>
              <span id="addressLine1">
                <Translate contentKey="cidApp.company.addressLine1">Address Line 1</Translate>
              </span>
            </dt>
            <dd>{companyEntity.addressLine1}</dd>
            <dt>
              <span id="addressLine2">
                <Translate contentKey="cidApp.company.addressLine2">Address Line 2</Translate>
              </span>
            </dt>
            <dd>{companyEntity.addressLine2}</dd>
            <dt>
              <span id="city">
                <Translate contentKey="cidApp.company.city">City</Translate>
              </span>
            </dt>
            <dd>{companyEntity.city}</dd>
            <dt>
              <span id="country">
                <Translate contentKey="cidApp.company.country">Country</Translate>
              </span>
            </dt>
            <dd>{companyEntity.country}</dd>
            <dt>
              <span id="postcode">
                <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
              </span>
            </dt>
            <dd>{companyEntity.postcode}</dd>
            <dt>
              <span id="companyLogo">
                <Translate contentKey="cidApp.company.companyLogo">Company Logo</Translate>
              </span>
            </dt>
            <dd>
              {companyEntity.companyLogo ? (
                <div>
                  <a onClick={openFile(companyEntity.companyLogoContentType, companyEntity.companyLogo)}>
                    <img
                      src={`data:${companyEntity.companyLogoContentType};base64,${companyEntity.companyLogo}`}
                      style={{ maxHeight: '30px' }}
                    />
                  </a>
                  <span>
                    {companyEntity.companyLogoContentType}, {byteSize(companyEntity.companyLogo)}
                  </span>
                </div>
              ) : null}
            </dd>
          </dl>
          <Button tag={Link} to="/entity/company" replace color="info">
            <FontAwesomeIcon icon="arrow-left" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.back">Back</Translate>
            </span>
          </Button>
          &nbsp;
          <Button tag={Link} to={`/entity/company/${companyEntity.id}/edit`} replace color="primary">
            <FontAwesomeIcon icon="pencil-alt" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.edit">Edit</Translate>
            </span>
          </Button>
        </Col>
      </Row>
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
