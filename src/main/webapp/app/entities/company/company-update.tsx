import React, { Fragment } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
// tslint:disable-next-line:no-unused-variable
import { Translate, translate, ICrudGetAction, ICrudGetAllAction, setFileData, openFile, byteSize, ICrudPutAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { getEntity, updateEntity, createEntity, setBlob, reset } from './company.reducer';
import LoadingModal from '../../shared/layout/custom-components/loading-modal/loading-modal';

export interface ICompanyUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export interface ICompanyUpdateState {
  isNew: boolean;
}

export class CompanyUpdate extends React.Component<ICompanyUpdateProps, ICompanyUpdateState> {
  _isMounted = false;
  constructor(props) {
    super(props);
    this.state = {
      isNew: !this.props.match.params || !this.props.match.params.id
    };
  }

  componentWillUpdate(nextProps, nextState) {
    if (nextProps.updateSuccess !== this.props.updateSuccess && nextProps.updateSuccess) {
      this.handleClose();
    }
  }

  componentDidMount() {
    this._isMounted = true;
    if (this.state.isNew) {
      this.props.reset();
    } else {
      this.props.getEntity(this.props.match.params.id, this._isMounted);
    }
  }

  componentWillUnmount() {
    this._isMounted = false;
  }

  onBlobChange = (isAnImage, name) => event => {
    setFileData(event, (contentType, data) => this.props.setBlob(name, data, contentType), isAnImage);
  };

  clearBlob = name => () => {
    this.props.setBlob(name, undefined, undefined);
  };

  saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const { companyEntity } = this.props;
      const entity = {
        ...companyEntity,
        ...values
      };
      if (this.state.isNew) {
        this.props.createEntity(this._isMounted, entity);
      } else {
        this.props.updateEntity(this._isMounted, entity);
      }
    }
  };

  handleClose = () => {
    this.props.history.push('/company/company-status');
  };

  render() {
    const { companyEntity, loading, updating } = this.props;
    const { isNew } = this.state;
    const redirect = isNew ? '/' : '/company/company-status';
    const { companyLogo, companyLogoContentType } = companyEntity;
    return loading || updating ? (
      <LoadingModal />
    ) : (
      <Fragment>
        <Row className="justify-content-center">
          <Col md="8">
            {isNew ? (
              <h2 id="cidApp.company.home.createOrEditLabel">Create a New Company</h2>
            ) : (
              <h2 id="cidApp.company.home.createOrEditLabel">Edit Company</h2>
            )}
          </Col>
        </Row>
        <Row className="justify-content-center">
          <Col md="8">
            <AvForm model={isNew ? {} : companyEntity} onSubmit={this.saveEntity}>
              <AvGroup>
                <Label id="nameLabel" for="company-name">
                  <Translate contentKey="cidApp.company.name">Name</Translate>
                </Label>
                <AvField
                  id="company-name"
                  type="text"
                  name="name"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="emailLabel" for="company-email">
                  <Translate contentKey="cidApp.company.email">Email</Translate>
                </Label>
                <AvField
                  id="company-email"
                  type="text"
                  name="email"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') },
                    minLength: { value: 5, errorMessage: translate('entity.validation.minlength', { min: 5 }) },
                    maxLength: { value: 254, errorMessage: translate('entity.validation.maxlength', { max: 254 }) },
                    pattern: {
                      value: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$',
                      errorMessage: translate('entity.validation.pattern', { pattern: '^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$' })
                    }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="phoneLabel" for="company-phone">
                  <Translate contentKey="cidApp.company.phone">Phone</Translate>
                </Label>
                <AvField
                  id="company-phone"
                  type="text"
                  name="phone"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="addressLine1Label" for="company-addressLine1">
                  <Translate contentKey="cidApp.company.addressLine1">Address Line 1</Translate>
                </Label>
                <AvField
                  id="company-addressLine1"
                  type="text"
                  name="addressLine1"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="addressLine2Label" for="company-addressLine2">
                  <Translate contentKey="cidApp.company.addressLine2">Address Line 2</Translate>
                </Label>
                <AvField id="company-addressLine2" type="text" name="addressLine2" />
              </AvGroup>
              <AvGroup>
                <Label id="cityLabel" for="company-city">
                  <Translate contentKey="cidApp.company.city">City</Translate>
                </Label>
                <AvField
                  id="company-city"
                  type="text"
                  name="city"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="countryLabel" for="company-country">
                  <Translate contentKey="cidApp.company.country">Country</Translate>
                </Label>
                <AvField
                  id="company-country"
                  type="text"
                  name="country"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <Label id="postcodeLabel" for="company-postcode">
                  <Translate contentKey="cidApp.company.postcode">Postcode</Translate>
                </Label>
                <AvField
                  id="company-postcode"
                  type="text"
                  name="postcode"
                  validate={{
                    required: { value: true, errorMessage: translate('entity.validation.required') }
                  }}
                />
              </AvGroup>
              <AvGroup>
                <AvGroup>
                  <Label id="companyLogoLabel" for="companyLogo">
                    <Translate contentKey="cidApp.company.companyLogo">Company Logo</Translate>
                  </Label>
                  <br />
                  {companyLogo ? (
                    <div>
                      <a onClick={openFile(companyLogoContentType, companyLogo)}>
                        <img src={`data:${companyLogoContentType};base64,${companyLogo}`} style={{ maxHeight: '50px' }} />
                      </a>
                      <br />
                      <Row>
                        <Col md="11">
                          <span>
                            {companyLogoContentType}, {byteSize(companyLogo)}
                          </span>
                        </Col>
                        <Col md="1">
                          <Button onClick={this.clearBlob('companyLogo')}>
                            <FontAwesomeIcon icon="times-circle" />
                          </Button>
                        </Col>
                      </Row>
                    </div>
                  ) : null}
                  <input id="file_companyLogo" type="file" onChange={this.onBlobChange(true, 'companyLogo')} accept="image/*" />
                  <AvInput type="hidden" name="companyLogo" value={companyLogo ? companyLogo : ''} />
                </AvGroup>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to={redirect} replace>
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button id="save-entity" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          </Col>
        </Row>
      </Fragment>
    );
  }
}

const mapStateToProps = (storeState: IRootState) => ({
  companyEntity: storeState.company.entity,
  loading: storeState.company.loading,
  updating: storeState.company.updating,
  updateSuccess: storeState.company.updateSuccess
});

const mapDispatchToProps = {
  getEntity,
  updateEntity,
  setBlob,
  createEntity,
  reset
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(CompanyUpdate);
