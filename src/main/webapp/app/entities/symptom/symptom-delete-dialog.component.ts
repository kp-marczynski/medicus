import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { ISymptom } from 'app/shared/model/symptom.model';
import { SymptomService } from './symptom.service';

@Component({
  selector: 'jhi-symptom-delete-dialog',
  templateUrl: './symptom-delete-dialog.component.html'
})
export class SymptomDeleteDialogComponent {
  symptom: ISymptom;

  constructor(protected symptomService: SymptomService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.symptomService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'symptomListModification',
        content: 'Deleted an symptom'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-symptom-delete-popup',
  template: ''
})
export class SymptomDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ symptom }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(SymptomDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.symptom = symptom;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/symptom', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/symptom', { outlets: { popup: null } }]);
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
