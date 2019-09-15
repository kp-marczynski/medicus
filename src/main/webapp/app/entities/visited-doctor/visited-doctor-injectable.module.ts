import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { VisitedDoctorComponent } from './visited-doctor.component';
import { VisitedDoctorDetailComponent } from './visited-doctor-detail.component';
import { VisitedDoctorUpdateComponent } from './visited-doctor-update.component';
import { VisitedDoctorDeletePopupComponent, VisitedDoctorDeleteDialogComponent } from './visited-doctor-delete-dialog.component';
import { visitedDoctorRoute, visitedDoctorPopupRoute } from './visited-doctor.route';

@NgModule({
  imports: [MedicusSharedModule, RouterModule],
  declarations: [
    VisitedDoctorComponent,
    VisitedDoctorDetailComponent,
    VisitedDoctorUpdateComponent,
    VisitedDoctorDeleteDialogComponent,
    VisitedDoctorDeletePopupComponent
  ],
  exports: [
    VisitedDoctorComponent
  ],
  entryComponents: [
    VisitedDoctorComponent,
    VisitedDoctorUpdateComponent,
    VisitedDoctorDeleteDialogComponent,
    VisitedDoctorDeletePopupComponent
  ]
})
export class MedicusVisitedDoctorInjectableModule {}
