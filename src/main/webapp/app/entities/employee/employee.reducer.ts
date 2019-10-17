import axios from 'axios';
import { ICrudSearchAction, IPayload, IPayloadResult } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { REQUEST, SUCCESS, FAILURE } from 'app/shared/reducers/action-type.util';

import { IEmployee, defaultValue } from 'app/shared/model/employee.model';
import { getSession } from '../../shared/reducers/authentication';
import { getCompanysNotifiactions, getCurrentEntities as getCurrentNotifications } from '../notification/notification.reducer';

export declare type ICrudGetAllActionWithGuard<T> = (
  isMounted: boolean,
  page?: number,
  size?: number,
  sort?: string
) => IPayload<T> | ((dispatch: any) => IPayload<T>);
export declare type ICrudPutActionWithGuard<T> = (isMounted: boolean, data?: T) => IPayload<T> | IPayloadResult<T>;
export declare type ICrudDeleteActionWithGuard<T> = (isMounted: boolean, id?: string | number) => IPayload<T> | IPayloadResult<T>;
export declare type ICrudGetActionWithGuard<T> = (
  isMounted: boolean,
  id: string | number
) => IPayload<T> | ((dispatch: any) => IPayload<T>);

export const ACTION_TYPES = {
  SEARCH_EMPLOYEES: 'employee/SEARCH_EMPLOYEES',
  FETCH_EMPLOYEE_LIST: 'employee/FETCH_EMPLOYEE_LIST',
  FETCH_EMPLOYEE: 'employee/FETCH_EMPLOYEE',
  FETCH_CURRENT_EMPLOYEE: 'employee/FETCH_CURRENT_EMPLOYEE',
  CREATE_EMPLOYEE: 'employee/CREATE_EMPLOYEE',
  UPDATE_EMPLOYEE: 'employee/UPDATE_EMPLOYEE',
  DELETE_EMPLOYEE: 'employee/DELETE_EMPLOYEE',
  SET_BLOB: 'employee/SET_BLOB',
  RESET: 'employee/RESET',
  JOIN: 'employee/JOIN_COMPANY',
  INVITE: 'employee/INVITE_EMPLOYEE',
  ACCEPT_INVITE: 'employee/ACCEPT_INVITE',
  REJECT_INVITE: 'employee/REJECT_INVITE'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<IEmployee>,
  entity: defaultValue,
  currentEmployeeEntity: defaultValue,
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type EmployeeState = Readonly<typeof initialState>;

// Reducer

export default (state: EmployeeState = initialState, action): EmployeeState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.SEARCH_EMPLOYEES):
    case REQUEST(ACTION_TYPES.FETCH_EMPLOYEE_LIST):
    case REQUEST(ACTION_TYPES.FETCH_EMPLOYEE):
    case REQUEST(ACTION_TYPES.FETCH_CURRENT_EMPLOYEE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_EMPLOYEE):
    case REQUEST(ACTION_TYPES.UPDATE_EMPLOYEE):
    case REQUEST(ACTION_TYPES.DELETE_EMPLOYEE):
    case REQUEST(ACTION_TYPES.JOIN):
    case REQUEST(ACTION_TYPES.INVITE):
    case REQUEST(ACTION_TYPES.ACCEPT_INVITE):
    case REQUEST(ACTION_TYPES.REJECT_INVITE):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.SEARCH_EMPLOYEES):
    case FAILURE(ACTION_TYPES.FETCH_EMPLOYEE_LIST):
    case FAILURE(ACTION_TYPES.FETCH_EMPLOYEE):
    case FAILURE(ACTION_TYPES.FETCH_CURRENT_EMPLOYEE):
    case FAILURE(ACTION_TYPES.CREATE_EMPLOYEE):
    case FAILURE(ACTION_TYPES.UPDATE_EMPLOYEE):
    case FAILURE(ACTION_TYPES.DELETE_EMPLOYEE):
    case FAILURE(ACTION_TYPES.JOIN):
    case FAILURE(ACTION_TYPES.INVITE):
    case FAILURE(ACTION_TYPES.ACCEPT_INVITE):
    case FAILURE(ACTION_TYPES.REJECT_INVITE):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.SEARCH_EMPLOYEES):
    case SUCCESS(ACTION_TYPES.FETCH_EMPLOYEE_LIST):
      return !action.payload
        ? state
        : {
            ...state,
            loading: false,
            entities: action.payload.data,
            totalItems: parseInt(action.payload.headers['x-total-count'], 10)
          };
    case SUCCESS(ACTION_TYPES.FETCH_EMPLOYEE):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.FETCH_CURRENT_EMPLOYEE):
      return {
        ...state,
        loading: false,
        currentEmployeeEntity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_EMPLOYEE):
    case SUCCESS(ACTION_TYPES.UPDATE_EMPLOYEE):
      return !action.payload
        ? state
        : {
            ...state,
            updating: false,
            updateSuccess: true,
            entity: action.payload.data
          };
    case SUCCESS(ACTION_TYPES.DELETE_EMPLOYEE):
    case SUCCESS(ACTION_TYPES.JOIN):
    case SUCCESS(ACTION_TYPES.INVITE):
    case SUCCESS(ACTION_TYPES.ACCEPT_INVITE):
    case SUCCESS(ACTION_TYPES.REJECT_INVITE):
      return {
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

const apiUrl = 'api/employees';
const apiSearchUrl = 'api/_search/employees';

// Actions

export const getSearchEntities: ICrudSearchAction<IEmployee> = (query, page, size, sort) => ({
  type: ACTION_TYPES.SEARCH_EMPLOYEES,
  payload: axios.get<IEmployee>(`${apiSearchUrl}?query=${query}${sort ? `&page=${page}&size=${size}&sort=${sort}` : ''}`)
});

export const getEntities: ICrudGetAllActionWithGuard<IEmployee> = (isMounted, page?, size?, sort?) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_EMPLOYEE_LIST,
    payload: axios.get<IEmployee>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getAllEntities = isMounted => {
  const requestUrl = `${apiUrl}/all?cacheBuster=${new Date().getTime()}`;
  return {
    type: ACTION_TYPES.FETCH_EMPLOYEE_LIST,
    payload: axios.get<IEmployee>(requestUrl).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getEntity: ICrudGetActionWithGuard<IEmployee> = (isMounted: boolean, id) => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_EMPLOYEE,
    payload: axios.get<IEmployee>(requestUrl).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getCurrentEmployeeEntity = (isMounted: boolean) => {
  const requestUrl = `${apiUrl}/current-employee`;
  return {
    type: ACTION_TYPES.FETCH_CURRENT_EMPLOYEE,
    payload: axios.get<IEmployee>(requestUrl).then(result => {
      if (isMounted) {
        return result;
      }
    })
  };
};

export const getCurrentEmployeeAsync = (isMounted: boolean) => dispatch => dispatch(getCurrentEmployeeEntity(isMounted));

export const createEntity: ICrudPutActionWithGuard<IEmployee> = (isMounted, entity) => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_EMPLOYEE,
    payload: axios.post(apiUrl, cleanEntity(entity)).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  dispatch(getEntities(isMounted));
  return result;
};

export const getAsyncEntities = (isMounted: boolean) => async dispatch => dispatch(getAllEntities(isMounted));

export const updateEntity: ICrudPutActionWithGuard<IEmployee> = (isMounted, entity) => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_EMPLOYEE,
    payload: axios.put(apiUrl, cleanEntity(entity)).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getCurrentEmployeeEntity(isMounted));
  return result;
};

