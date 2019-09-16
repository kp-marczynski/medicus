import { IUser } from 'app/core/user/user.model';
import { IExamination } from 'app/shared/model/examination.model';

export interface IExaminationType {
  id?: number;
  name?: string;
  unit?: string;
  minValue?: number;
  maxValue?: number;
  innerRange?: boolean;
  user?: IUser;
  examinations?: IExamination[];
}

export class ExaminationType implements IExaminationType {
  constructor(
    public id?: number,
    public name?: string,
    public unit?: string,
    public minValue?: number,
    public maxValue?: number,
    public innerRange?: boolean,
    public user?: IUser,
    public examinations?: IExamination[]
  ) {
    this.innerRange = this.innerRange || false;
  }
}
