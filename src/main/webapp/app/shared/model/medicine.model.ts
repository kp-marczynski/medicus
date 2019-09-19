import { IUser } from 'app/core/user/user.model';
import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { ITreatment } from 'app/shared/model/treatment.model';
import { IFile } from 'app/shared/model/file.model';

export interface IMedicine {
  id?: number;
  name?: string;
  indication?: any;
  leaflet?: IFile;
  user?: IUser;
  ownedMedicines?: IOwnedMedicine[];
  treatments?: ITreatment[];
}

export class Medicine implements IMedicine {
  constructor(
    public id?: number,
    public name?: string,
    public indication?: any,
    public leaflet?: IFile,
    public user?: IUser,
    public ownedMedicines?: IOwnedMedicine[],
    public treatments?: ITreatment[]
  ) {}
}
