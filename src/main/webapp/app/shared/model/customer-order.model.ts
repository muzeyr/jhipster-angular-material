import { Moment } from 'moment';
import { ICustomer } from 'app/shared/model/customer.model';
import { IProduct } from 'app/shared/model/product.model';

export interface ICustomerOrder {
  id?: number;
  price?: number;
  sysDate?: Moment;
  customer?: ICustomer;
  product?: IProduct;
}

export class CustomerOrder implements ICustomerOrder {
  constructor(public id?: number, public price?: number, public sysDate?: Moment, public customer?: ICustomer, public product?: IProduct) {}
}
