import { Moment } from 'moment';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IUser } from 'app/core/user/user.model';
import { IMedicine } from 'app/shared/model/medicine.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface ITreatment {
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  description?: any;
  descriptionScanContentType?: string;
  descriptionScan?: any;
  visitedDoctors?: IVisitedDoctor[];
  user?: IUser;
  medicines?: IMedicine[];
  appointments?: IAppointment[];
}

export class Treatment implements ITreatment {
  constructor(
    public id?: number,
    public startDate?: Moment,
    public endDate?: Moment,
    public description?: any,
    public descriptionScanContentType?: string,
    public descriptionScan?: any,
    public visitedDoctors?: IVisitedDoctor[],
    public user?: IUser,
    public medicines?: IMedicine[],
    public appointments?: IAppointment[]
  ) {}
}
