import axios from 'axios';
import { ICrudSearchAction, IPayload } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';
import { ICompany, defaultValue } from 'app/shared/model/company.model';
import { getAsyncEntities as getEmployees } from '../employee/employee.reducer';
import { getSession } from '../../shared/reducers/authentication';

export const ACTION_TYPES = {
  SEARCH_COMPANIES: 'company/SEARCH_COMPANIES',
  FETCH_COMPANY_LIST: 'company/FETCH_COMPANY_LIST',
  FETCH_COMPANY: 'company/FETCH_COMPANY',
  FETCH_CURRENT_COMPANY: 'company/FETCH_CURRENT_COMPANY',
  CREATE_COMPANY: 'company/CREATE_COMPANY',
  UPDATE_COMPANY: 'company/UPDATE_COMPANY',
  DELETE_COMPANY: 'company/DELETE_COMPANY',
  SET_BLOB: 'company/SET_BLOB',
  HIRE_EMPLOYEE: 'company/HIRE_EMPLOYEE',
  REJECT_EMPLOYEE: 'company/REJECT_EMPLOYEE',
  FIRE_EMPLOYEE: 'company/FIRE_EMPLOYEE',
  RESET: 'company/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<ICompany>,
  entity: defaultValue,
  employeeEntity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export declare type ICrudGetActionWithGuard<T> = (
  id: string | number,
  isMounted: boolean
) => IPayload<T> | ((dispatch: any) => IPayload<T>);
export type CompanyState = Readonly<typeof initialState>;

// Reducer

export default (state: CompanyState = initialState, action): CompanyState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_COMPANIES):
    case REQUEST(ACTION_TYPES.FETCH_COMPANY_LIST):
    case REQUEST(ACTION_TYPES.FETCH_COMPANY):
    case REQUEST(ACTION_TYPES.FETCH_CURRENT_COMPANY):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_COMPANY):
    case REQUEST(ACTION_TYPES.UPDATE_COMPANY):
    case REQUEST(ACTION_TYPES.DELETE_COMPANY):
    case REQUEST(ACTION_TYPES.HIRE_EMPLOYEE):
    case REQUEST(ACTION_TYPES.REJECT_EMPLOYEE):
    case REQUEST(ACTION_TYPES.FIRE_EMPLOYEE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_COMPANIES):
    case FAILURE(ACTION_TYPES.FETCH_COMPANY_LIST):
    case FAILURE(ACTION_TYPES.FETCH_COMPANY):
    case FAILURE(ACTION_TYPES.FETCH_CURRENT_COMPANY):
    case FAILURE(ACTION_TYPES.CREATE_COMPANY):
    case FAILURE(ACTION_TYPES.UPDATE_COMPANY):
    case FAILURE(ACTION_TYPES.DELETE_COMPANY):
    case FAILURE(ACTION_TYPES.HIRE_EMPLOYEE):
    case FAILURE(ACTION_TYPES.REJECT_EMPLOYEE):
    case FAILURE(ACTION_TYPES.FIRE_EMPLOYEE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_COMPANIES):
    case SUCCESS(ACTION_TYPES.FETCH_COMPANY_LIST):
      return !action.payload
        ? state
        : {
            ...state,
            loading: false,
            entities: action.payload.data,
            totalItems: parseInt(action.payload.headers['x-total-count'], 10)
          };
    case SUCCESS(ACTION_TYPES.FETCH_COMPANY):
      return !action.payload
        ? state
        : {
            ...state,
            loading: false,
            entity: action.payload.data
          };
    case SUCCESS(ACTION_TYPES.FETCH_CURRENT_COMPANY):
      return !action.payload
        ? state
        : {
            ...state,
            loading: false,
            employeeEntity: action.payload.data
          };
    case SUCCESS(ACTION_TYPES.CREATE_COMPANY):
    case SUCCESS(ACTION_TYPES.UPDATE_COMPANY):
      return !action.payload
        ? state
        : {
            ...state,
            updating: false,
            updateSuccess: true,
            entity: action.payload.data
          };
    case SUCCESS(ACTION_TYPES.DELETE_COMPANY):
    case SUCCESS(ACTION_TYPES.HIRE_EMPLOYEE):
    case SUCCESS(ACTION_TYPES.REJECT_EMPLOYEE):
    case SUCCESS(ACTION_TYPES.FIRE_EMPLOYEE):
      return !action.payload
        ? state
        : {
            ...state,
            updating: false,
            updateSuccess: true,
            entity: {}
          };
    case ACTION_TYPES.SET_BLOB:
      const { name, data, contentType } = action.payload;
      return {
        ...state,
        entity: {
          ...state.entity,
          [name]: data,
          [name + 'ContentType']: contentType
        }
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/companies';
const apiSearchUrl = 'api/_search/companies';

// Actions

export const getSearchEntities: ICrudSearchAction<ICompany> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_COMPANIES,
  payload: axios.get<ICompany>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities = (isMounted, page?, size?, sort?) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_COMPANY_LIST,
    payload: axios.get<ICompany>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getEntity: ICrudGetActionWithGuard<ICompany> = (id, isMounted) => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_COMPANY,
    payload: axios.get<ICompany>(requestUrl).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getCurrentUserEntity = isMounted => {
  const requestUrl = `${apiUrl}/current-company`;
  return {
    type: ACTION_TYPES.FETCH_CURRENT_COMPANY,
    payload: axios.get<ICompany>(requestUrl).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getCurrentUsersCompanyAsync = isMounted => async dispatch => dispatch(getCurrentUserEntity(isMounted));

export const hireEmployee = (companyId: Number, employeeId: Number, isMounted: boolean) => async dispatch => {
  const requestUrl = `${apiUrl}/${companyId}/hire-employee/${employeeId}`;
  const result = await dispatch({
    type: ACTION_TYPES.HIRE_EMPLOYEE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getEmployees(isMounted));
  await dispatch(getCurrentUserEntity(isMounted));
  return result;
};

export const rejectEmployee = (isMounted: boolean, companyId: string, employeeId: string) => async dispatch => {
  const requestUrl = `${apiUrl}/${companyId}/reject-employee/${employeeId}`;
  const result = await dispatch({
    type: ACTION_TYPES.REJECT_EMPLOYEE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getEmployees(isMounted));
  return result;
};

export const fireEmployee = (isMounted: boolean, companyId: string, employeeId: string) => async dispatch => {
  const requestUrl = `${apiUrl}/${companyId}/fire/${employeeId}`;
  const result = await dispatch({
    type: ACTION_TYPES.FIRE_EMPLOYEE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getCurrentUserEntity(isMounted));
  await dispatch(getEmployees(isMounted));
  return result;
};

export const createEntity = (isMounted: boolean, entity) => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_COMPANY,
    payload: axios.post(apiUrl, cleanEntity(entity)).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getSession());
  dispatch(getEntities(isMounted));
  return result;
};

export const updateEntity = (isMounted: boolean, entity) => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_COMPANY,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity = (isMounted: boolean, id) => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_COMPANY,
    payload: axios.delete(requestUrl)
  });
  dispatch(getEntities(isMounted));
  return result;
};

export const setBlob = (name, data, contentType?) => ({
  type: ACTION_TYPES.SET_BLOB,
  payload: {
    name,
    data,
    contentType
  }
});

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
