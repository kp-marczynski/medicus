import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { TreatmentComponent } from './treatment.component';
import { TreatmentDetailComponent } from './treatment-detail.component';
import { TreatmentUpdateComponent } from './treatment-update.component';
import { TreatmentDeletePopupComponent, TreatmentDeleteDialogComponent } from './treatment-delete-dialog.component';
import { treatmentRoute, treatmentPopupRoute } from './treatment.route';

const ENTITY_STATES = [...treatmentRoute, ...treatmentPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    TreatmentComponent,
    TreatmentDetailComponent,
    TreatmentUpdateComponent,
    TreatmentDeleteDialogComponent,
    TreatmentDeletePopupComponent
  ],
  entryComponents: [TreatmentComponent, TreatmentUpdateComponent, TreatmentDeleteDialogComponent, TreatmentDeletePopupComponent]
})
export class MedicusTreatmentModule {}
