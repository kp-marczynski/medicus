import { IExaminationType } from 'app/shared/model/examination-type.model';
import { IUser } from 'app/core/user/user.model';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

export interface IExamination {
  id?: number;
  value?: number;
  valueModificator?: string;
  examinationType?: IExaminationType;
  user?: IUser;
  examinationPackages?: IExaminationPackage[];
}

export class Examination implements IExamination {
  constructor(
    public id?: number,
    public value?: number,
    public valueModificator?: string,
    public examinationType?: IExaminationType,
    public user?: IUser,
    public examinationPackages?: IExaminationPackage[]
  ) {}
}
