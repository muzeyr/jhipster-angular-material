package com.zcntech.domain;

import java.time.Instant;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CustomerOrder.class)
public abstract class CustomerOrder_ {

	public static volatile SingularAttribute<CustomerOrder, Instant> sysDate;
	public static volatile SingularAttribute<CustomerOrder, Product> product;
	public static volatile SingularAttribute<CustomerOrder, Long> price;
	public static volatile SingularAttribute<CustomerOrder, Long> id;
	public static volatile SingularAttribute<CustomerOrder, Customer> customer;

	public static final String SYS_DATE = "sysDate";
	public static final String PRODUCT = "product";
	public static final String PRICE = "price";
	public static final String ID = "id";
	public static final String CUSTOMER = "customer";

}

