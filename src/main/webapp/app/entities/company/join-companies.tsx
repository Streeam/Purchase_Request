import React from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { GridComponent, Sort, ColumnDirective, ColumnsDirective, Inject, Page } from '@syncfusion/ej2-react-grids';
// tslint:disable-next-line:no-unused-variable
import { openFile, byteSize, Translate, translate, getSortState, IPaginationBaseState, JhiPagination, JhiItemCount } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './company.reducer';
import { getCurrentEmployeeEntity } from '../employee/employee.reducer';
import { NOTIFICATIONS } from '../../../app/config/constants';
// tslint:disable-next-line:no-unused-variable
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ITEMS_PER_PAGE } from 'app/shared/util/pagination.constants';
import '../../../content/css/grid.css';
import { getEntities as getNotifications } from '../notification/notification.reducer';

export interface ICompanyProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export interface ICompanyState extends IPaginationBaseState {
  search: string;
}

export class JoinCompany extends React.Component<ICompanyProps, ICompanyState> {
  state: ICompanyState = {
    search: '',
    ...getSortState(this.props.location, ITEMS_PER_PAGE)
  };

  componentDidMount() {
    this.getEntities();
    this.props.getNotifications();
    // this.props.getCurrentEmployeeEntity();
  }

  search = () => {
    if (this.state.search) {
      this.setState({ activePage: 1 }, () => {
        const { activePage, itemsPerPage, sort, order, search } = this.state;
        this.props.getSearchEntities(search, activePage - 1, itemsPerPage, `${sort},${order}`);
      });
    }
  };

  clear = () => {
    this.setState({ search: '', activePage: 1 }, () => {
      this.props.getEntities();
    });
  };

  handleSearch = event => this.setState({ search: event.target.value });

  sort = prop => () => {
    this.setState(
      {
        order: this.state.order === 'asc' ? 'desc' : 'asc',
        sort: prop
      },
      () => this.sortEntities()
    );
  };

  sortEntities() {
    this.getEntities();
    this.props.history.push(`${this.props.location.pathname}?page=${this.state.activePage}&sort=${this.state.sort},${this.state.order}`);
  }

  handlePagination = activePage => this.setState({ activePage }, () => this.sortEntities());

  getEntities = () => {
    const { activePage, itemsPerPage, sort, order, search } = this.state;
    if (search) {
      this.props.getSearchEntities(search, activePage - 1, itemsPerPage, `${sort},${order}`);
    } else {
      this.props.getEntities(activePage - 1, itemsPerPage, `${sort},${order}`);
    }
  };

