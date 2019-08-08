import { INotification } from 'app/shared/model/notification.model';

export interface IEmployee {
  id?: number;
  login?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  hired?: boolean;
  imageContentType?: string;
  image?: any;
  userLogin?: string;
  userId?: number;
  notifications?: INotification[];
  companyName?: string;
  companyId?: number;
}

export const defaultValue: Readonly<IEmployee> = {
  hired: false
};
