import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {ExaminationPackageComponent} from './examination-package.component';
import {ExaminationPackageUpdateComponent} from './examination-package-update.component';
import {
  ExaminationPackageDeleteDialogComponent,
  ExaminationPackageDeletePopupComponent
} from './examination-package-delete-dialog.component';
import {examinationPackagePopupRoute, examinationPackageRoute} from './examination-package.route';
import {MedicusExaminationPackageInjectableModule} from "app/entities/examination-package/examination-package.injectable-module";

const ENTITY_STATES = [...examinationPackageRoute, ...examinationPackagePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusExaminationPackageInjectableModule],
  entryComponents: [
    ExaminationPackageComponent,
    ExaminationPackageUpdateComponent,
    ExaminationPackageDeleteDialogComponent,
    ExaminationPackageDeletePopupComponent
  ]
})
export class MedicusExaminationPackageModule {}
