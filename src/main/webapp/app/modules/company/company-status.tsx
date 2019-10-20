import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Table } from 'reactstrap';
// tslint:disable-next-line:no-unused-variable
import { Translate, ICrudGetAction, openFile } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from '../../entities/company/company.reducer';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { AUTHORITIES } from '../../config/constants';
import { hasAnyAuthority } from '../../shared/auth/private-route';
import '../../app.scss';

export interface ICompanyDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string; match: string }> {}

export const companyDetail = (props: ICompanyDetailProps) => {
  let _isMounted = false;

  useEffect(() => {
    _isMounted = true;
    props.getEntity(props.match.params.id, _isMounted);
    return () => (_isMounted = false);
  }, [props.match.params.id]);

  const lableStyle = { color: 'black' };
  const { companyEntity } = props;

  const isManager = (authorities, anyAuthority) => {
    const auth = authorities.map(authority => authority.name);
    return hasAnyAuthority(auth, anyAuthority) ? (
      <div>
        <img src={`content/images/check.png`} style={{ maxHeight: '20px' }} />
      </div>
    ) : (
      <div />
    );
  };

  const tableContent = companyEntity.employees
    ? companyEntity.employees.map((employee, i) => (
        <tr key={`entity-${i}`}>
          <td>
            {employee.image ? (
              <div>
                <img
                  src={`data:${employee.imageContentType};base64,${employee.image}`}
                  style={{
                    maxHeight: '50px',
                    borderRadius: '50%'
                  }}
                />
              </div>
            ) : (
              <div>
                <img
                  src={`content/images/default_profile_icon.png`}
                  style={{
                    maxHeight: '50px',
                    borderRadius: '50%'
                  }}
                />
              </div>
            )}
          </td>
          <td>{employee.user.firstName && employee.user.lastName ? `${employee.user.firstName}` + ` ${employee.user.lastName}` : ''}</td>
          <td>{employee.login ? employee.login : ''}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.MANAGER])}</td>
          <td>{isManager(employee.user.authorities, [AUTHORITIES.EMPLOYEE])}</td>
        </tr>
      ))
    : null;

  return (
    <div>
      <div>
        <div
          style={{
            display: 'inline-block',
            textAlign: 'left',
            width: '50%'
          }}
        >
          <div style={{ display: 'inline-block' }}>
            <h1 style={lableStyle}>{companyEntity.name}</h1>
          </div>
        </div>
        <div
          style={{
            display: 'inline-block',
            textAlign: 'right',
            width: '50%'
          }}
        >
          {companyEntity && companyEntity.companyLogo ? (
            <div>
              <a onClick={openFile(companyEntity.companyLogoContentType, companyEntity.companyLogo)}>
                <img
                  src={`data:${companyEntity.companyLogoContentType};base64,${companyEntity.companyLogo}`}
                  style={{
                    maxHeight: '70px',
                    borderRadius: '5%'
                  }}
                />
              </a>
            </div>
          ) : (
            <div>
              <img src={`content/images/company-logo.png`} style={{ maxHeight: '50px' }} />
            </div>
          )}
        </div>
      </div>
      <br />
      <h4>Company Details</h4>
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
      <h4>Company Structure</h4>
      <Table>
        <thead>
          <tr>
            <th style={{ width: '2%' }} />
            <th>Employee's Name</th>
            <th>Username</th>
            <th>Manager</th>
            <th>Employee</th>
          </tr>
        </thead>
        <tbody>{tableContent}</tbody>
      </Table>
      <br />
      <Button outline className="Button" tag={Link} to="/company/join-company" replace size="sm">
        <FontAwesomeIcon icon="arrow-left" />{' '}
        <span className="d-none d-md-inline">
          <Translate contentKey="entity.action.back">Back</Translate>
        </span>
      </Button>
      &nbsp;
    </div>
  );
};

const mapStateToProps = ({ company }: IRootState) => ({
  companyEntity: company.entity
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(companyDetail);
