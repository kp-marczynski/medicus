import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';

@Component({
  selector: 'jhi-visited-doctor-detail',
  templateUrl: './visited-doctor-detail.component.html'
})
export class VisitedDoctorDetailComponent implements OnInit {
  visitedDoctor: IVisitedDoctor;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ visitedDoctor }) => {
      this.visitedDoctor = visitedDoctor;
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
