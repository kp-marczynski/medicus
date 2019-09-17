import { IUser } from 'app/core/user/user.model';
import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { ITreatment } from 'app/shared/model/treatment.model';

export interface IMedicalHistoryReport {
  title?: string;
  reportContentType?: string;
  report?: any;
}

export class MedicalHistoryReport implements IMedicalHistoryReport {
  constructor(public title?: string, public reportContentType?: string, public report?: any) {}
}
