import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { VisitedDoctorComponent } from './visited-doctor.component';
import { VisitedDoctorUpdateComponent } from './visited-doctor-update.component';
import { VisitedDoctorDeletePopupComponent, VisitedDoctorDeleteDialogComponent } from './visited-doctor-delete-dialog.component';
import { visitedDoctorRoute, visitedDoctorPopupRoute } from './visited-doctor.route';
import { MedicusVisitedDoctorInjectableModule } from 'app/entities/visited-doctor/visited-doctor.injectable-module';

const ENTITY_STATES = [...visitedDoctorRoute, ...visitedDoctorPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusVisitedDoctorInjectableModule],
  entryComponents: [
    VisitedDoctorComponent,
    VisitedDoctorUpdateComponent,
    VisitedDoctorDeleteDialogComponent,
    VisitedDoctorDeletePopupComponent
  ]
})
export class MedicusVisitedDoctorModule {}
