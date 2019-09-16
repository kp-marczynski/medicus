import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {TreatmentComponent} from './treatment.component';
import {TreatmentUpdateComponent} from './treatment-update.component';
import {TreatmentDeleteDialogComponent, TreatmentDeletePopupComponent} from './treatment-delete-dialog.component';
import {treatmentPopupRoute, treatmentRoute} from './treatment.route';
import {MedicusTreatmentInjectableModule} from "app/entities/treatment/treatment-injectable.module";

const ENTITY_STATES = [...treatmentRoute, ...treatmentPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusTreatmentInjectableModule],
  entryComponents: [TreatmentComponent, TreatmentUpdateComponent, TreatmentDeleteDialogComponent, TreatmentDeletePopupComponent]
})
export class MedicusTreatmentModule {}
