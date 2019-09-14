import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';

type EntityResponseType = HttpResponse<IOwnedMedicine>;
type EntityArrayResponseType = HttpResponse<IOwnedMedicine[]>;

@Injectable({ providedIn: 'root' })
export class OwnedMedicineService {
  public resourceUrl = SERVER_API_URL + 'api/owned-medicines';

  constructor(protected http: HttpClient) {}

  create(ownedMedicine: IOwnedMedicine): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ownedMedicine);
    return this.http
      .post<IOwnedMedicine>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(ownedMedicine: IOwnedMedicine): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(ownedMedicine);
    return this.http
      .put<IOwnedMedicine>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IOwnedMedicine>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IOwnedMedicine[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(ownedMedicine: IOwnedMedicine): IOwnedMedicine {
    const copy: IOwnedMedicine = Object.assign({}, ownedMedicine, {
      expirationDate:
        ownedMedicine.expirationDate != null && ownedMedicine.expirationDate.isValid()
          ? ownedMedicine.expirationDate.format(DATE_FORMAT)
          : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.expirationDate = res.body.expirationDate != null ? moment(res.body.expirationDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((ownedMedicine: IOwnedMedicine) => {
        ownedMedicine.expirationDate = ownedMedicine.expirationDate != null ? moment(ownedMedicine.expirationDate) : null;
      });
    }
    return res;
  }
}
