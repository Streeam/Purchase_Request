package com.streeam.cims.service.mapper;

import com.streeam.cims.domain.*;
import com.streeam.cims.service.dto.NotificationDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring", uses = {EmployeeMapper.class})
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {

    @Mapping(source = "employee.id", target = "employeeId")
    NotificationDTO toDto(Notification notification);

    @Mapping(source = "employeeId", target = "employee")
    Notification toEntity(NotificationDTO notificationDTO);

    default Notification fromId(Long id) {
        if (id == null) {
            return null;
        }
        Notification notification = new Notification();
        notification.setId(id);
        return notification;
    }
}
