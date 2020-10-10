package com.zcntech.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * A CustomerOrder.
 */
@Entity
@Table(name = "customer_order")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class CustomerOrder implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "price")
    private Long price;

    @Column(name = "sys_date")
    private Instant sysDate;

    @OneToOne
    @JoinColumn(unique = true)
    private Customer customer;

    @OneToOne
    @JoinColumn(unique = true)
    private Product product;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPrice() {
        return price;
    }

    public CustomerOrder price(Long price) {
        this.price = price;
        return this;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Instant getSysDate() {
        return sysDate;
    }

    public CustomerOrder sysDate(Instant sysDate) {
        this.sysDate = sysDate;
        return this;
    }

    public void setSysDate(Instant sysDate) {
        this.sysDate = sysDate;
    }

    public Customer getCustomer() {
        return customer;
    }

    public CustomerOrder customer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Product getProduct() {
        return product;
    }

    public CustomerOrder product(Product product) {
        this.product = product;
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CustomerOrder)) {
            return false;
        }
        return id != null && id.equals(((CustomerOrder) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrder{" +
            "id=" + getId() +
            ", price=" + getPrice() +
            ", sysDate='" + getSysDate() + "'" +
            "}";
    }
}
