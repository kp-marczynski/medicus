import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { MedicineComponent } from './medicine.component';
import { MedicineDetailComponent } from './medicine-detail.component';
import { MedicineUpdateComponent } from './medicine-update.component';
import { MedicineDeletePopupComponent, MedicineDeleteDialogComponent } from './medicine-delete-dialog.component';

@NgModule({
  imports: [MedicusSharedModule, RouterModule],
  declarations: [
    MedicineComponent,
    MedicineDetailComponent,
    MedicineUpdateComponent,
    MedicineDeleteDialogComponent,
    MedicineDeletePopupComponent
  ],
  exports: [
    MedicineComponent
  ]
})
export class MedicusMedicineInjectableModule {}
