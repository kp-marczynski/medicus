import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'examination-type',
        loadChildren: () => import('./examination-type/examination-type.module').then(m => m.MedicusExaminationTypeModule)
      },
      {
        path: 'examination',
        loadChildren: () => import('./examination/examination.module').then(m => m.MedicusExaminationModule)
      },
      {
        path: 'procedure',
        loadChildren: () => import('./procedure/procedure.module').then(m => m.MedicusProcedureModule)
      },
      {
        path: 'treatment',
        loadChildren: () => import('./treatment/treatment.module').then(m => m.MedicusTreatmentModule)
      },
      {
        path: 'medicine',
        loadChildren: () => import('./medicine/medicine.module').then(m => m.MedicusMedicineModule)
      },
      {
        path: 'owned-medicine',
        loadChildren: () => import('./owned-medicine/owned-medicine.module').then(m => m.MedicusOwnedMedicineModule)
      },
      {
        path: 'appointment',
        loadChildren: () => import('./appointment/appointment.module').then(m => m.MedicusAppointmentModule)
      },
      {
        path: 'examination-package',
        loadChildren: () => import('./examination-package/examination-package.module').then(m => m.MedicusExaminationPackageModule)
      },
      {
        path: 'symptom',
        loadChildren: () => import('./symptom/symptom.module').then(m => m.MedicusSymptomModule)
      },
      {
        path: 'doctor',
        loadChildren: () => import('./doctor/doctor.module').then(m => m.MedicusDoctorModule)
      },
      {
        path: 'visited-doctor',
        loadChildren: () => import('./visited-doctor/visited-doctor.module').then(m => m.MedicusVisitedDoctorModule)
      }
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ])
  ],
  declarations: [],
  entryComponents: [],
  providers: []
})
export class MedicusEntityModule {}
