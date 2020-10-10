package com.zcntech.web.rest;

import com.zcntech.ZcntechApp;
import com.zcntech.domain.CustomerOrder;
import com.zcntech.domain.Customer;
import com.zcntech.domain.Product;
import com.zcntech.repository.CustomerOrderRepository;
import com.zcntech.service.CustomerOrderService;
import com.zcntech.service.dto.CustomerOrderCriteria;
import com.zcntech.service.CustomerOrderQueryService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link CustomerOrderResource} REST controller.
 */
@SpringBootTest(classes = ZcntechApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class CustomerOrderResourceIT {

    private static final Long DEFAULT_PRICE = 1L;
    private static final Long UPDATED_PRICE = 2L;
    private static final Long SMALLER_PRICE = 1L - 1L;

    private static final Instant DEFAULT_SYS_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SYS_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    @Autowired
    private CustomerOrderRepository customerOrderRepository;

    @Autowired
    private CustomerOrderService customerOrderService;

    @Autowired
    private CustomerOrderQueryService customerOrderQueryService;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCustomerOrderMockMvc;

    private CustomerOrder customerOrder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createEntity(EntityManager em) {
        CustomerOrder customerOrder = new CustomerOrder()
            .price(DEFAULT_PRICE)
            .sysDate(DEFAULT_SYS_DATE);
        return customerOrder;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static CustomerOrder createUpdatedEntity(EntityManager em) {
        CustomerOrder customerOrder = new CustomerOrder()
            .price(UPDATED_PRICE)
            .sysDate(UPDATED_SYS_DATE);
        return customerOrder;
    }

    @BeforeEach
    public void initTest() {
        customerOrder = createEntity(em);
    }

    @Test
    @Transactional
    public void createCustomerOrder() throws Exception {
        int databaseSizeBeforeCreate = customerOrderRepository.findAll().size();
        // Create the CustomerOrder
        restCustomerOrderMockMvc.perform(post("/api/customer-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customerOrder)))
            .andExpect(status().isCreated());

        // Validate the CustomerOrder in the database
        List<CustomerOrder> customerOrderList = customerOrderRepository.findAll();
        assertThat(customerOrderList).hasSize(databaseSizeBeforeCreate + 1);
        CustomerOrder testCustomerOrder = customerOrderList.get(customerOrderList.size() - 1);
        assertThat(testCustomerOrder.getPrice()).isEqualTo(DEFAULT_PRICE);
        assertThat(testCustomerOrder.getSysDate()).isEqualTo(DEFAULT_SYS_DATE);
    }

    @Test
    @Transactional
    public void createCustomerOrderWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = customerOrderRepository.findAll().size();

        // Create the CustomerOrder with an existing ID
        customerOrder.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCustomerOrderMockMvc.perform(post("/api/customer-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customerOrder)))
            .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        List<CustomerOrder> customerOrderList = customerOrderRepository.findAll();
        assertThat(customerOrderList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllCustomerOrders() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].sysDate").value(hasItem(DEFAULT_SYS_DATE.toString())));
    }
    
    @Test
    @Transactional
    public void getCustomerOrder() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get the customerOrder
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/{id}", customerOrder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(customerOrder.getId().intValue()))
            .andExpect(jsonPath("$.price").value(DEFAULT_PRICE.intValue()))
            .andExpect(jsonPath("$.sysDate").value(DEFAULT_SYS_DATE.toString()));
    }


    @Test
    @Transactional
    public void getCustomerOrdersByIdFiltering() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        Long id = customerOrder.getId();

        defaultCustomerOrderShouldBeFound("id.equals=" + id);
        defaultCustomerOrderShouldNotBeFound("id.notEquals=" + id);

        defaultCustomerOrderShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultCustomerOrderShouldNotBeFound("id.greaterThan=" + id);

        defaultCustomerOrderShouldBeFound("id.lessThanOrEqual=" + id);
        defaultCustomerOrderShouldNotBeFound("id.lessThan=" + id);
    }


    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price equals to DEFAULT_PRICE
        defaultCustomerOrderShouldBeFound("price.equals=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price equals to UPDATED_PRICE
        defaultCustomerOrderShouldNotBeFound("price.equals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsNotEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price not equals to DEFAULT_PRICE
        defaultCustomerOrderShouldNotBeFound("price.notEquals=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price not equals to UPDATED_PRICE
        defaultCustomerOrderShouldBeFound("price.notEquals=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsInShouldWork() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price in DEFAULT_PRICE or UPDATED_PRICE
        defaultCustomerOrderShouldBeFound("price.in=" + DEFAULT_PRICE + "," + UPDATED_PRICE);

        // Get all the customerOrderList where price equals to UPDATED_PRICE
        defaultCustomerOrderShouldNotBeFound("price.in=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsNullOrNotNull() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price is not null
        defaultCustomerOrderShouldBeFound("price.specified=true");

        // Get all the customerOrderList where price is null
        defaultCustomerOrderShouldNotBeFound("price.specified=false");
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsGreaterThanOrEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price is greater than or equal to DEFAULT_PRICE
        defaultCustomerOrderShouldBeFound("price.greaterThanOrEqual=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price is greater than or equal to UPDATED_PRICE
        defaultCustomerOrderShouldNotBeFound("price.greaterThanOrEqual=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsLessThanOrEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price is less than or equal to DEFAULT_PRICE
        defaultCustomerOrderShouldBeFound("price.lessThanOrEqual=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price is less than or equal to SMALLER_PRICE
        defaultCustomerOrderShouldNotBeFound("price.lessThanOrEqual=" + SMALLER_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsLessThanSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price is less than DEFAULT_PRICE
        defaultCustomerOrderShouldNotBeFound("price.lessThan=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price is less than UPDATED_PRICE
        defaultCustomerOrderShouldBeFound("price.lessThan=" + UPDATED_PRICE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByPriceIsGreaterThanSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where price is greater than DEFAULT_PRICE
        defaultCustomerOrderShouldNotBeFound("price.greaterThan=" + DEFAULT_PRICE);

        // Get all the customerOrderList where price is greater than SMALLER_PRICE
        defaultCustomerOrderShouldBeFound("price.greaterThan=" + SMALLER_PRICE);
    }


    @Test
    @Transactional
    public void getAllCustomerOrdersBySysDateIsEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where sysDate equals to DEFAULT_SYS_DATE
        defaultCustomerOrderShouldBeFound("sysDate.equals=" + DEFAULT_SYS_DATE);

        // Get all the customerOrderList where sysDate equals to UPDATED_SYS_DATE
        defaultCustomerOrderShouldNotBeFound("sysDate.equals=" + UPDATED_SYS_DATE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersBySysDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where sysDate not equals to DEFAULT_SYS_DATE
        defaultCustomerOrderShouldNotBeFound("sysDate.notEquals=" + DEFAULT_SYS_DATE);

        // Get all the customerOrderList where sysDate not equals to UPDATED_SYS_DATE
        defaultCustomerOrderShouldBeFound("sysDate.notEquals=" + UPDATED_SYS_DATE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersBySysDateIsInShouldWork() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where sysDate in DEFAULT_SYS_DATE or UPDATED_SYS_DATE
        defaultCustomerOrderShouldBeFound("sysDate.in=" + DEFAULT_SYS_DATE + "," + UPDATED_SYS_DATE);

        // Get all the customerOrderList where sysDate equals to UPDATED_SYS_DATE
        defaultCustomerOrderShouldNotBeFound("sysDate.in=" + UPDATED_SYS_DATE);
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersBySysDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);

        // Get all the customerOrderList where sysDate is not null
        defaultCustomerOrderShouldBeFound("sysDate.specified=true");

        // Get all the customerOrderList where sysDate is null
        defaultCustomerOrderShouldNotBeFound("sysDate.specified=false");
    }

    @Test
    @Transactional
    public void getAllCustomerOrdersByCustomerIsEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);
        Customer customer = CustomerResourceIT.createEntity(em);
        em.persist(customer);
        em.flush();
        customerOrder.setCustomer(customer);
        customerOrderRepository.saveAndFlush(customerOrder);
        Long customerId = customer.getId();

        // Get all the customerOrderList where customer equals to customerId
        defaultCustomerOrderShouldBeFound("customerId.equals=" + customerId);

        // Get all the customerOrderList where customer equals to customerId + 1
        defaultCustomerOrderShouldNotBeFound("customerId.equals=" + (customerId + 1));
    }


    @Test
    @Transactional
    public void getAllCustomerOrdersByProductIsEqualToSomething() throws Exception {
        // Initialize the database
        customerOrderRepository.saveAndFlush(customerOrder);
        Product product = ProductResourceIT.createEntity(em);
        em.persist(product);
        em.flush();
        customerOrder.setProduct(product);
        customerOrderRepository.saveAndFlush(customerOrder);
        Long productId = product.getId();

        // Get all the customerOrderList where product equals to productId
        defaultCustomerOrderShouldBeFound("productId.equals=" + productId);

        // Get all the customerOrderList where product equals to productId + 1
        defaultCustomerOrderShouldNotBeFound("productId.equals=" + (productId + 1));
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCustomerOrderShouldBeFound(String filter) throws Exception {
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(customerOrder.getId().intValue())))
            .andExpect(jsonPath("$.[*].price").value(hasItem(DEFAULT_PRICE.intValue())))
            .andExpect(jsonPath("$.[*].sysDate").value(hasItem(DEFAULT_SYS_DATE.toString())));

        // Check, that the count call also returns 1
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultCustomerOrderShouldNotBeFound(String filter) throws Exception {
        restCustomerOrderMockMvc.perform(get("/api/customer-orders?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    public void getNonExistingCustomerOrder() throws Exception {
        // Get the customerOrder
        restCustomerOrderMockMvc.perform(get("/api/customer-orders/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCustomerOrder() throws Exception {
        // Initialize the database
        customerOrderService.save(customerOrder);

        int databaseSizeBeforeUpdate = customerOrderRepository.findAll().size();

        // Update the customerOrder
        CustomerOrder updatedCustomerOrder = customerOrderRepository.findById(customerOrder.getId()).get();
        // Disconnect from session so that the updates on updatedCustomerOrder are not directly saved in db
        em.detach(updatedCustomerOrder);
        updatedCustomerOrder
            .price(UPDATED_PRICE)
            .sysDate(UPDATED_SYS_DATE);

        restCustomerOrderMockMvc.perform(put("/api/customer-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedCustomerOrder)))
            .andExpect(status().isOk());

        // Validate the CustomerOrder in the database
        List<CustomerOrder> customerOrderList = customerOrderRepository.findAll();
        assertThat(customerOrderList).hasSize(databaseSizeBeforeUpdate);
        CustomerOrder testCustomerOrder = customerOrderList.get(customerOrderList.size() - 1);
        assertThat(testCustomerOrder.getPrice()).isEqualTo(UPDATED_PRICE);
        assertThat(testCustomerOrder.getSysDate()).isEqualTo(UPDATED_SYS_DATE);
    }

    @Test
    @Transactional
    public void updateNonExistingCustomerOrder() throws Exception {
        int databaseSizeBeforeUpdate = customerOrderRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCustomerOrderMockMvc.perform(put("/api/customer-orders")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(customerOrder)))
            .andExpect(status().isBadRequest());

        // Validate the CustomerOrder in the database
        List<CustomerOrder> customerOrderList = customerOrderRepository.findAll();
        assertThat(customerOrderList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteCustomerOrder() throws Exception {
        // Initialize the database
        customerOrderService.save(customerOrder);

        int databaseSizeBeforeDelete = customerOrderRepository.findAll().size();

        // Delete the customerOrder
        restCustomerOrderMockMvc.perform(delete("/api/customer-orders/{id}", customerOrder.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<CustomerOrder> customerOrderList = customerOrderRepository.findAll();
        assertThat(customerOrderList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
