package com.streeam.cims.repository;

import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.Notification;
import com.streeam.cims.domain.enumeration.NotificationType;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


/**
 * Spring Data  repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findAllByEmployee(Employee employeeToDelete);

    List<Notification> findAllBySentDateBetweenAndEmployeeIdAndFormatAndCompany(LocalDate now, LocalDate threeDaysAgo, Long id, NotificationType fired, Long companyId);

    List<Notification> findAllByFormat(NotificationType notificationType);

}
