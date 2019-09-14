import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { OwnedMedicineComponent } from './owned-medicine.component';
import { OwnedMedicineDetailComponent } from './owned-medicine-detail.component';
import { OwnedMedicineUpdateComponent } from './owned-medicine-update.component';
import { OwnedMedicineDeletePopupComponent, OwnedMedicineDeleteDialogComponent } from './owned-medicine-delete-dialog.component';
import { ownedMedicineRoute, ownedMedicinePopupRoute } from './owned-medicine.route';

const ENTITY_STATES = [...ownedMedicineRoute, ...ownedMedicinePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    OwnedMedicineComponent,
    OwnedMedicineDetailComponent,
    OwnedMedicineUpdateComponent,
    OwnedMedicineDeleteDialogComponent,
    OwnedMedicineDeletePopupComponent
  ],
  entryComponents: [
    OwnedMedicineComponent,
    OwnedMedicineUpdateComponent,
    OwnedMedicineDeleteDialogComponent,
    OwnedMedicineDeletePopupComponent
  ]
})
export class MedicusOwnedMedicineModule {}
