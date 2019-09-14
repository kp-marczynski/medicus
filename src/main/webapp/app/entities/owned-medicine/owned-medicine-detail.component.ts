import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';

@Component({
  selector: 'jhi-owned-medicine-detail',
  templateUrl: './owned-medicine-detail.component.html'
})
export class OwnedMedicineDetailComponent implements OnInit {
  ownedMedicine: IOwnedMedicine;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ ownedMedicine }) => {
      this.ownedMedicine = ownedMedicine;
    });
  }

  previousState() {
    window.history.back();
  }
}
