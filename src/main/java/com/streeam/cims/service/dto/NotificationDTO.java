package com.streeam.cims.service.dto;
import java.time.Instant;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;
import com.streeam.cims.domain.enumeration.NotificationType;

/**
 * A DTO for the {@link com.streeam.cims.domain.Notification} entity.
 */
public class NotificationDTO implements Serializable {

    private Long id;

    private String comment;

    @NotNull
    private Instant sentDate;

    @NotNull
    private Boolean read;

    @NotNull
    private NotificationType format;

    private Long company;

    private String referenced_user;

    @NotNull
    private Long employeeId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean isRead() {
        return read;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationType getFormat() {
        return format;
    }

    public void setFormat(NotificationType format) {
        this.format = format;
    }

    public Long getCompany() {
        return company;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public String getReferenced_user() {
        return referenced_user;
    }

    public void setReferenced_user(String referenced_user) {
        this.referenced_user = referenced_user;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        NotificationDTO notificationDTO = (NotificationDTO) o;
        if (notificationDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), notificationDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "NotificationDTO{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", sentDate='" + getSentDate() + "'" +
            ", read='" + isRead() + "'" +
            ", format='" + getFormat() + "'" +
            ", company=" + getCompany() +
            ", referenced_user='" + getReferenced_user() + "'" +
            ", employee=" + getEmployeeId() +
            "}";
    }
}
