import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as moment from 'moment';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ICustomerOrder } from 'app/shared/model/customer-order.model';

type EntityResponseType = HttpResponse<ICustomerOrder>;
type EntityArrayResponseType = HttpResponse<ICustomerOrder[]>;

@Injectable({ providedIn: 'root' })
export class CustomerOrderService {
  public resourceUrl = SERVER_API_URL + 'api/customer-orders';

  constructor(protected http: HttpClient) {}

  create(customerOrder: ICustomerOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customerOrder);
    return this.http
      .post<ICustomerOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(customerOrder: ICustomerOrder): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(customerOrder);
    return this.http
      .put<ICustomerOrder>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ICustomerOrder>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ICustomerOrder[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(customerOrder: ICustomerOrder): ICustomerOrder {
    const copy: ICustomerOrder = Object.assign({}, customerOrder, {
      sysDate: customerOrder.sysDate && customerOrder.sysDate.isValid() ? customerOrder.sysDate.toJSON() : undefined,
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.sysDate = res.body.sysDate ? moment(res.body.sysDate) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((customerOrder: ICustomerOrder) => {
        customerOrder.sysDate = customerOrder.sysDate ? moment(customerOrder.sysDate) : undefined;
      });
    }
    return res;
  }
}
