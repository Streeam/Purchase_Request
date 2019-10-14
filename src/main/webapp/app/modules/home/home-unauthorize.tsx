import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const homePage = props =>
  props.isCurrentUserManager ? (
    <div>Manager</div>
  ) : (
    <div>
      <br />
      <p>When you first log in, you are assigned the default User_Role. As a user you have two options:</p>
      <br />
      <ul>
        <li>
          <p className="font-weight-bold">
            <Link to="/company/join-company" className="alert-link">
              <FontAwesomeIcon icon="sign-in-alt" />
              <span> Join A Company</span>
            </Link>
          </p>
        </li>
        <li>
          <p className="font-weight-bold">
            <Link to="/entity/company/new" className="alert-link">
              <FontAwesomeIcon icon="sign-in-alt" />
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
