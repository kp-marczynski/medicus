import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IExaminationType } from 'app/shared/model/examination-type.model';

@Component({
  selector: 'jhi-examination-type-detail',
  templateUrl: './examination-type-detail.component.html'
})
export class ExaminationTypeDetailComponent implements OnInit {
  examinationType: IExaminationType;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ examinationType }) => {
      this.examinationType = examinationType;
    });
  }

  previousState() {
    window.history.back();
  }
}
