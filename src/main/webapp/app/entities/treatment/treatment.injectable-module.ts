import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {TreatmentComponent} from './treatment.component';
import {TreatmentDetailComponent} from './treatment-detail.component';
import {TreatmentUpdateComponent} from './treatment-update.component';
import {TreatmentDeleteDialogComponent, TreatmentDeletePopupComponent} from './treatment-delete-dialog.component';
import {MedicusMedicineInjectableModule} from "app/entities/medicine/medicine.injectable-module";
import {MedicusVisitedDoctorInjectableModule} from "app/entities/visited-doctor/visited-doctor.injectable-module";

@NgModule({
  imports: [MedicusSharedModule, RouterModule, MedicusMedicineInjectableModule, MedicusVisitedDoctorInjectableModule],
  declarations: [
    TreatmentComponent,
    TreatmentDetailComponent,
    TreatmentUpdateComponent,
    TreatmentDeleteDialogComponent,
    TreatmentDeletePopupComponent
  ],
  exports: [
    TreatmentComponent
  ]
})
export class MedicusTreatmentInjectableModule {}
