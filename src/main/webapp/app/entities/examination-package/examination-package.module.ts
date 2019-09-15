import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { ExaminationPackageComponent } from './examination-package.component';
import { ExaminationPackageDetailComponent } from './examination-package-detail.component';
import { ExaminationPackageUpdateComponent } from './examination-package-update.component';
import {
  ExaminationPackageDeletePopupComponent,
  ExaminationPackageDeleteDialogComponent
} from './examination-package-delete-dialog.component';
import { examinationPackageRoute, examinationPackagePopupRoute } from './examination-package.route';
import {MedicusExaminationInjectableModule} from "app/entities/examination/examination-injectable.module";
import {MedicusVisitedDoctorInjectableModule} from "app/entities/visited-doctor/visited-doctor-injectable.module";

const ENTITY_STATES = [...examinationPackageRoute, ...examinationPackagePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusExaminationInjectableModule, MedicusVisitedDoctorInjectableModule],
  declarations: [
    ExaminationPackageComponent,
    ExaminationPackageDetailComponent,
    ExaminationPackageUpdateComponent,
    ExaminationPackageDeleteDialogComponent,
    ExaminationPackageDeletePopupComponent
  ],
  entryComponents: [
    ExaminationPackageComponent,
    ExaminationPackageUpdateComponent,
    ExaminationPackageDeleteDialogComponent,
    ExaminationPackageDeletePopupComponent
  ]
})
export class MedicusExaminationPackageModule {}
