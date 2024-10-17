package com.mycompany.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A MstCustomer.
 */
@Table("mst_customer")
@org.springframework.data.elasticsearch.annotations.Document(indexName = "mstcustomer")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class MstCustomer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("first_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String firstName;

    @NotNull(message = "must not be null")
    @Column("last_name")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String lastName;

    @NotNull(message = "must not be null")
    @Column("email")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String email;

    @Column("phone_number")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String phoneNumber;

    @Column("address")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Text)
    private String address;

    @Column("loyalty_points")
    @org.springframework.data.elasticsearch.annotations.Field(type = org.springframework.data.elasticsearch.annotations.FieldType.Integer)
    private Integer loyaltyPoints;

    @Transient
    @JsonIgnoreProperties(value = { "deliveries", "mstCustomer" }, allowSetters = true)
    private Set<TrxOrder> orders = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public MstCustomer id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public MstCustomer firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public MstCustomer lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public MstCustomer email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public MstCustomer phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return this.address;
    }

    public MstCustomer address(String address) {
        this.setAddress(address);
        return this;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getLoyaltyPoints() {
        return this.loyaltyPoints;
    }

    public MstCustomer loyaltyPoints(Integer loyaltyPoints) {
        this.setLoyaltyPoints(loyaltyPoints);
        return this;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public Set<TrxOrder> getOrders() {
        return this.orders;
    }

    public void setOrders(Set<TrxOrder> trxOrders) {
        if (this.orders != null) {
            this.orders.forEach(i -> i.setMstCustomer(null));
        }
        if (trxOrders != null) {
            trxOrders.forEach(i -> i.setMstCustomer(this));
        }
        this.orders = trxOrders;
    }

    public MstCustomer orders(Set<TrxOrder> trxOrders) {
        this.setOrders(trxOrders);
        return this;
    }

    public MstCustomer addOrder(TrxOrder trxOrder) {
        this.orders.add(trxOrder);
        trxOrder.setMstCustomer(this);
        return this;
    }

    public MstCustomer removeOrder(TrxOrder trxOrder) {
        this.orders.remove(trxOrder);
        trxOrder.setMstCustomer(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MstCustomer)) {
            return false;
        }
        return getId() != null && getId().equals(((MstCustomer) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "MstCustomer{" +
            "id=" + getId() +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", address='" + getAddress() + "'" +
            ", loyaltyPoints=" + getLoyaltyPoints() +
            "}";
    }
}
