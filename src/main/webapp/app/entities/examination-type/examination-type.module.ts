import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { ExaminationTypeComponent } from './examination-type.component';
import { ExaminationTypeDetailComponent } from './examination-type-detail.component';
import { ExaminationTypeUpdateComponent } from './examination-type-update.component';
import { ExaminationTypeDeletePopupComponent, ExaminationTypeDeleteDialogComponent } from './examination-type-delete-dialog.component';
import { examinationTypeRoute, examinationTypePopupRoute } from './examination-type.route';

const ENTITY_STATES = [...examinationTypeRoute, ...examinationTypePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ExaminationTypeComponent,
    ExaminationTypeDetailComponent,
    ExaminationTypeUpdateComponent,
    ExaminationTypeDeleteDialogComponent,
    ExaminationTypeDeletePopupComponent
  ],
  entryComponents: [
    ExaminationTypeComponent,
    ExaminationTypeUpdateComponent,
    ExaminationTypeDeleteDialogComponent,
    ExaminationTypeDeletePopupComponent
  ]
})
export class MedicusExaminationTypeModule {}
