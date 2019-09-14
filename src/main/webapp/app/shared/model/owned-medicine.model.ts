import { Moment } from 'moment';
import { IUser } from 'app/core/user/user.model';
import { IMedicine } from 'app/shared/model/medicine.model';

export interface IOwnedMedicine {
  id?: number;
  doses?: number;
  expirationDate?: Moment;
  user?: IUser;
  medicines?: IMedicine[];
}

export class OwnedMedicine implements IOwnedMedicine {
  constructor(
    public id?: number,
    public doses?: number,
    public expirationDate?: Moment,
    public user?: IUser,
    public medicines?: IMedicine[]
  ) {}
}
