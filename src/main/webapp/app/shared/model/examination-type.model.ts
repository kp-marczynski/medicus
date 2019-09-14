import { IExamination } from 'app/shared/model/examination.model';

export interface IExaminationType {
  id?: number;
  name?: string;
  unit?: string;
  minValue?: number;
  maxValue?: number;
  language?: string;
  examinations?: IExamination[];
}

export class ExaminationType implements IExaminationType {
  constructor(
    public id?: number,
    public name?: string,
    public unit?: string,
    public minValue?: number,
    public maxValue?: number,
    public language?: string,
    public examinations?: IExamination[]
  ) {}
}
