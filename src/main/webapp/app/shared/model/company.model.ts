import { IEmployee } from 'app/shared/model/employee.model';

export interface ICompany {
  id?: number;
  name?: string;
  email?: string;
  phone?: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  country?: string;
  postcode?: string;
  companyLogoContentType?: string;
  companyLogo?: any;
  employees?: IEmployee[];
}

export const defaultValue: Readonly<ICompany> = {};
