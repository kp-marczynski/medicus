import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { HOME_ROUTE } from './home.route';
import { HomeComponent } from './home.component';
import { MedicalHistoryReportComponent } from './medical-history-report/medical-history-report.component';

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild([HOME_ROUTE])],
  declarations: [HomeComponent, MedicalHistoryReportComponent]
})
export class MedicusHomeModule {}
