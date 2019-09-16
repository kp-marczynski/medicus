import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {MedicusSharedModule} from 'app/shared/shared.module';
import {ProcedureComponent} from './procedure.component';
import {ProcedureUpdateComponent} from './procedure-update.component';
import {ProcedureDeleteDialogComponent, ProcedureDeletePopupComponent} from './procedure-delete-dialog.component';
import {procedurePopupRoute, procedureRoute} from './procedure.route';
import {MedicusProcedureInjectableModule} from "app/entities/procedure/procedure.injectable-module";

const ENTITY_STATES = [...procedureRoute, ...procedurePopupRoute];

@NgModule({
  imports: [MedicusSharedModule, RouterModule.forChild(ENTITY_STATES), MedicusProcedureInjectableModule],
  entryComponents: [ProcedureComponent, ProcedureUpdateComponent, ProcedureDeleteDialogComponent, ProcedureDeletePopupComponent]
})
export class MedicusProcedureModule {}
