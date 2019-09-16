import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {SymptomComponent} from './symptom.component';
import {SymptomUpdateComponent} from './symptom-update.component';
import {SymptomDeleteDialogComponent, SymptomDeletePopupComponent} from './symptom-delete-dialog.component';
import {symptomPopupRoute, symptomRoute} from './symptom.route';
import {MedicusSymptomInjectableModule} from "app/entities/symptom/symptom.injectable-module";

const ENTITY_STATES = [...symptomRoute, ...symptomPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusSymptomInjectableModule],
  entryComponents: [SymptomComponent, SymptomUpdateComponent, SymptomDeleteDialogComponent, SymptomDeletePopupComponent]
})
export class MedicusSymptomModule {}
