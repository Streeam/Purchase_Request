import '../../app.scss';
import React from 'react';
import { Link } from 'react-router-dom';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

export const home = () => (
  <div>
    <br />
    <br />
    <p className="font-weight-bold">
      <span>To get started, please </span>
      <Link to="/login" className="alert-link">
        <FontAwesomeIcon icon="sign-in-alt" /> sign in
      </Link>
    </p>
    <p>
      <Link to="/about" className="btn btn-info d-none d-sm-inline text-white">
        <span>Learn more about Company Information Database</span>
      </Link>
    </p>
    <p>
      If you have any questions about the application please{' '}
      <a href="mailto: bogdanmihoci@gmail.com" target="_blank" rel="noopener noreferrer">
        email
      </a>{' '}
      me.{' '}
    </p>
    <ul>
      <li>
        <p>
          Find the code{' '}
          <a href="https://github.com/Streeam/Purchase_Request" target="_blank" rel="noopener noreferrer">
            {' '}
            here.
          </a>
        </p>
      </li>
      <li>
        <a href="http://stackoverflow.com/tags/jhipster/info" target="_blank" rel="noopener noreferrer">
          Get in contact with through linkedin
        </a>
      </li>
      <li>
        <a href="https://twitter.com/jhipster_book" target="_blank" rel="noopener noreferrer">
          Contact @java_hipster on Twitter
        </a>
      </li>
      <li>
        <a href="https://github.com/mraible/21-points/issues" target="_blank" rel="noopener noreferrer">
          Report an issue
        </a>
      </li>
    </ul>
    <div>
      Don't have an account yet?&nbsp;
      <Link to="/register" className="alert-link">
        Register a new account
      </Link>
    </div>
    <br />
    <br />
  </div>
);

export default home;
