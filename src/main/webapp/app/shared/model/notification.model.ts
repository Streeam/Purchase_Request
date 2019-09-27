import { Moment } from 'moment';

export const enum NotificationType {
  INVITATION = 'INVITATION',
  WELCOME = 'WELCOME',
  NEW_EMPLOYEE = 'NEW_EMPLOYEE',
  FIRED = 'FIRED',
  ACCEPT_INVITE = 'ACCEPT_INVITE',
  REJECT_REQUEST = 'REJECT_REQUEST',
  REQUEST_TO_JOIN = 'REQUEST_TO_JOIN',
  LEFT_COMPANY = 'LEFT_COMPANY',
  COMPANY_DELETED = 'COMPANY_DELETED',
  ACCEPT_REQUEST = 'ACCEPT_REQUEST',
  REJECT_REQUEST = ' REJECT_REQUEST',
  OTHERS = 'OTHERS'
}

export interface INotification {
  id?: number;
  comment?: string;
  sentDate?: Moment;
  read?: boolean;
  format?: NotificationType;
  company?: number;
  referenced_user?: string;
  employeeId?: number;
}

export const defaultValue: Readonly<INotification> = {
  read: false
};
