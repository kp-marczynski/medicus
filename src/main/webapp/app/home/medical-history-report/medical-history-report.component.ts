import { Component, OnInit } from '@angular/core';
import { MedicalHistoryReportService } from 'app/home/medical-history-report/medical-history-report.service';
import { IMedicalHistoryReport } from 'app/shared/model/medical-history-report.model';

@Component({
  selector: 'jhi-medical-history-report',
  templateUrl: './medical-history-report.component.html',
  styleUrls: ['./medical-history-report.component.scss']
})
export class MedicalHistoryReportComponent implements OnInit {
  medicalHistoryReport: IMedicalHistoryReport;

  constructor(private medicalHistoryReportService: MedicalHistoryReportService) {}

  ngOnInit() {}

  downloadReport() {
    this.medicalHistoryReportService.getReport().subscribe(res => (this.medicalHistoryReport = res.body));
  }
}
