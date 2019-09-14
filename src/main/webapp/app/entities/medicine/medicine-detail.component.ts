import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { JhiDataUtils } from 'ng-jhipster';

import { IMedicine } from 'app/shared/model/medicine.model';

@Component({
  selector: 'jhi-medicine-detail',
  templateUrl: './medicine-detail.component.html'
})
export class MedicineDetailComponent implements OnInit {
  medicine: IMedicine;

  constructor(protected dataUtils: JhiDataUtils, protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ medicine }) => {
      this.medicine = medicine;
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
