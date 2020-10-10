package com.zcntech.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Product.class)
public abstract class Product_ {

	public static volatile SingularAttribute<Product, String> code;
	public static volatile SingularAttribute<Product, Long> price;
	public static volatile SingularAttribute<Product, String> name;
	public static volatile SingularAttribute<Product, Long> id;

	public static final String CODE = "code";
	public static final String PRICE = "price";
	public static final String NAME = "name";
	public static final String ID = "id";

}

