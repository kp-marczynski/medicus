import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { ISymptom } from 'app/shared/model/symptom.model';

@Component({
  selector: 'jhi-symptom-detail',
  templateUrl: './symptom-detail.component.html'
})
export class SymptomDetailComponent implements OnInit {
  symptom: ISymptom;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ symptom }) => {
      this.symptom = symptom;
    });
  }

  previousState() {
    window.history.back();
  }
}
