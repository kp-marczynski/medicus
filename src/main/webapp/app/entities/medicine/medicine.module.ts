import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { MedicineComponent } from './medicine.component';
import { MedicineDetailComponent } from './medicine-detail.component';
import { MedicineUpdateComponent } from './medicine-update.component';
import { MedicineDeletePopupComponent, MedicineDeleteDialogComponent } from './medicine-delete-dialog.component';
import { medicineRoute, medicinePopupRoute } from './medicine.route';

const ENTITY_STATES = [...medicineRoute, ...medicinePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    MedicineComponent,
    MedicineDetailComponent,
    MedicineUpdateComponent,
    MedicineDeleteDialogComponent,
    MedicineDeletePopupComponent
  ],
  entryComponents: [MedicineComponent, MedicineUpdateComponent, MedicineDeleteDialogComponent, MedicineDeletePopupComponent]
})
export class MedicusMedicineModule {}
