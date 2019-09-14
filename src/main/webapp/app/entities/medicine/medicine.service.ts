import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IMedicine } from 'app/shared/model/medicine.model';

type EntityResponseType = HttpResponse<IMedicine>;
type EntityArrayResponseType = HttpResponse<IMedicine[]>;

@Injectable({ providedIn: 'root' })
export class MedicineService {
  public resourceUrl = SERVER_API_URL + 'api/medicines';

  constructor(protected http: HttpClient) {}

  create(medicine: IMedicine): Observable<EntityResponseType> {
    return this.http.post<IMedicine>(this.resourceUrl, medicine, { observe: 'response' });
  }

  update(medicine: IMedicine): Observable<EntityResponseType> {
    return this.http.put<IMedicine>(this.resourceUrl, medicine, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMedicine>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMedicine[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }
}
