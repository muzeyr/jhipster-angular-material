import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';
import { DATE_TIME_FORMAT } from 'app/shared/constants/input.constants';

import { ICustomerOrder, CustomerOrder } from 'app/shared/model/customer-order.model';
import { CustomerOrderService } from './customer-order.service';
import { ICustomer } from 'app/shared/model/customer.model';
import { CustomerService } from 'app/entities/customer/customer.service';
import { IProduct } from 'app/shared/model/product.model';
import { ProductService } from 'app/entities/product/product.service';

import { debounceTime, tap, switchMap, finalize } from 'rxjs/operators';

type SelectableEntity = ICustomer | IProduct;

@Component({
  selector: 'jhi-customer-order-update',
  templateUrl: './customer-order-update.component.html',
})
export class CustomerOrderUpdateComponent implements OnInit {
  isSaving = false;
  customers: ICustomer[] = [];
  products: IProduct[] = [];
  isLoading: boolean = false;
  selectedCustomer: any;

  editForm = this.fb.group({
    id: [],
    price: [],
    sysDate: [],
    customer: [], 
    product: [],
  });

  constructor(
    protected customerOrderService: CustomerOrderService,
    protected customerService: CustomerService,
    protected productService: ProductService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {

    this.editForm.controls['customer'].valueChanges
      .pipe(
        debounceTime(100),
        tap(() => {
          this.customers = [];
          this.isLoading = true;
        }),
        switchMap(value => {
          let criteria 
          if(value){
              criteria = {
              'name.contains' : value.name ?  value.name: value
           };
            
          } 
          criteria = {
            'customerOrderId.specified': 'false' 
          };
          return this.customerService.query(criteria)
            .pipe(
              finalize(() => {
                console.log(2);
                this.isLoading = false;
              }),
            )
         
        }
        )
      )
      .subscribe(data => {
         this.customers = data.body? data.body: [];
      }, error => {
        this.customers = [];
      });
    this.activatedRoute.data.subscribe(({ customerOrder }) => {
      if (!customerOrder.id) {
        const today = moment().startOf('day');
        customerOrder.sysDate = today;
      }

      this.updateForm(customerOrder);

      this.customerService
        .query({ 'customerOrderId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<ICustomer[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: ICustomer[]) => {
          if (!customerOrder.customer || !customerOrder.customer.id) {
            this.customers = resBody;
          } else {
            this.customerService
              .find(customerOrder.customer.id)
              .pipe(
                map((subRes: HttpResponse<ICustomer>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: ICustomer[]) => (this.customers = concatRes));
          }
        });

      this.productService
        .query({ 'customerOrderId.specified': 'false' })
        .pipe(
          map((res: HttpResponse<IProduct[]>) => {
            return res.body || [];
          })
        )
        .subscribe((resBody: IProduct[]) => {
          if (!customerOrder.product || !customerOrder.product.id) {
            this.products = resBody;
          } else {
            this.productService
              .find(customerOrder.product.id)
              .pipe(
                map((subRes: HttpResponse<IProduct>) => {
                  return subRes.body ? [subRes.body].concat(resBody) : resBody;
                })
              )
              .subscribe((concatRes: IProduct[]) => (this.products = concatRes));
          }
        });
    });
  }

  updateForm(customerOrder: ICustomerOrder): void {
    this.editForm.patchValue({
      id: customerOrder.id,
      price: customerOrder.price,
      sysDate: customerOrder.sysDate ? customerOrder.sysDate.format(DATE_TIME_FORMAT) : null,
      customer: customerOrder.customer?.name,
      product: customerOrder.product,
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const customerOrder = this.createFromForm();
    customerOrder.customer = this.selectedCustomer;
    if (customerOrder.id !== undefined) {
      this.subscribeToSaveResponse(this.customerOrderService.update(customerOrder));
    } else {
      this.subscribeToSaveResponse(this.customerOrderService.create(customerOrder));
    }
  }

  private createFromForm(): ICustomerOrder {
    return {
      ...new CustomerOrder(),
      id: this.editForm.get(['id'])!.value,
      price: this.editForm.get(['price'])!.value,
      sysDate: this.editForm.get(['sysDate'])!.value ? moment(this.editForm.get(['sysDate'])!.value, DATE_TIME_FORMAT) : undefined,
      customer: this.editForm.get(['customer'])!.value,
      product: this.editForm.get(['product'])!.value,
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICustomerOrder>>): void {
    result.subscribe(
      () => this.onSaveSuccess(),
      () => this.onSaveError()
    );
  }

  protected onSaveSuccess(): void {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError(): void {
    this.isSaving = false;
  }

  trackById(index: number, item: SelectableEntity): any {
    return item.id;
  }

  public onTypeChange(uuid: number): void {
   
    this.customers.forEach(item => {
      
      if (item.id === uuid) {
          this.selectedCustomer = item;
          this.editForm.get('customer')?.setValue(item.name);
          return;
      }
  });
 
  }

}
