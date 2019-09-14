import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IExaminationPackage } from 'app/shared/model/examination-package.model';

@Component({
  selector: 'jhi-examination-package-detail',
  templateUrl: './examination-package-detail.component.html'
})
export class ExaminationPackageDetailComponent implements OnInit {
  examinationPackage: IExaminationPackage;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ examinationPackage }) => {
      this.examinationPackage = examinationPackage;
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
