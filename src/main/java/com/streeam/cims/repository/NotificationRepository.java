package com.streeam.cims.repository;

import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.Notification;
import com.streeam.cims.domain.enumeration.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByEmployee(Employee employeeToDelete);

    List<Notification> findAllByEmployeeAndFormatAndCompany(Employee employee, NotificationType fired, Long companyId);

    List<Notification> findAllByFormat(NotificationType notificationType);

    List<Notification> findAllByCompany(Long companyId);

}
