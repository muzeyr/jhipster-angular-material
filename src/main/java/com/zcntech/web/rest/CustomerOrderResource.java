package com.zcntech.web.rest;

import com.zcntech.domain.CustomerOrder;
import com.zcntech.service.CustomerOrderService;
import com.zcntech.web.rest.errors.BadRequestAlertException;
import com.zcntech.service.dto.CustomerOrderCriteria;
import com.zcntech.service.CustomerOrderQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.zcntech.domain.CustomerOrder}.
 */
@RestController
@RequestMapping("/api")
public class CustomerOrderResource {

    private final Logger log = LoggerFactory.getLogger(CustomerOrderResource.class);

    private static final String ENTITY_NAME = "customerOrder";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CustomerOrderService customerOrderService;

    private final CustomerOrderQueryService customerOrderQueryService;

    public CustomerOrderResource(CustomerOrderService customerOrderService, CustomerOrderQueryService customerOrderQueryService) {
        this.customerOrderService = customerOrderService;
        this.customerOrderQueryService = customerOrderQueryService;
    }

    /**
     * {@code POST  /customer-orders} : Create a new customerOrder.
     *
     * @param customerOrder the customerOrder to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new customerOrder, or with status {@code 400 (Bad Request)} if the customerOrder has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/customer-orders")
    public ResponseEntity<CustomerOrder> createCustomerOrder(@RequestBody CustomerOrder customerOrder) throws URISyntaxException {
        log.debug("REST request to save CustomerOrder : {}", customerOrder);
        if (customerOrder.getId() != null) {
            throw new BadRequestAlertException("A new customerOrder cannot already have an ID", ENTITY_NAME, "idexists");
        }
        CustomerOrder result = customerOrderService.save(customerOrder);
        return ResponseEntity.created(new URI("/api/customer-orders/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /customer-orders} : Updates an existing customerOrder.
     *
     * @param customerOrder the customerOrder to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated customerOrder,
     * or with status {@code 400 (Bad Request)} if the customerOrder is not valid,
     * or with status {@code 500 (Internal Server Error)} if the customerOrder couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/customer-orders")
    public ResponseEntity<CustomerOrder> updateCustomerOrder(@RequestBody CustomerOrder customerOrder) throws URISyntaxException {
        log.debug("REST request to update CustomerOrder : {}", customerOrder);
        if (customerOrder.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        CustomerOrder result = customerOrderService.save(customerOrder);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, customerOrder.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /customer-orders} : get all the customerOrders.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of customerOrders in body.
     */
    @GetMapping("/customer-orders")
    public ResponseEntity<List<CustomerOrder>> getAllCustomerOrders(CustomerOrderCriteria criteria, Pageable pageable) {
        log.debug("REST request to get CustomerOrders by criteria: {}", criteria);
        Page<CustomerOrder> page = customerOrderQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /customer-orders/count} : count all the customerOrders.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/customer-orders/count")
    public ResponseEntity<Long> countCustomerOrders(CustomerOrderCriteria criteria) {
        log.debug("REST request to count CustomerOrders by criteria: {}", criteria);
        return ResponseEntity.ok().body(customerOrderQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /customer-orders/:id} : get the "id" customerOrder.
     *
     * @param id the id of the customerOrder to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the customerOrder, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/customer-orders/{id}")
    public ResponseEntity<CustomerOrder> getCustomerOrder(@PathVariable Long id) {
        log.debug("REST request to get CustomerOrder : {}", id);
        Optional<CustomerOrder> customerOrder = customerOrderService.findOne(id);
        return ResponseUtil.wrapOrNotFound(customerOrder);
    }

    /**
     * {@code DELETE  /customer-orders/:id} : delete the "id" customerOrder.
     *
     * @param id the id of the customerOrder to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/customer-orders/{id}")
    public ResponseEntity<Void> deleteCustomerOrder(@PathVariable Long id) {
        log.debug("REST request to delete CustomerOrder : {}", id);
        customerOrderService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }
}
