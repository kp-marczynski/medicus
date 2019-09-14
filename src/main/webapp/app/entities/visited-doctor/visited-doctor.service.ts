import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';

type EntityResponseType = HttpResponse<IVisitedDoctor>;
type EntityArrayResponseType = HttpResponse<IVisitedDoctor[]>;

@Injectable({ providedIn: 'root' })
export class VisitedDoctorService {
  public resourceUrl = SERVER_API_URL + 'api/visited-doctors';

  constructor(protected http: HttpClient) {}

  create(visitedDoctor: IVisitedDoctor): Observable<EntityResponseType> {
    return this.http.post<IVisitedDoctor>(this.resourceUrl, visitedDoctor, { observe: 'response' });
  }

  update(visitedDoctor: IVisitedDoctor): Observable<EntityResponseType> {
    return this.http.put<IVisitedDoctor>(this.resourceUrl, visitedDoctor, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IVisitedDoctor>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IVisitedDoctor[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
