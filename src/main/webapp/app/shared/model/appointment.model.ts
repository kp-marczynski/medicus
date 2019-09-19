import { Moment } from 'moment';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { IProcedure } from 'app/shared/model/procedure.model';
import { IUser } from 'app/core/user/user.model';
import { ITreatment } from 'app/shared/model/treatment.model';
import { ISymptom } from 'app/shared/model/symptom.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IFile } from 'app/shared/model/file.model';

export interface IAppointment {
  id?: number;
  date?: Moment;
  title?: string;
  description?: any;
  descriptionScan?: IFile;
  examinationPackages?: IExaminationPackage[];
  procedures?: IProcedure[];
  user?: IUser;
  treatments?: ITreatment[];
  symptoms?: ISymptom[];
  visitedDoctors?: IVisitedDoctor[];
}

export class Appointment implements IAppointment {
  constructor(
    public id?: number,
    public date?: Moment,
    public title?: string,
    public description?: any,
    public descriptionScan?: IFile,
    public examinationPackages?: IExaminationPackage[],
    public procedures?: IProcedure[],
    public user?: IUser,
    public treatments?: ITreatment[],
    public symptoms?: ISymptom[],
    public visitedDoctors?: IVisitedDoctor[]
  ) {}
}
