const config = {
  VERSION: process.env.VERSION
};

export default config;

export const SERVER_API_URL = process.env.SERVER_API_URL;

export const AUTHORITIES = {
  ADMIN: 'ROLE_ADMIN',
  USER: 'ROLE_USER',
  MANAGER: 'ROLE_MANAGER',
  EMPLOYEE: 'ROLE_EMPLOYEE',
  SALES_MANAGER: 'ROLE_SALES_MANAGER',
  SALES_SUPPORT: 'ROLE_SALES_SUPPORT',
  ACCOUNT_MANAGER: 'ROLE_ACCOUNT_MANAGER',
  ACCOUNT_SUPPORT: 'ROLE_ACCOUNT_SUPPORT',
  CLIENT_SERVICE: 'ROLE_CLIENT_SERVICE',
  QUALITY_MANAGER: 'ROLE_QUALITY_MANAGER',
  QUALITY_CONTROL: 'ROLE_QUALITY_CONTROL',
  QUALITY_ASSURANCE: 'ROLE_QUALITY_ASSURANCE',
  PRODUCTION_MANAGER: 'ROLE_PRODUCTION_MANAGER',
  PRODUCTION_SUPPORT: 'ROLE_PRODUCTION_SUPPORT',
  LOGISTIC_MANAGER: 'ROLE_LOGISTIC_MANAGER',
  LOGISTIC_SUPPORT: 'ROLE_LOGISTIC_SUPPORT',
  ENGINEERING_MANAGER: 'ROLE_ENGINEERING_MANAGER',
  ENGINEER: 'ROLE_ENGINEERING',
  STORE_MANAGER: 'ROLE_STORE_MANAGER',
  STORE_SUPPORT: 'ROLE_STORE_SUPPORT',
  MARKETING_MANAGER: 'ROLE_MARKETING_MANAGER',
  MARKETING_SUPPORT: 'ROLE_MARKETING_SUPPORT'
};

export const GROUPED_AUTHORITIES = [
  {
    text: 'ROLE_SALES_MANAGER',
    id: '9bdb',
    category: 'Sales',
    isChecked: false
  },
  {
    text: 'ROLE_SALES_SUPPORT',
    id: '4589',
    category: 'Sales',
    isChecked: false
  },
  {
    text: 'ROLE_ACCOUNT_MANAGER',
    id: 'e807',
    category: 'Sales',
    isChecked: false
  },
  {
    text: 'ROLE_ACCOUNT_SUPPORT',
    id: 'a0cc',
    category: 'Sales',
    isChecked: false
  },
  {
    text: 'ROLE_CLIENT_SERVICE',
    id: '5e26',
    category: 'Sales',
    isChecked: false
  },
  {
    text: 'ROLE_QUALITY_MANAGER',
    id: 'f849',
    category: 'Quality Management',
    isChecked: false
  },
  {
    text: 'ROLE_QUALITY_CONTROL',
    id: '7aff',
    category: 'Quality Management',
    isChecked: false
  },
  {
    text: 'ROLE_QUALITY_ASSURANCE',
    id: 'b1da',
    category: 'Quality Management',
    isChecked: false
  },
  {
    text: 'ROLE_PRODUCTION_MANAGER',
    id: 'de2f',
    category: 'Production',
    isChecked: false
  },
  {
    text: 'ROLE_PRODUCTION_SUPPORT',
    id: 'b2b1',
    category: 'Production',
    isChecked: false
  },
  {
    text: 'ROLE_LOGISTIC_MANAGER',
    id: 'b2b1',
    category: 'Logistic',
    isChecked: false
  },
  {
    text: 'ROLE_LOGISTIC_SUPPORT',
    id: 'b2b1',
    category: 'Logistic',
    isChecked: false
  },
  {
    text: 'ROLE_ENGINEERING_MANAGER',
    id: 'b2b1',
    category: 'Research and Development',
    isChecked: false
  },
  {
    text: 'ROLE_ENGINEERING',
    id: 'b2b1',
    category: 'Research and Development',
    isChecked: false
  },
  {
    text: 'ROLE_STORE_MANAGER',
    id: 'b2b1',
    category: 'Store',
    isChecked: false
  },
  {
    text: 'ROLE_STORE_SUPPORT',
    id: 'b2b1',
    category: 'Store',
    isChecked: false
  },
  {
    text: 'ROLE_MARKETING_MANAGER',
    id: 'b2b1',
    category: 'Marketing',
    isChecked: false
  },
  {
    text: 'ROLE_MARKETING_SUPPORT',
    id: 'b2b1',
    category: 'Marketing',
    isChecked: false
  }
];

export const NOTIFICATIONS = {
  INVITATION: 'INVITATION',
  WELCOME: 'WELCOME',
  NEW_EMPLOYEE: 'NEW_EMPLOYEE',
  FIRED: 'FIRED',
  ACCEPT_INVITE: 'ACCEPT_INVITE',
  REJECT_REQUEST: 'REJECT_REQUEST',
  REQUEST_TO_JOIN: 'REQUEST_TO_JOIN',
  LEFT_COMPANY: 'LEFT_COMPANY',
  COMPANY_DELETED: 'COMPANY_DELETED',
  ACCEPT_REQUEST: 'ACCEPT_REQUEST',
  REJECT_INVITE: 'REJECT_INVITE',
  OTHERS: 'OTHERS'
};

export const messages = {
  DATA_ERROR_ALERT: 'Internal Error'
};

export const APP_DATE_FORMAT = 'DD/MM/YY HH:mm';
export const APP_TIMESTAMP_FORMAT = 'DD/MM/YY HH:mm:ss';
export const APP_LOCAL_DATE_FORMAT = 'DD/MM/YYYY';
export const APP_LOCAL_DATETIME_FORMAT = 'YYYY-MM-DDTHH:mm';
export const APP_LOCAL_DATETIME_FORMAT_Z = 'YYYY-MM-DDTHH:mm Z';
export const APP_WHOLE_NUMBER_FORMAT = '0,0';
export const APP_TWO_DIGITS_AFTER_POINT_NUMBER_FORMAT = '0,0.[00]';
