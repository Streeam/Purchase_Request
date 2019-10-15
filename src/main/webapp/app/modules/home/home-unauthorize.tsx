import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const homePage = props =>
  props.isCurrentUserManager ? (
    <div>
      <br />
      <p>After you create a company you become the manager and you are assigned the manager role. As a manager you have three options:</p>
      <br />
      <ul>
        <li>
          <p className="font-weight-bold">
            <Link to="/company/company-status" className="alert-link">
              <span>My Company</span>
            </Link>
          </p>
        </li>
        <li>
          <p className="font-weight-bold">
            <Link to="/company/applicants" className="alert-link">
              <span>Applicants</span>
            </Link>
          </p>
        </li>
        <li>
          <p className="font-weight-bold">
            <Link to="/company/invite" className="alert-link">
              <span>Hire</span>
            </Link>
          </p>
        </li>
      </ul>
      <br />
      <br />
    </div>
  ) : (
    <div>
      <br />
      <p>When you first log in, you are assigned the default user role. As a user you have two options:</p>
      <br />
      <ul>
        <li>
          <p className="font-weight-bold">
            <Link to="/company/join-company" className="alert-link">
              <span> Join A Company</span>
            </Link>
          </p>
        </li>
        <li>
          <p className="font-weight-bold">
            <Link to="/entity/company/new" className="alert-link">
              <span> Create A Company</span>
            </Link>
          </p>
        </li>
      </ul>
      <br />
      <br />
    </div>
  );

export default homePage;
