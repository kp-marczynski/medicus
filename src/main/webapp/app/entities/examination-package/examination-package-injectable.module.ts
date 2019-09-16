import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {ExaminationPackageComponent} from './examination-package.component';
import {ExaminationPackageDetailComponent} from './examination-package-detail.component';
import {ExaminationPackageUpdateComponent} from './examination-package-update.component';
import {
  ExaminationPackageDeleteDialogComponent,
  ExaminationPackageDeletePopupComponent
} from './examination-package-delete-dialog.component';
import {MedicusExaminationInjectableModule} from "app/entities/examination/examination-injectable.module";
import {MedicusVisitedDoctorInjectableModule} from "app/entities/visited-doctor/visited-doctor-injectable.module";


@NgModule({
  imports: [MedicusSharedModule, RouterModule, MedicusExaminationInjectableModule, MedicusVisitedDoctorInjectableModule],
  declarations: [
    ExaminationPackageComponent,
    ExaminationPackageDetailComponent,
    ExaminationPackageUpdateComponent,
    ExaminationPackageDeleteDialogComponent,
    ExaminationPackageDeletePopupComponent
  ],
  exports: [
    ExaminationPackageComponent
  ],
  entryComponents: [
    ExaminationPackageComponent,
    ExaminationPackageUpdateComponent,
    ExaminationPackageDeleteDialogComponent,
    ExaminationPackageDeletePopupComponent
  ]
})
export class MedicusExaminationPackageInjectableModule {}
