import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { MedicusSharedModule } from 'app/shared/shared.module';
import { AppointmentComponent } from './appointment.component';
import { AppointmentDetailComponent } from './appointment-detail.component';
import { AppointmentUpdateComponent } from './appointment-update.component';
import { AppointmentDeletePopupComponent, AppointmentDeleteDialogComponent } from './appointment-delete-dialog.component';
import { appointmentRoute, appointmentPopupRoute } from './appointment.route';
import { MedicusVisitedDoctorInjectableModule } from 'app/entities/visited-doctor/visited-doctor-injectable.module';
import { MedicusExaminationPackageInjectableModule } from 'app/entities/examination-package/examination-package-injectable.module';
import { MedicusTreatmentInjectableModule } from 'app/entities/treatment/treatment-injectable.module';
import { MedicusProcedureInjectableModule } from 'app/entities/procedure/procedure-injectable.module';
import { MedicusSymptomInjectableModule } from 'app/entities/symptom/symptom-injectable.module';

const ENTITY_STATES = [...appointmentRoute, ...appointmentPopupRoute];

@NgModule({
  imports: [
    MedicusSharedModule,
    RouterModule.forChild(ENTITY_STATES),
    MedicusVisitedDoctorInjectableModule,
    MedicusExaminationPackageInjectableModule,
    MedicusTreatmentInjectableModule,
    MedicusProcedureInjectableModule,
    MedicusSymptomInjectableModule
  ],
  declarations: [
    AppointmentComponent,
    AppointmentDetailComponent,
    AppointmentUpdateComponent,
    AppointmentDeleteDialogComponent,
    AppointmentDeletePopupComponent
  ],
  entryComponents: [AppointmentComponent, AppointmentUpdateComponent, AppointmentDeleteDialogComponent, AppointmentDeletePopupComponent]
})
export class MedicusAppointmentModule {}
