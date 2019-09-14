import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from './visited-doctor.service';

@Component({
  selector: 'jhi-visited-doctor-delete-dialog',
  templateUrl: './visited-doctor-delete-dialog.component.html'
})
export class VisitedDoctorDeleteDialogComponent {
  visitedDoctor: IVisitedDoctor;

  constructor(
    protected visitedDoctorService: VisitedDoctorService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.visitedDoctorService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'visitedDoctorListModification',
        content: 'Deleted an visitedDoctor'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-visited-doctor-delete-popup',
  template: ''
})
export class VisitedDoctorDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ visitedDoctor }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(VisitedDoctorDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.visitedDoctor = visitedDoctor;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/visited-doctor', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/visited-doctor', { outlets: { popup: null } }]);
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
