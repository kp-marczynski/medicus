import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IProcedure } from 'app/shared/model/procedure.model';

@Component({
  selector: 'jhi-procedure-detail',
  templateUrl: './procedure-detail.component.html'
})
export class ProcedureDetailComponent implements OnInit {
  procedure: IProcedure;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ procedure }) => {
      this.procedure = procedure;
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
