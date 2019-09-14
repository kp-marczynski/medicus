import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ITreatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from './treatment.service';

@Component({
  selector: 'jhi-treatment-delete-dialog',
  templateUrl: './treatment-delete-dialog.component.html'
})
export class TreatmentDeleteDialogComponent {
  treatment: ITreatment;

  constructor(protected treatmentService: TreatmentService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.treatmentService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'treatmentListModification',
        content: 'Deleted an treatment'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-treatment-delete-popup',
  template: ''
})
export class TreatmentDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ treatment }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(TreatmentDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.treatment = treatment;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/treatment', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/treatment', { outlets: { popup: null } }]);
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
