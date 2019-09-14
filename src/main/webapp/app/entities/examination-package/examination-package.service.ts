import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

type EntityResponseType = HttpResponse<IExaminationPackage>;
type EntityArrayResponseType = HttpResponse<IExaminationPackage[]>;

@Injectable({ providedIn: 'root' })
export class ExaminationPackageService {
  public resourceUrl = SERVER_API_URL + 'api/examination-packages';

  constructor(protected http: HttpClient) {}

  create(examinationPackage: IExaminationPackage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(examinationPackage);
    return this.http
      .post<IExaminationPackage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(examinationPackage: IExaminationPackage): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(examinationPackage);
    return this.http
      .put<IExaminationPackage>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IExaminationPackage>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IExaminationPackage[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(examinationPackage: IExaminationPackage): IExaminationPackage {
    const copy: IExaminationPackage = Object.assign({}, examinationPackage, {
      date: examinationPackage.date != null && examinationPackage.date.isValid() ? examinationPackage.date.format(DATE_FORMAT) : null
    });
    return copy;
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date != null ? moment(res.body.date) : null;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((examinationPackage: IExaminationPackage) => {
        examinationPackage.date = examinationPackage.date != null ? moment(examinationPackage.date) : null;
      });
    }
    return res;
  }
}
