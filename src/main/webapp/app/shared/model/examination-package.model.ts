import { Moment } from 'moment';
import { IExamination } from 'app/shared/model/examination.model';
import { IUser } from 'app/core/user/user.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IFile } from 'app/shared/model/file.model';

export interface IExaminationPackage {
  id?: number;
  date?: Moment;
  title?: string;
  descriptionScan?: IFile;
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
    public descriptionScan?: IFile,
    public examinations?: IExamination[],
    public user?: IUser,
    public visitedDoctors?: IVisitedDoctor[],
    public appointment?: any
  ) {}
}