  render() {
    const { companyList, match, totalItems, currentEmployee, notifications } = this.props;

    const requestToJoinPending = (companyId: Number): JSX.Element => {
      const employeeNotifications = notifications.filter(
        (value, index) => value.company === companyId && value.employeeId === currentEmployee.id
      );
      const rejectedNotification = employeeNotifications.filter(
        (value, index) => value.company === companyId && NOTIFICATIONS.REJECT_REQUEST === value.format
      );
      const pendingRequestNotification = employeeNotifications.filter(
        (value, index) => value.company === companyId && NOTIFICATIONS.REQUEST_TO_JOIN === value.format
      );

      if (rejectedNotification && rejectedNotification.length > 0) {
        return (
          <Button color="danger" size="sm">
            <FontAwesomeIcon icon="ban" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.rejected">Application Rejected</Translate>
            </span>
          </Button>
        );
      }

      if (pendingRequestNotification && pendingRequestNotification.length > 0) {
        return (
          <Button color="primary" size="sm" disabled>
            <FontAwesomeIcon icon="file-signature" />{' '}
            <span className="d-none d-md-inline">
              <Translate contentKey="entity.action.submitted">Application Submitted</Translate>
            </span>
          </Button>
        );
      }

      return (
        <Button tag={Link} to={`${this.props.match.url}/${companyId}/join`} color="primary" size="sm">
          <FontAwesomeIcon icon="file-signature" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.join">Join</Translate>
          </span>
        </Button>
      );
    };

    return (
      /*<div>
        <GridComponent
          dataSource={companyList && companyList.length > 0 ? companyList : []}
          allowSorting
          allowPaging
          pageSettings={{ pageCount: 5 }}
        >
          <ColumnsDirective>
            <ColumnDirective
              headerText="Photo"
              width="100" //src={`data:${company.companyLogoContentType};base64,${company.companyLogo}`}
              template="<img src= 'data:{{:companyLogoContentType}};base64,{{:companyLogo}}'/>"
            />
            <ColumnDirective field="name" headerText="Name" width="220" />
            <ColumnDirective field="email" headerText="Email" width="220" />
            <ColumnDirective field="city" headerText="City" width="220" />
            <ColumnDirective field="country" headerText="Country" width="220" />
          </ColumnsDirective>
          <Inject services={[Sort, Page]} />
        </GridComponent>
      </div>*/
      <div>
        <h2 id="company-heading">
          <Translate contentKey="cidApp.company.home.join">Join a Company</Translate>
        </h2>
        <Row>
          <Col sm="12">
            <AvForm onSubmit={this.search}>
              <AvGroup>
                <InputGroup>
                  <AvInput
                    type="text"
                    name="search"
                    value={this.state.search}
                    onChange={this.handleSearch}
                    placeholder={translate('cidApp.company.home.search')}
                  />
                  <Button className="input-group-addon">
                    <FontAwesomeIcon icon="search" />
                  </Button>
                  <Button type="reset" className="input-group-addon" onClick={this.clear}>
                    <FontAwesomeIcon icon="trash" />
                  </Button>
                </InputGroup>
              </AvGroup>
            </AvForm>
          </Col>
        </Row>
        <div className="table-responsive">
          {companyList && companyList.length > 0 ? (
            <Table responsive>
              <thead>
                <tr>
                  <th />
                  <th className="hand" onClick={this.sort('name')}>
                    <Translate contentKey="cidApp.company.name">Name</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('email')}>
                    <Translate contentKey="cidApp.company.email">Email</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('phone')}>
                    <Translate contentKey="cidApp.company.phone">Phone</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('addressLine1')}>
                    <Translate contentKey="cidApp.company.addressLine1">Address</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('city')}>
                    <Translate contentKey="cidApp.company.city">City</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('country')}>
                    <Translate contentKey="cidApp.company.country">Country</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th className="hand" onClick={this.sort('postcode')}>
                    <Translate contentKey="cidApp.company.postcode">Postcode</Translate> <FontAwesomeIcon icon="sort" />
                  </th>
                  <th />
                </tr>
              </thead>
              <tbody>
                {companyList.map((company, i) => (
                  <tr key={`entity-${i}`}>
                    <td>
                      {company.companyLogo ? (
                        <div>
                          <a onClick={openFile(company.companyLogoContentType, company.companyLogo)}>
                            <img
                              src={`data:${company.companyLogoContentType};base64,${company.companyLogo}`}
                              style={{ maxHeight: '30px' }}
                            />
                            &nbsp;
                          </a>
                        </div>
                      ) : (
                        <div>
                          <img src={`content/images/company-logo.png`} style={{ maxHeight: '20px' }} />
                        </div>
                      )}
                    </td>
                    <td>{company.name}</td>
                    <td>{company.email}</td>
                    <td>{company.phone}</td>
                    <td>{company.addressLine1}</td>
                    <td>{company.city}</td>
                    <td>{company.country}</td>
                    <td>{company.postcode}</td>
                    <td className="text-right">
                      <div className="btn-group flex-btn-group-container">
                        <Button tag={Link} to={`${match.url}/${company.id}`} color="info" size="sm">
                          <FontAwesomeIcon icon="eye" />{' '}
                          <span className="d-none d-md-inline">
                            <Translate contentKey="entity.action.view">View</Translate>
                          </span>
                        </Button>
                        {requestToJoinPending(company.id)}
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </Table>
          ) : (
            <div className="alert alert-warning">
              <Translate contentKey="cidApp.company.home.notFound">No Companies found</Translate>
            </div>
          )}
        </div>
        <div className={companyList && companyList.length > 0 ? '' : 'd-none'}>
          <Row className="justify-content-center">
            <JhiItemCount page={this.state.activePage} total={totalItems} itemsPerPage={this.state.itemsPerPage} i18nEnabled />
          </Row>
          <Row className="justify-content-center">
            <JhiPagination
              activePage={this.state.activePage}
              onSelect={this.handlePagination}
              maxButtons={5}
              itemsPerPage={this.state.itemsPerPage}
              totalItems={this.props.totalItems}
            />
          </Row>
        </div>
      </div>
    );
  }
}

const mapStateToProps = ({ company, employee, notification }: IRootState) => ({
  companyList: company.entities,
  totalItems: company.totalItems,
  currentEmployee: employee.currentEmployeeEntity,
  notifications: notification.entities
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
  getCurrentEmployeeEntity,
  getNotifications
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(
  mapStateToProps,
  mapDispatchToProps
)(JoinCompany);