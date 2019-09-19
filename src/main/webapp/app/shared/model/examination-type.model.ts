import { IUser } from 'app/core/user/user.model';
import { IExamination } from 'app/shared/model/examination.model';

export interface IExaminationType {
  id?: number;
  name?: string;
  unit?: string;
  minGoodValue?: number;
  maxGoodValue?: number;
  minBadValue?: number;
  maxBadValue?: number;
  user?: IUser;
  examinations?: IExamination[];
}

export class ExaminationType implements IExaminationType {
  constructor(
    public id?: number,
    public name?: string,
    public unit?: string,
    public minGoodValue?: number,
    public maxGoodValue?: number,
    public minBadValue?: number,
    public maxBadValue?: number,
    public user?: IUser,
    public examinations?: IExamination[]
  ) {}
}
