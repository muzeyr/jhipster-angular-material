export interface ICustomer {
  id?: number;
  name?: string;
  code?: string;
  phone?: string;
}

export class Customer implements ICustomer {
  constructor(public id?: number, public name?: string, public code?: string, public phone?: string) {}
}
