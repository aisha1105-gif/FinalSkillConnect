package com.juw.dbms.skillConnect.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
@AllArgsConstructor
@NoArgsConstructor
public class Client extends User{
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone must be between 10 to 15 digits.")
    private String phone;

    @Size(max = 100)
    private String relatedIndustry;

    @Size(max = 100)
    private String companyName;

}
