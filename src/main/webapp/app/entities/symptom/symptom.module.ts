import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { SymptomComponent } from './symptom.component';
import { SymptomDetailComponent } from './symptom-detail.component';
import { SymptomUpdateComponent } from './symptom-update.component';
import { SymptomDeletePopupComponent, SymptomDeleteDialogComponent } from './symptom-delete-dialog.component';
import { symptomRoute, symptomPopupRoute } from './symptom.route';

const ENTITY_STATES = [...symptomRoute, ...symptomPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    SymptomComponent,
    SymptomDetailComponent,
    SymptomUpdateComponent,
    SymptomDeleteDialogComponent,
    SymptomDeletePopupComponent
  ],
  entryComponents: [SymptomComponent, SymptomUpdateComponent, SymptomDeleteDialogComponent, SymptomDeletePopupComponent]
})
export class MedicusSymptomModule {}
