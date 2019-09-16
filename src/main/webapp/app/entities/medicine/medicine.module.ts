import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { MedicineComponent } from './medicine.component';
import { MedicineUpdateComponent } from './medicine-update.component';
import { MedicineDeletePopupComponent, MedicineDeleteDialogComponent } from './medicine-delete-dialog.component';
import { medicineRoute, medicinePopupRoute } from './medicine.route';
import {MedicusMedicineInjectableModule} from "app/entities/medicine/medicine.injectable-module";

const ENTITY_STATES = [...medicineRoute, ...medicinePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusMedicineInjectableModule],
  entryComponents: [MedicineComponent, MedicineUpdateComponent, MedicineDeleteDialogComponent, MedicineDeletePopupComponent]
})
export class MedicusMedicineModule {}
