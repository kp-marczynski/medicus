import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IMedicine } from 'app/shared/model/medicine.model';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { IAppointment } from 'app/shared/model/appointment.model';

export interface ITreatment {
  id?: number;
  startDate?: Moment;
  endDate?: Moment;
  title?: string;
  description?: any;
  descriptionScanContentType?: string;
  descriptionScan?: any;
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
    public descriptionScanContentType?: string,
    public descriptionScan?: any,
    public user?: IUser,
    public medicines?: IMedicine[],
    public visitedDoctors?: IVisitedDoctor[],
    public appointments?: IAppointment[]
  ) {}
}
