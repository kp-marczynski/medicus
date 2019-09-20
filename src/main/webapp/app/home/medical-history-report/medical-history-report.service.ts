import {Injectable} from '@angular/core';
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SERVER_API_URL} from 'app/app.constants';
import {IMedicalHistoryReport} from 'app/shared/model/medical-history-report.model';
import {createRequestOption} from "app/shared/util/request-util";

type EntityResponseType = HttpResponse<IMedicalHistoryReport>;

@Injectable({providedIn: 'root'})
export class MedicalHistoryReportService {
  public resourceUrl = SERVER_API_URL + 'api/report';

  constructor(protected http: HttpClient) {
  }

  getReport(req?: any): Observable<EntityResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMedicalHistoryReport>(`${this.resourceUrl}`, {params: options, observe: 'response'});
  }
}
