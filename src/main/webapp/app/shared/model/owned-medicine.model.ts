import { Moment } from 'moment';
import { IMedicine } from 'app/shared/model/medicine.model';
import { IUser } from 'app/core/user/user.model';

export interface IOwnedMedicine {
  id?: number;
  doses?: number;
  expirationDate?: Moment;
  medicine?: IMedicine;
  user?: IUser;
}

export class OwnedMedicine implements IOwnedMedicine {
  constructor(
    public id?: number,
    public doses?: number,
    public expirationDate?: Moment,
    public medicine?: IMedicine,
    public user?: IUser
  ) {}
}
