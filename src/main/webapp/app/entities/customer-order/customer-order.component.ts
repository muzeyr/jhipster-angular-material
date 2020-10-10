import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpHeaders, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager, JhiParseLinks } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';

import { ICustomerOrder } from 'app/shared/model/customer-order.model';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { CustomerOrderService } from './customer-order.service';
import { CustomerOrderDeleteDialogComponent } from './customer-order-delete-dialog.component';

@Component({
  selector: 'jhi-customer-order',
  templateUrl: './customer-order.component.html',
})
export class CustomerOrderComponent implements OnInit, OnDestroy {
  customerOrders: ICustomerOrder[];
  eventSubscriber?: Subscription;
  itemsPerPage: number;
  links: any;
  page: number;
  predicate: string;
  ascending: boolean;

  constructor(
    protected customerOrderService: CustomerOrderService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    protected parseLinks: JhiParseLinks
  ) {
    this.customerOrders = [];
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.page = 0;
    this.links = {
      last: 0,
    };
    this.predicate = 'id';
    this.ascending = true;
  }

  loadAll(): void {
    this.customerOrderService
      .query({
        page: this.page,
        size: this.itemsPerPage,
        sort: this.sort(),
      })
      .subscribe((res: HttpResponse<ICustomerOrder[]>) => this.paginateCustomerOrders(res.body, res.headers));
  }

  reset(): void {
    this.page = 0;
    this.customerOrders = [];
    this.loadAll();
  }

  loadPage(page: number): void {
    this.page = page;
    this.loadAll();
  }

  ngOnInit(): void {
    this.loadAll();
    this.registerChangeInCustomerOrders();
  }

  ngOnDestroy(): void {
    if (this.eventSubscriber) {
      this.eventManager.destroy(this.eventSubscriber);
    }
  }

  trackId(index: number, item: ICustomerOrder): number {
    // eslint-disable-next-line @typescript-eslint/no-unnecessary-type-assertion
    return item.id!;
  }

  registerChangeInCustomerOrders(): void {
    this.eventSubscriber = this.eventManager.subscribe('customerOrderListModification', () => this.reset());
  }

  delete(customerOrder: ICustomerOrder): void {
    const modalRef = this.modalService.open(CustomerOrderDeleteDialogComponent, { size: 'lg', backdrop: 'static' });
    modalRef.componentInstance.customerOrder = customerOrder;
  }

  sort(): string[] {
    const result = [this.predicate + ',' + (this.ascending ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateCustomerOrders(data: ICustomerOrder[] | null, headers: HttpHeaders): void {
    const headersLink = headers.get('link');
    this.links = this.parseLinks.parse(headersLink ? headersLink : '');
    if (data) {
      for (let i = 0; i < data.length; i++) {
        this.customerOrders.push(data[i]);
      }
    }
  }
}
