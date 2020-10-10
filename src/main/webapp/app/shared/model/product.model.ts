export interface IProduct {
  id?: number;
  name?: string;
  code?: string;
  price?: number;
}

export class Product implements IProduct {
  constructor(public id?: number, public name?: string, public code?: string, public price?: number) {}
}
