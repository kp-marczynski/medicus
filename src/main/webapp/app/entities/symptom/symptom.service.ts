import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { ISymptom } from 'app/shared/model/symptom.model';

type EntityResponseType = HttpResponse<ISymptom>;
type EntityArrayResponseType = HttpResponse<ISymptom[]>;

@Injectable({ providedIn: 'root' })
export class SymptomService {
  public resourceUrl = SERVER_API_URL + 'api/symptoms';

  constructor(protected http: HttpClient) {}

  create(symptom: ISymptom): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(symptom);
    return this.http
      .post<ISymptom>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(symptom: ISymptom): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(symptom);
    return this.http
      .put<ISymptom>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISymptom>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISymptom[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(symptom: ISymptom): ISymptom {
    const copy: ISymptom = Object.assign({}, symptom, {
      startDate: symptom.startDate != null && symptom.startDate.isValid() ? symptom.startDate.format(DATE_FORMAT) : null,
      endDate: symptom.endDate != null && symptom.endDate.isValid() ? symptom.endDate.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.startDate = res.body.startDate != null ? moment(res.body.startDate) : null;
      res.body.endDate = res.body.endDate != null ? moment(res.body.endDate) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((symptom: ISymptom) => {
        symptom.startDate = symptom.startDate != null ? moment(symptom.startDate) : null;
        symptom.endDate = symptom.endDate != null ? moment(symptom.endDate) : null;
      });
    }
    return res;
  }
}
