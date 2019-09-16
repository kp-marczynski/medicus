import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { ExaminationComponent } from './examination.component';
import { ExaminationDetailComponent } from './examination-detail.component';
import { ExaminationUpdateComponent } from './examination-update.component';
import { ExaminationDeletePopupComponent, ExaminationDeleteDialogComponent } from './examination-delete-dialog.component';

@NgModule({
  imports: [MedicusSharedModule, RouterModule],
  declarations: [
    ExaminationComponent,
    ExaminationDetailComponent,
    ExaminationUpdateComponent,
    ExaminationDeleteDialogComponent,
    ExaminationDeletePopupComponent
  ],
  exports: [
    ExaminationComponent
  ],
})
export class MedicusExaminationInjectableModule {}
