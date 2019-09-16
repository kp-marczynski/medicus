import { Moment } from 'moment';
import { IExamination } from 'app/shared/model/examination.model';
import { IUser } from 'app/core/user/user.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';

export interface IExaminationPackage {
  id?: number;
  date?: Moment;
  title?: string;
  examinationPackageScanContentType?: string;
  examinationPackageScan?: any;
  examinations?: IExamination[];
  user?: IUser;
  visitedDoctors?: IVisitedDoctor[];
  appointment?: any;
}

export class ExaminationPackage implements IExaminationPackage {
  constructor(
    public id?: number,
    public date?: Moment,
    public title?: string,
    public examinationPackageScanContentType?: string,
    public examinationPackageScan?: any,
    public examinations?: IExamination[],
    public user?: IUser,
    public visitedDoctors?: IVisitedDoctor[],
    public appointment?: any
  ) {}
}
