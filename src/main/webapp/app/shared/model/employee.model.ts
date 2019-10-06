import { INotification } from 'app/shared/model/notification.model';
import { IUser } from './user.model';
import { ICompany } from './company.model';

export interface IEmployee {
  id?: number;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  hired?: boolean;
  language?: string;
  imageContentType?: string;
  image?: any;
  user?: IUser;
  userId?: number;
  notifications?: INotification[];
  company?: ICompany;
  companyId?: number;
}

export const defaultValue: Readonly<IEmployee> = {
  hired: false
};
