import { IDoctor } from 'app/shared/model/doctor.model';
import { IAppointment } from 'app/shared/model/appointment.model';
import { IProcedure } from 'app/shared/model/procedure.model';
import { ITreatment } from 'app/shared/model/treatment.model';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

export interface IVisitedDoctor {
  id?: number;
  opinion?: any;
  doctor?: IDoctor;
  appointment?: IAppointment;
  procedure?: IProcedure;
  treatment?: ITreatment;
  examinationPackage?: IExaminationPackage;
}

export class VisitedDoctor implements IVisitedDoctor {
  constructor(
    public id?: number,
    public opinion?: any,
    public doctor?: IDoctor,
    public appointment?: IAppointment,
    public procedure?: IProcedure,
    public treatment?: ITreatment,
    public examinationPackage?: IExaminationPackage
  ) {}
}
