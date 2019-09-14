import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IExamination } from 'app/shared/model/examination.model';
import { ExaminationService } from './examination.service';

@Component({
  selector: 'jhi-examination-delete-dialog',
  templateUrl: './examination-delete-dialog.component.html'
})
export class ExaminationDeleteDialogComponent {
  examination: IExamination;

  constructor(
    protected examinationService: ExaminationService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.examinationService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'examinationListModification',
        content: 'Deleted an examination'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-examination-delete-popup',
  template: ''
})
export class ExaminationDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ examination }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ExaminationDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.examination = examination;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/examination', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/examination', { outlets: { popup: null } }]);
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
