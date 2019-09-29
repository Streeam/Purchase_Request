package com.streeam.cims.service.dto;

import com.streeam.cims.domain.Notification;
import lombok.Data;

import javax.persistence.Lob;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.streeam.cims.domain.Employee} entity.
 */
@Data
public class EmployeeDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 50)
    private String login;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    @NotNull
    @Size(min = 5, max = 254)
    @Pattern(regexp = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")
    private String email;

    @NotNull
    private Boolean hired;

    private String language;

    @Lob
    private byte[] image;

    private String imageContentType;

    private Long userId;

    private String userLogin;

    private Long companyId;

    private String companyName;

    private Set<Notification> notifications;



    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        EmployeeDTO employeeDTO = (EmployeeDTO) o;
        if (employeeDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), employeeDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "EmployeeDTO{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", hired='" + getHired() + "'" +
            ", language='" + getLanguage() + "'" +
            ", image='" + getImage() + "'" +
            ", user=" + getUserId() +
            ", user='" + getUserLogin() + "'" +
            ", company=" + getCompanyId() +
            ", company='" + getCompanyName() + "'" +
            "}";
    }
}
