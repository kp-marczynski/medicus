import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from './appointment.service';

@Component({
  selector: 'jhi-appointment-delete-dialog',
  templateUrl: './appointment-delete-dialog.component.html'
})
export class AppointmentDeleteDialogComponent {
  appointment: IAppointment;

  constructor(
    protected appointmentService: AppointmentService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.appointmentService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'appointmentListModification',
        content: 'Deleted an appointment'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-appointment-delete-popup',
  template: ''
})
export class AppointmentDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ appointment }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(AppointmentDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.appointment = appointment;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/appointment', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/appointment', { outlets: { popup: null } }]);
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
