import { Moment } from 'moment';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IExamination } from 'app/shared/model/examination.model';
import { IUser } from 'app/core/user/user.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface IExaminationPackage {
  id?: number;
  date?: Moment;
  title?: string;
  examinationPackageScanContentType?: string;
  examinationPackageScan?: any;
  visitedDoctors?: IVisitedDoctor[];
  examination?: IExamination;
  user?: IUser;
  appointment?: IAppointment;
}

export class ExaminationPackage implements IExaminationPackage {
  constructor(
    public id?: number,
    public date?: Moment,
    public title?: string,
    public examinationPackageScanContentType?: string,
    public examinationPackageScan?: any,
    public visitedDoctors?: IVisitedDoctor[],
    public examination?: IExamination,
    public user?: IUser,
    public appointment?: IAppointment
  ) {}
}
