import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {ProcedureComponent} from './procedure.component';
import {ProcedureDetailComponent} from './procedure-detail.component';
import {ProcedureUpdateComponent} from './procedure-update.component';
import {ProcedureDeleteDialogComponent, ProcedureDeletePopupComponent} from './procedure-delete-dialog.component';

@NgModule({
  imports: [MedicusSharedModule, RouterModule],
  declarations: [
    ProcedureComponent,
    ProcedureDetailComponent,
    ProcedureUpdateComponent,
    ProcedureDeleteDialogComponent,
    ProcedureDeletePopupComponent
  ],
  exports: [
    ProcedureComponent
  ],
  entryComponents: [ProcedureComponent, ProcedureUpdateComponent, ProcedureDeleteDialogComponent, ProcedureDeletePopupComponent]
})
export class MedicusProcedureInjectableModule {}
