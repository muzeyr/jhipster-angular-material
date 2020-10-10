 import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';  
import { MaterialModule } from '../../shared/material.module';

@NgModule({
  imports: [ 
    MaterialModule,
    RouterModule.forChild([
      {
        path: 'customer',
        loadChildren: () => import('./customer/customer.module').then(m => m.ZcntechCustomerModule),
      },
      {
        path: 'product',
        loadChildren: () => import('./product/product.module').then(m => m.ZcntechProductModule),
      },
      {
        path: 'customer-order',
        loadChildren: () => import('./customer-order/customer-order.module').then(m => m.ZcntechCustomerOrderModule),
      },
    ]), 
  ],
})
export class ZcntechEntityModule {}
