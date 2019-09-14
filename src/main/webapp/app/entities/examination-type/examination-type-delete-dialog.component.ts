import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IExaminationType } from 'app/shared/model/examination-type.model';
import { ExaminationTypeService } from './examination-type.service';

@Component({
  selector: 'jhi-examination-type-delete-dialog',
  templateUrl: './examination-type-delete-dialog.component.html'
})
export class ExaminationTypeDeleteDialogComponent {
  examinationType: IExaminationType;

  constructor(
    protected examinationTypeService: ExaminationTypeService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.examinationTypeService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'examinationTypeListModification',
        content: 'Deleted an examinationType'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-examination-type-delete-popup',
  template: ''
})
export class ExaminationTypeDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ examinationType }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ExaminationTypeDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.examinationType = examinationType;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/examination-type', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/examination-type', { outlets: { popup: null } }]);
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
