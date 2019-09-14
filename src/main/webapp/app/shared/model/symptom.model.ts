import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface ISymptom {
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  description?: string;
  user?: IUser;
  appointments?: IAppointment[];
}

export class Symptom implements ISymptom {
  constructor(
    public id?: number,
    public startDate?: Moment,
    public endDate?: Moment,
    public description?: string,
    public user?: IUser,
    public appointments?: IAppointment[]
  ) {}
}
