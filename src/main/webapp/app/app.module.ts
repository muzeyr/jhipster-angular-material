import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import './vendor';
import { ZcntechSharedModule } from 'app/shared/shared.module';
import { ZcntechCoreModule } from 'app/core/core.module';
import { ZcntechAppRoutingModule } from './app-routing.module';
import { ZcntechHomeModule } from './home/home.module';
import { ZcntechEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component'; 
import {MaterialModule } from '../shared/material.module';
@NgModule({
  imports: [
    BrowserModule,
    ZcntechSharedModule,
    ZcntechCoreModule,
    ZcntechHomeModule,
    ZcntechEntityModule,
    ZcntechAppRoutingModule,
    MaterialModule,
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent],
})
export class ZcntechAppModule {}
