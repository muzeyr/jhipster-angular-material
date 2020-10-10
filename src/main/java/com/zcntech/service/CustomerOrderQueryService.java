package com.zcntech.service;

import java.util.List;

import javax.persistence.criteria.JoinType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.jhipster.service.QueryService;

import com.zcntech.domain.CustomerOrder;
import com.zcntech.domain.*; // for static metamodels
import com.zcntech.repository.CustomerOrderRepository;
import com.zcntech.service.dto.CustomerOrderCriteria;

/**
 * Service for executing complex queries for {@link CustomerOrder} entities in the database.
 * The main input is a {@link CustomerOrderCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link CustomerOrder} or a {@link Page} of {@link CustomerOrder} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class CustomerOrderQueryService extends QueryService<CustomerOrder> {

    private final Logger log = LoggerFactory.getLogger(CustomerOrderQueryService.class);

    private final CustomerOrderRepository customerOrderRepository;

    public CustomerOrderQueryService(CustomerOrderRepository customerOrderRepository) {
        this.customerOrderRepository = customerOrderRepository;
    }

    /**
     * Return a {@link List} of {@link CustomerOrder} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<CustomerOrder> findByCriteria(CustomerOrderCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<CustomerOrder> specification = createSpecification(criteria);
        return customerOrderRepository.findAll(specification);
    }

    /**
     * Return a {@link Page} of {@link CustomerOrder} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<CustomerOrder> findByCriteria(CustomerOrderCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<CustomerOrder> specification = createSpecification(criteria);
        return customerOrderRepository.findAll(specification, page);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(CustomerOrderCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<CustomerOrder> specification = createSpecification(criteria);
        return customerOrderRepository.count(specification);
    }

    /**
     * Function to convert {@link CustomerOrderCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<CustomerOrder> createSpecification(CustomerOrderCriteria criteria) {
        Specification<CustomerOrder> specification = Specification.where(null);
        if (criteria != null) {
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), CustomerOrder_.id));
            }
            if (criteria.getPrice() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getPrice(), CustomerOrder_.price));
            }
            if (criteria.getSysDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getSysDate(), CustomerOrder_.sysDate));
            }
            if (criteria.getCustomerId() != null) {
                specification = specification.and(buildSpecification(criteria.getCustomerId(),
                    root -> root.join(CustomerOrder_.customer, JoinType.LEFT).get(Customer_.id)));
            }
            if (criteria.getProductId() != null) {
                specification = specification.and(buildSpecification(criteria.getProductId(),
                    root -> root.join(CustomerOrder_.product, JoinType.LEFT).get(Product_.id)));
            }
        }
        return specification;
    }
}
