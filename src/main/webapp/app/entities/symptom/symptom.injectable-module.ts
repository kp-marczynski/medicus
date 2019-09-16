import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {SymptomComponent} from './symptom.component';
import {SymptomDetailComponent} from './symptom-detail.component';
import {SymptomUpdateComponent} from './symptom-update.component';
import {SymptomDeleteDialogComponent, SymptomDeletePopupComponent} from './symptom-delete-dialog.component';

@NgModule({
  imports: [MedicusSharedModule, RouterModule],
  declarations: [
    SymptomComponent,
    SymptomDetailComponent,
    SymptomUpdateComponent,
    SymptomDeleteDialogComponent,
    SymptomDeletePopupComponent
  ],
  exports: [
    SymptomComponent
  ]
})
export class MedicusSymptomInjectableModule {}
