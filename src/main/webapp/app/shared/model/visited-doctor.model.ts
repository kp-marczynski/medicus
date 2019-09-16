import { IUser } from 'app/core/user/user.model';
import { IAppointment } from 'app/shared/model/appointment.model';
import { IProcedure } from 'app/shared/model/procedure.model';
import { ITreatment } from 'app/shared/model/treatment.model';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

export interface IVisitedDoctor {
  id?: number;
  name?: string;
  specialization?: string;
  opinion?: any;
  user?: IUser;
  appointments?: IAppointment[];
  procedures?: IProcedure[];
  treatments?: ITreatment[];
  examinationPackages?: IExaminationPackage[];
}

export class VisitedDoctor implements IVisitedDoctor {
  constructor(
    public id?: number,
    public name?: string,
    public specialization?: string,
    public opinion?: any,
    public user?: IUser,
    public appointments?: IAppointment[],
    public procedures?: IProcedure[],
    public treatments?: ITreatment[],
    public examinationPackages?: IExaminationPackage[]
  ) {}
}