export const deleteEntity: ICrudDeleteActionWithGuard<IEmployee> = (isMounted: boolean, id) => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_EMPLOYEE,
    payload: axios.delete(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  dispatch(getEntities(isMounted));
  return result;
};

export const joinCompany = (isMounted: boolean, companyId: string) => async dispatch => {
  const requestUrl = `${apiUrl}/request-to-join/${companyId}`;
  const result = await dispatch({
    type: ACTION_TYPES.JOIN,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getCurrentEmployeeEntity(isMounted));
  return result;
};

export const acceptCompanyInvitation = (isMounted: boolean, companyId: String) => async dispatch => {
  const requestUrl = `${apiUrl}/accept-invitation/${companyId}`;
  const result = await dispatch({
    type: ACTION_TYPES.ACCEPT_INVITE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getSession());
  return result;
};

export const rejectCompanyInvitation = (isMounted: boolean, companyId: String) => async dispatch => {
  const requestUrl = `${apiUrl}/decline-request/${companyId}`;
  const result = await dispatch({
    type: ACTION_TYPES.REJECT_INVITE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getCurrentNotifications(isMounted));
  return result;
};

export const inviteEmployee = (isMounted: boolean, employeeEmail: string, companyId: string) => async dispatch => {
  const requestUrl = `${apiUrl}/invite-to-join/${employeeEmail}`;
  const result = await dispatch({
    type: ACTION_TYPES.INVITE,
    payload: axios.post(requestUrl).then(data => {
      if (isMounted) {
        return data;
      }
    })
  });
  await dispatch(getCompanysNotifiactions(isMounted, companyId));
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
