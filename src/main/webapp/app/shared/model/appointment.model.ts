import { Moment } from 'moment';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { IProcedure } from 'app/shared/model/procedure.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IUser } from 'app/core/user/user.model';
import { ITreatment } from 'app/shared/model/treatment.model';
import { ISymptom } from 'app/shared/model/symptom.model';

export interface IAppointment {
  id?: number;
  date?: Moment;
  appointmentType?: string;
  description?: any;
  descriptionScanContentType?: string;
  descriptionScan?: any;
  examinationPackages?: IExaminationPackage[];
  procedures?: IProcedure[];
  visitedDoctors?: IVisitedDoctor[];
  user?: IUser;
  treatments?: ITreatment[];
  symptoms?: ISymptom[];
}

export class Appointment implements IAppointment {
  constructor(
    public id?: number,
    public date?: Moment,
    public appointmentType?: string,
    public description?: any,
    public descriptionScanContentType?: string,
    public descriptionScan?: any,
    public examinationPackages?: IExaminationPackage[],
    public procedures?: IProcedure[],
    public visitedDoctors?: IVisitedDoctor[],
    public user?: IUser,
    public treatments?: ITreatment[],
    public symptoms?: ISymptom[]
  ) {}
}
