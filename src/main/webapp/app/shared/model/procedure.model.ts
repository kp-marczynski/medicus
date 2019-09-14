import { Moment } from 'moment';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IUser } from 'app/core/user/user.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface IProcedure {
  id?: number;
  date?: Moment;
  description?: any;
  descriptionScanContentType?: string;
  descriptionScan?: any;
  visitedDoctors?: IVisitedDoctor[];
  user?: IUser;
  appointment?: IAppointment;
}

export class Procedure implements IProcedure {
  constructor(
    public id?: number,
    public date?: Moment,
    public description?: any,
    public descriptionScanContentType?: string,
    public descriptionScan?: any,
    public visitedDoctors?: IVisitedDoctor[],
    public user?: IUser,
    public appointment?: IAppointment
  ) {}
}
