import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { ProcedureComponent } from './procedure.component';
import { ProcedureDetailComponent } from './procedure-detail.component';
import { ProcedureUpdateComponent } from './procedure-update.component';
import { ProcedureDeletePopupComponent, ProcedureDeleteDialogComponent } from './procedure-delete-dialog.component';
import { procedureRoute, procedurePopupRoute } from './procedure.route';

const ENTITY_STATES = [...procedureRoute, ...procedurePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES)],
  declarations: [
    ProcedureComponent,
    ProcedureDetailComponent,
    ProcedureUpdateComponent,
    ProcedureDeleteDialogComponent,
    ProcedureDeletePopupComponent
  ],
  entryComponents: [ProcedureComponent, ProcedureUpdateComponent, ProcedureDeleteDialogComponent, ProcedureDeletePopupComponent]
})
export class MedicusProcedureModule {}
