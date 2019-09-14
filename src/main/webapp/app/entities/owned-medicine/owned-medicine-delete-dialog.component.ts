import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { OwnedMedicineService } from './owned-medicine.service';

@Component({
  selector: 'jhi-owned-medicine-delete-dialog',
  templateUrl: './owned-medicine-delete-dialog.component.html'
})
export class OwnedMedicineDeleteDialogComponent {
  ownedMedicine: IOwnedMedicine;

  constructor(
    protected ownedMedicineService: OwnedMedicineService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.ownedMedicineService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'ownedMedicineListModification',
        content: 'Deleted an ownedMedicine'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-owned-medicine-delete-popup',
  template: ''
})
export class OwnedMedicineDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ ownedMedicine }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(OwnedMedicineDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.ownedMedicine = ownedMedicine;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/owned-medicine', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/owned-medicine', { outlets: { popup: null } }]);
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
