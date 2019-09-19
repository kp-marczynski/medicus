import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IMedicine } from 'app/shared/model/medicine.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IAppointment } from 'app/shared/model/appointment.model';
import { IFile } from 'app/shared/model/file.model';

export interface ITreatment {
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  title?: string;
  description?: any;
  descriptionScan?: IFile;
  user?: IUser;
  medicines?: IMedicine[];
  visitedDoctors?: IVisitedDoctor[];
  appointments?: IAppointment[];
}

export class Treatment implements ITreatment {
  constructor(
    public id?: number,
    public startDate?: Moment,
    public endDate?: Moment,
    public title?: string,
    public description?: any,
    public descriptionScan?: IFile,
    public user?: IUser,
    public medicines?: IMedicine[],
    public visitedDoctors?: IVisitedDoctor[],
    public appointments?: IAppointment[]
  ) {}
}
