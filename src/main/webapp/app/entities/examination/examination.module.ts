import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {ExaminationComponent} from './examination.component';
import {ExaminationUpdateComponent} from './examination-update.component';
import {ExaminationDeleteDialogComponent, ExaminationDeletePopupComponent} from './examination-delete-dialog.component';
import {examinationPopupRoute, examinationRoute} from './examination.route';
import {MedicusExaminationInjectableModule} from "app/entities/examination/examination-injectable.module";

const ENTITY_STATES = [...examinationRoute, ...examinationPopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusExaminationInjectableModule],
  entryComponents: [ExaminationComponent, ExaminationUpdateComponent, ExaminationDeleteDialogComponent, ExaminationDeletePopupComponent]
})
export class MedicusExaminationModule {}
