import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { ExaminationComponent } from './examination.component';
import { ExaminationDetailComponent } from './examination-detail.component';
import { ExaminationUpdateComponent } from './examination-update.component';
import { ExaminationDeletePopupComponent, ExaminationDeleteDialogComponent } from './examination-delete-dialog.component';
import { examinationRoute, examinationPopupRoute } from './examination.route';

const ENTITY_STATES = [...examinationRoute, ...examinationPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ExaminationComponent,
    ExaminationDetailComponent,
    ExaminationUpdateComponent,
    ExaminationDeleteDialogComponent,
    ExaminationDeletePopupComponent
  ],
  entryComponents: [ExaminationComponent, ExaminationUpdateComponent, ExaminationDeleteDialogComponent, ExaminationDeletePopupComponent]
})
export class MedicusExaminationModule {}
