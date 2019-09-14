import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IExamination } from 'app/shared/model/examination.model';

type EntityResponseType = HttpResponse<IExamination>;
type EntityArrayResponseType = HttpResponse<IExamination[]>;

@Injectable({ providedIn: 'root' })
export class ExaminationService {
  public resourceUrl = SERVER_API_URL + 'api/examinations';

  constructor(protected http: HttpClient) {}

  create(examination: IExamination): Observable<EntityResponseType> {
    return this.http.post<IExamination>(this.resourceUrl, examination, { observe: 'response' });
  }

  update(examination: IExamination): Observable<EntityResponseType> {
    return this.http.put<IExamination>(this.resourceUrl, examination, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IExamination>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IExamination[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
