package com.streeam.cims.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.streeam.cims.domain.enumeration.NotificationType;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;

/**
 * A Notification.
 */
@Entity
@Table(name = "notification")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "notification")
public class Notification implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Column(name = "comment")
    private String comment;

    @NotNull
    @Column(name = "sent_date", nullable = false)
    private Instant sentDate;

    @NotNull
    @Column(name = "read", nullable = false)
    private Boolean read;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "format", nullable = false)
    private NotificationType format;

    @Column(name = "company")
    private Long company;

    @Column(name = "referenced_user")
    private String referenced_user;

    @ManyToOne(optional = false)
    @NotNull
    @JsonIgnoreProperties("notifications")
    private Employee employee;

    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public Notification comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Instant getSentDate() {
        return sentDate;
    }

    public Notification sentDate(Instant sentDate) {
        this.sentDate = sentDate;
        return this;
    }

    public void setSentDate(Instant sentDate) {
        this.sentDate = sentDate;
    }

    public Boolean isRead() {
        return read;
    }

    public Notification read(Boolean read) {
        this.read = read;
        return this;
    }

    public void setRead(Boolean read) {
        this.read = read;
    }

    public NotificationType getFormat() {
        return format;
    }

    public Notification format(NotificationType format) {
        this.format = format;
        return this;
    }

    public void setFormat(NotificationType format) {
        this.format = format;
    }

    public Long getCompany() {
        return company;
    }

    public Notification company(Long company) {
        this.company = company;
        return this;
    }

    public void setCompany(Long company) {
        this.company = company;
    }

    public String getReferenced_user() {
        return referenced_user;
    }

    public Notification referenced_user(String referenced_user) {
        this.referenced_user = referenced_user;
        return this;
    }

    public void setReferenced_user(String referenced_user) {
        this.referenced_user = referenced_user;
    }

    public Employee getEmployee() {
        return employee;
    }

    public Notification employee(Employee employee) {
        this.employee = employee;
        return this;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Notification)) {
            return false;
        }
        return id != null && id.equals(((Notification) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "Notification{" +
            "id=" + getId() +
            ", comment='" + getComment() + "'" +
            ", sentDate='" + getSentDate() + "'" +
            ", read='" + isRead() + "'" +
            ", format='" + getFormat() + "'" +
            ", company=" + getCompany() +
            ", referenced_user='" + getReferenced_user() + "'" +
            "}";
    }
}
