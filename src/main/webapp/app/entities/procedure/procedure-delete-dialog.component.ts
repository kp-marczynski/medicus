import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IProcedure } from 'app/shared/model/procedure.model';
import { ProcedureService } from './procedure.service';

@Component({
  selector: 'jhi-procedure-delete-dialog',
  templateUrl: './procedure-delete-dialog.component.html'
})
export class ProcedureDeleteDialogComponent {
  procedure: IProcedure;

  constructor(protected procedureService: ProcedureService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.procedureService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'procedureListModification',
        content: 'Deleted an procedure'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-procedure-delete-popup',
  template: ''
})
export class ProcedureDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ procedure }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ProcedureDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.procedure = procedure;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/procedure', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/procedure', { outlets: { popup: null } }]);
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
