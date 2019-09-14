import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IExaminationType } from 'app/shared/model/examination-type.model';

type EntityResponseType = HttpResponse<IExaminationType>;
type EntityArrayResponseType = HttpResponse<IExaminationType[]>;

@Injectable({ providedIn: 'root' })
export class ExaminationTypeService {
  public resourceUrl = SERVER_API_URL + 'api/examination-types';

  constructor(protected http: HttpClient) {}

  create(examinationType: IExaminationType): Observable<EntityResponseType> {
    return this.http.post<IExaminationType>(this.resourceUrl, examinationType, { observe: 'response' });
  }

  update(examinationType: IExaminationType): Observable<EntityResponseType> {
    return this.http.put<IExaminationType>(this.resourceUrl, examinationType, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IExaminationType>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExaminationType[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
