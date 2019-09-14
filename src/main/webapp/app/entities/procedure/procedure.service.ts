import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import * as moment from 'moment';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { DATE_FORMAT } from 'app/shared/constants/input.constants';
import { map } from 'rxjs/operators';

import { SERVER_API_URL } from 'app/app.constants';
import { createRequestOption } from 'app/shared/util/request-util';
import { IProcedure } from 'app/shared/model/procedure.model';

type EntityResponseType = HttpResponse<IProcedure>;
type EntityArrayResponseType = HttpResponse<IProcedure[]>;

@Injectable({ providedIn: 'root' })
export class ProcedureService {
  public resourceUrl = SERVER_API_URL + 'api/procedures';

  constructor(protected http: HttpClient) {}

  create(procedure: IProcedure): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(procedure);
    return this.http
      .post<IProcedure>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(procedure: IProcedure): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(procedure);
    return this.http
      .put<IProcedure>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IProcedure>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IProcedure[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<any>> {
    return this.http.delete<any>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  protected convertDateFromClient(procedure: IProcedure): IProcedure {
    const copy: IProcedure = Object.assign({}, procedure, {
      date: procedure.date != null && procedure.date.isValid() ? procedure.date.format(DATE_FORMAT) : null
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
      res.body.forEach((procedure: IProcedure) => {
        procedure.date = procedure.date != null ? moment(procedure.date) : null;
      });
    }
    return res;
  }
}
