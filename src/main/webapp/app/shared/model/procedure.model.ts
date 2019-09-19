import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IFile } from 'app/shared/model/file.model';

export interface IProcedure {
  id?: number;
  date?: Moment;
  title?: string;
  description?: any;
  descriptionScan?: IFile;
  user?: IUser;
  visitedDoctors?: IVisitedDoctor[];
  appointment?: any;
}

export class Procedure implements IProcedure {
  constructor(
    public id?: number,
    public date?: Moment,
    public title?: string,
    public description?: any,
    public descriptionScan?: IFile,
    public user?: IUser,
    public visitedDoctors?: IVisitedDoctor[],
    public appointment?: any
  ) {}
}
