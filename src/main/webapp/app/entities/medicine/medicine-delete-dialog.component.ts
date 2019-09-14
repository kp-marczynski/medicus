import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IMedicine } from 'app/shared/model/medicine.model';
import { MedicineService } from './medicine.service';

@Component({
  selector: 'jhi-medicine-delete-dialog',
  templateUrl: './medicine-delete-dialog.component.html'
})
export class MedicineDeleteDialogComponent {
  medicine: IMedicine;

  constructor(protected medicineService: MedicineService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.medicineService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'medicineListModification',
        content: 'Deleted an medicine'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-medicine-delete-popup',
  template: ''
})
export class MedicineDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ medicine }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(MedicineDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.medicine = medicine;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/medicine', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/medicine', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          }
        );
      }, 0);
    });
  }

  ngOnDestroy() {
    this.ngbModalRef = null;
  }
}
