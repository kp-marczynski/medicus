import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';

export interface IDoctor {
  id?: number;
  name?: string;
  specialization?: string;
  language?: string;
  visitedDoctors?: IVisitedDoctor[];
}

export class Doctor implements IDoctor {
  constructor(
    public id?: number,
    public name?: string,
    public specialization?: string,
    public language?: string,
    public visitedDoctors?: IVisitedDoctor[]
  ) {}
}
