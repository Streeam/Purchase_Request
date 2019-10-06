import pick from 'lodash/pick';
import { IEmployee } from '../model/employee.model';
import { hasAnyAuthority } from '../auth/private-route';

/**
 * Removes fields with an 'id' field that equals ''.
 * This function was created to prevent entities to be sent to
 * the server with relationship fields with an empty id and thus
 * resulting in a 500.
 *
 * @param entity Object to clean.
 */
export const cleanEntity = entity => {
  const keysToKeep = Object.keys(entity).filter(k => !(entity[k] instanceof Object) || (entity[k]['id'] !== '' && entity[k]['id'] !== -1));

  return pick(entity, keysToKeep);
};

/**
 * Simply map a list of element to a list a object with the element as id.
 *
 * @param idList Elements to map.
 * @returns The list of objects with mapped ids.
 */
export const mapIdList = (idList: ReadonlyArray<any>) =>
  idList.filter((entityId: any) => entityId !== '').map((entityId: any) => ({ id: entityId }));

export const employeeListWithout = (employeeList: ReadonlyArray<IEmployee>, hasAnyAuthorities: string[]): ReadonlyArray<IEmployee> =>
  employeeList.filter(employee => {
    if (employee.user.authorities.length === 0) {
      return false;
    }
    const authorities: string[] = [];
    employee.user.authorities.map(auth => authorities.push(auth.name));
    return !hasAnyAuthority(authorities, hasAnyAuthorities);
  });
