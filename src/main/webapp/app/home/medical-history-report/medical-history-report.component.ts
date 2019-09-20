import {Component, OnInit} from '@angular/core';
import {MedicalHistoryReportService} from 'app/home/medical-history-report/medical-history-report.service';
import {IMedicalHistoryReport} from 'app/shared/model/medical-history-report.model';
import {JhiLanguageService} from 'ng-jhipster';

@Component({
  selector: 'jhi-medical-history-report',
  templateUrl: './medical-history-report.component.html',
  styleUrls: ['./medical-history-report.component.scss']
})
export class MedicalHistoryReportComponent implements OnInit {
  medicalHistoryReport: IMedicalHistoryReport;

  constructor(
    private medicalHistoryReportService: MedicalHistoryReportService,
    private languageService: JhiLanguageService
  ) {
  }

  ngOnInit() {
  }

  downloadReport() {
    this.languageService.getCurrent().then(currentLang => {
      this.medicalHistoryReportService.getReport({language: currentLang}).subscribe(res => (this.medicalHistoryReport = res.body));
    })
  }
}
