import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { ITreatment } from 'app/shared/model/treatment.model';

@Component({
  selector: 'jhi-treatment-detail',
  templateUrl: './treatment-detail.component.html'
})
export class TreatmentDetailComponent implements OnInit {
  treatment: ITreatment;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ treatment }) => {
      this.treatment = treatment;
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }
  previousState() {
    window.history.back();
  }
}
