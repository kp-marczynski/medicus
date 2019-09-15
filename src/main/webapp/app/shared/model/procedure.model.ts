import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface IProcedure {
  id?: number;
  date?: Moment;
  description?: any;
  descriptionScanContentType?: string;
  descriptionScan?: any;
  user?: IUser;
  visitedDoctors?: IVisitedDoctor[];
  appointment?: IAppointment;
}

export class Procedure implements IProcedure {
  constructor(
    public id?: number,
    public date?: Moment,
    public description?: any,
    public descriptionScanContentType?: string,
    public descriptionScan?: any,
    public user?: IUser,
    public visitedDoctors?: IVisitedDoctor[],
    public appointment?: IAppointment
  ) {}
}
