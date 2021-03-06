import { IExaminationType } from 'app/shared/model/examination-type.model';
import { IUser } from 'app/core/user/user.model';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

export interface IExamination {
  id?: number;
  value?: number;
  valueModificator?: string;
  ignoreNumberValue?: boolean;
  examinationType?: IExaminationType;
  user?: IUser;
  examinationPackage?: IExaminationPackage;
}

export class Examination implements IExamination {
  constructor(
    public id?: number,
    public value?: number,
    public valueModificator?: string,
    public ignoreNumberValue?: boolean,
    public examinationType?: IExaminationType,
    public user?: IUser,
    public examinationPackage?: IExaminationPackage
  ) {}
}
