import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { VisitedDoctorComponent } from './visited-doctor.component';
import { VisitedDoctorDetailComponent } from './visited-doctor-detail.component';
import { VisitedDoctorUpdateComponent } from './visited-doctor-update.component';
import { VisitedDoctorDeletePopupComponent, VisitedDoctorDeleteDialogComponent } from './visited-doctor-delete-dialog.component';
import { visitedDoctorRoute, visitedDoctorPopupRoute } from './visited-doctor.route';

const ENTITY_STATES = [...visitedDoctorRoute, ...visitedDoctorPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    VisitedDoctorComponent,
    VisitedDoctorDetailComponent,
    VisitedDoctorUpdateComponent,
    VisitedDoctorDeleteDialogComponent,
    VisitedDoctorDeletePopupComponent
  ],
  entryComponents: [
    VisitedDoctorComponent,
    VisitedDoctorUpdateComponent,
    VisitedDoctorDeleteDialogComponent,
    VisitedDoctorDeletePopupComponent
  ]
})
export class MedicusVisitedDoctorModule {}
