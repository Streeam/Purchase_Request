package com.streeam.cims.service;

import com.streeam.cims.domain.Employee;
import com.streeam.cims.domain.Notification;
import com.streeam.cims.domain.enumeration.NotificationType;
import com.streeam.cims.repository.NotificationRepository;
import com.streeam.cims.repository.search.EmployeeSearchRepository;
import com.streeam.cims.repository.search.NotificationSearchRepository;
import com.streeam.cims.service.dto.NotificationDTO;
import com.streeam.cims.service.mapper.NotificationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.streeam.cims.domain.enumeration.NotificationType.*;
import static org.elasticsearch.index.query.QueryBuilders.queryStringQuery;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationService {

    @Autowired
    private EmployeeSearchRepository employeeSearchRepository;

    @Autowired
    private EmployeeSearchRepository employeeRepository;

    private final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;

    private final NotificationMapper notificationMapper;

    private final NotificationSearchRepository notificationSearchRepository;

    public NotificationService(NotificationRepository notificationRepository, NotificationMapper notificationMapper, NotificationSearchRepository notificationSearchRepository) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.notificationSearchRepository = notificationSearchRepository;
    }

    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    public NotificationDTO save(NotificationDTO notificationDTO) {
        log.debug("Request to save Notification : {}", notificationDTO);
        Notification notification = notificationMapper.toEntity(notificationDTO);
        notification = notificationRepository.save(notification);
        NotificationDTO result = notificationMapper.toDto(notification);
        notificationSearchRepository.save(notification);
        return result;
    }

    /**
     * Get all the notifications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> findAll(Pageable pageable) {
        log.debug("Request to get all Notifications");
        return notificationRepository.findAll(pageable)
            .map(notificationMapper::toDto);
    }

    /**
     * Get all the employee's notifications.
     *
     * @param employee .
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<NotificationDTO> findAllByEmployee(Employee employee) {
        log.debug("Request to get all employee's Notifications");
        return notificationRepository.findAllByEmployee(employee).
            stream().
            map(notification -> notificationMapper.toDto(notification)).
            collect(Collectors.toList());
    }

    /**
     * Get one notification by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<NotificationDTO> findOne(Long id) {
        log.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id)
            .map(notificationMapper::toDto);
    }

    /**
     * Delete the notification by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Notification : {}", id);
        notificationRepository.deleteById(id);
        notificationSearchRepository.deleteById(id);
    }

    /**
     * Search for the notification corresponding to the query.
     *
     * @param query the query of the search.
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDTO> search(String query, Pageable pageable) {
        log.debug("Request to search for a page of Notifications for query {}", query);
        return notificationSearchRepository.search(queryStringQuery(query), pageable)
            .map(notificationMapper::toDto);
    }

    NotificationDTO saveWithEmployee(Employee authorEmployee,String referencedEmployeeEmail,Long companyId, NotificationType notificationType, String comment) {
        Instant now = Instant.now();
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setSentDate(now);
        notificationDTO.setFormat(notificationType);
        notificationDTO.setEmployeeId(authorEmployee.getId());
        notificationDTO.setRead(false);
        notificationDTO.setComment(comment);
        notificationDTO.setReferenced_user(referencedEmployeeEmail);
        notificationDTO.setCompany(companyId);
        notificationRepository.save(notificationMapper.toEntity(notificationDTO));
        authorEmployee.getNotifications().add(notificationMapper.toEntity(notificationDTO));
        employeeRepository.save(authorEmployee);
        employeeSearchRepository.save(authorEmployee);
        notificationSearchRepository.save(notificationMapper.toEntity(notificationDTO));
        return notificationDTO;}


    public void deleteAllByEmployee(Employee employeeToDelete) {
        List<Notification> notifications = notificationRepository.findAllByEmployee(employeeToDelete);
        notifications.stream()
            .forEach(notification -> {
                this.delete(notification.getId());
            });
    }


    @Transactional(readOnly = true)
    List<Long> hasUserBeenInvited(String email) {

        List<Notification> userInviteNotification = notificationRepository.findAllByFormat(INVITATION);

        return userInviteNotification.stream()
            .filter(notification -> notification.getReferenced_user().equalsIgnoreCase(email))
            .map(notificationMapper::toDto)
            .map(NotificationDTO::getCompany)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    boolean hasEventOccurredInThePast(Employee toThisEmployee ,NotificationType theEvent, Long inThisCompany, int numberOfDaysAgo) {
        Instant now = Instant.now();
        Instant nDaysAgo = now.minus(numberOfDaysAgo, ChronoUnit.DAYS);

        List<Notification> notifications= notificationRepository.
            findAllByEmployeeAndFormatAndCompany(toThisEmployee, theEvent, inThisCompany);
        return notifications.stream()
            .anyMatch(notification -> notification.getSentDate().isAfter(nDaysAgo));
    }

}
