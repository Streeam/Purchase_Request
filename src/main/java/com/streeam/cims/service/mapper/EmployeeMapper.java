package com.streeam.cims.service.mapper;

import com.streeam.cims.domain.*;
import com.streeam.cims.service.dto.EmployeeDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Employee} and its DTO {@link EmployeeDTO}.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, CompanyMapper.class})
public interface EmployeeMapper extends EntityMapper<EmployeeDTO, Employee> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "user")
    @Mapping(source = "company.id", target = "companyId")
    @Mapping(source = "company", target = "company")
    EmployeeDTO toDto(Employee employee);

    @Mapping(source = "userId", target = "user")
    @Mapping(target = "notifications", ignore = true)
    @Mapping(target = "removeNotification", ignore = true)
    @Mapping(source = "companyId", target = "company")
    Employee toEntity(EmployeeDTO employeeDTO);

    default Employee fromId(Long id) {
        if (id == null) {
            return null;
        }
        Employee employee = new Employee();
        employee.setId(id);
        return employee;
    }

}
