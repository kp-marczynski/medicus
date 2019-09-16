import { IUser } from 'app/core/user/user.model';
import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { ITreatment } from 'app/shared/model/treatment.model';

export interface IMedicine {
  id?: number;
  name?: string;
  indication?: any;
  leafletContentType?: string;
  leaflet?: any;
  user?: IUser;
  ownedMedicines?: IOwnedMedicine[];
  treatments?: ITreatment[];
}

export class Medicine implements IMedicine {
  constructor(
    public id?: number,
    public name?: string,
    public indication?: any,
    public leafletContentType?: string,
    public leaflet?: any,
    public user?: IUser,
    public ownedMedicines?: IOwnedMedicine[],
    public treatments?: ITreatment[]
  ) {}
}
