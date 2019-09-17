import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { SERVER_API_URL } from 'app/app.constants';
import { IMedicalHistoryReport } from 'app/shared/model/medical-history-report.model';

type EntityResponseType = HttpResponse<IMedicalHistoryReport>;

@Injectable({ providedIn: 'root' })
export class MedicalHistoryReportService {
  public resourceUrl = SERVER_API_URL + 'api/report';

  constructor(protected http: HttpClient) {}

  getReport(): Observable<EntityResponseType> {
    return this.http.get<IMedicalHistoryReport>(`${this.resourceUrl}`, { observe: 'response' });
  }
}
