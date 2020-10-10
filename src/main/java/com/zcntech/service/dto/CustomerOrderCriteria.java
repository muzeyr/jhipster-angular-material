package com.zcntech.service.dto;

import java.io.Serializable;
import java.util.Objects;
import io.github.jhipster.service.Criteria;
import io.github.jhipster.service.filter.BooleanFilter;
import io.github.jhipster.service.filter.DoubleFilter;
import io.github.jhipster.service.filter.Filter;
import io.github.jhipster.service.filter.FloatFilter;
import io.github.jhipster.service.filter.IntegerFilter;
import io.github.jhipster.service.filter.LongFilter;
import io.github.jhipster.service.filter.StringFilter;
import io.github.jhipster.service.filter.InstantFilter;

/**
 * Criteria class for the {@link com.zcntech.domain.CustomerOrder} entity. This class is used
 * in {@link com.zcntech.web.rest.CustomerOrderResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /customer-orders?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
public class CustomerOrderCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private LongFilter price;

    private InstantFilter sysDate;

    private LongFilter customerId;

    private LongFilter productId;

    public CustomerOrderCriteria() {
    }

    public CustomerOrderCriteria(CustomerOrderCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.price = other.price == null ? null : other.price.copy();
        this.sysDate = other.sysDate == null ? null : other.sysDate.copy();
        this.customerId = other.customerId == null ? null : other.customerId.copy();
        this.productId = other.productId == null ? null : other.productId.copy();
    }

    @Override
    public CustomerOrderCriteria copy() {
        return new CustomerOrderCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public LongFilter getPrice() {
        return price;
    }

    public void setPrice(LongFilter price) {
        this.price = price;
    }

    public InstantFilter getSysDate() {
        return sysDate;
    }

    public void setSysDate(InstantFilter sysDate) {
        this.sysDate = sysDate;
    }

    public LongFilter getCustomerId() {
        return customerId;
    }

    public void setCustomerId(LongFilter customerId) {
        this.customerId = customerId;
    }

    public LongFilter getProductId() {
        return productId;
    }

    public void setProductId(LongFilter productId) {
        this.productId = productId;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CustomerOrderCriteria that = (CustomerOrderCriteria) o;
        return
            Objects.equals(id, that.id) &&
            Objects.equals(price, that.price) &&
            Objects.equals(sysDate, that.sysDate) &&
            Objects.equals(customerId, that.customerId) &&
            Objects.equals(productId, that.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(
        id,
        price,
        sysDate,
        customerId,
        productId
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "CustomerOrderCriteria{" +
                (id != null ? "id=" + id + ", " : "") +
                (price != null ? "price=" + price + ", " : "") +
                (sysDate != null ? "sysDate=" + sysDate + ", " : "") +
                (customerId != null ? "customerId=" + customerId + ", " : "") +
                (productId != null ? "productId=" + productId + ", " : "") +
            "}";
    }

}
