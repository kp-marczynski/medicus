import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IDoctor } from 'app/shared/model/doctor.model';
import { DoctorService } from './doctor.service';

@Component({
  selector: 'jhi-doctor-delete-dialog',
  templateUrl: './doctor-delete-dialog.component.html'
})
export class DoctorDeleteDialogComponent {
  doctor: IDoctor;

  constructor(protected doctorService: DoctorService, public activeModal: NgbActiveModal, protected eventManager: JhiEventManager) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.doctorService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'doctorListModification',
        content: 'Deleted an doctor'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-doctor-delete-popup',
  template: ''
})
export class DoctorDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ doctor }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(DoctorDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.doctor = doctor;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/doctor', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/doctor', { outlets: { popup: null } }]);
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
