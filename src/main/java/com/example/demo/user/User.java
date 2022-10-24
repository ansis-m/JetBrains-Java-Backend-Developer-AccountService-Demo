package com.example.demo.user;

import com.example.demo.numberService.GeneralSequenceNumber;
import com.example.demo.payslip.PaySlip;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Getter
@Setter
@Entity
@Table(name="User")
public class User implements Comparable<User>{


    @Id
    @Column(name = "email", nullable = false)
    private String email;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "IDd", referencedColumnName = "number")
    @JsonIgnore
    private GeneralSequenceNumber number;

    @Column(length = 30)
    private String name;

    @JsonInclude()
    Long id;

    @Column(length = 30)
    private String lastname;

    @Column
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private List<PaySlip> paySlips;


    @JsonIgnore
    @Basic
    @Size(max=2500)
    private ArrayList<String> months;

    @Basic
    private ArrayList<String> roles;

    @JsonIgnore
    @Column
    private Boolean active;

    @JsonIgnore
    @Column
    private Integer failedAttempts;

    public User(){
        failedAttempts = 0;
        months = new ArrayList<String>();
        paySlips = new ArrayList<PaySlip>();
        number = new GeneralSequenceNumber();
        roles = new ArrayList<>();
        active = new Boolean(true);
    }

    public void failedLogin(){
        failedAttempts++;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setId() {
        this.id = number.getNumber();
    }

    public void addPayslip(PaySlip paySlip) {
        paySlips.add(paySlip);
    }

    public void addMonth(String month) {
        months.add(month);
    }


    public boolean valid() {
        return name != null && lastname != null &&
                email != null && password != null &&
                name.length() > 0 && lastname.length() > 0 &&
                password.length() > 0 && email.endsWith("@acme.com");
    }


    public void addRole(String role) {

        roles.add(role);
    }

    public void sortRoles(){
        Collections.sort(this.roles);
    }

    @Override
    public int compareTo(User u) {
        return (int) (this.id - u.getId());
    }

}
