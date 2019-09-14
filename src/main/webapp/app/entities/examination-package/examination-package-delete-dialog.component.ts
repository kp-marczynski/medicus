import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { NgbActiveModal, NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { ExaminationPackageService } from './examination-package.service';

@Component({
  selector: 'jhi-examination-package-delete-dialog',
  templateUrl: './examination-package-delete-dialog.component.html'
})
export class ExaminationPackageDeleteDialogComponent {
  examinationPackage: IExaminationPackage;

  constructor(
    protected examinationPackageService: ExaminationPackageService,
    public activeModal: NgbActiveModal,
    protected eventManager: JhiEventManager
  ) {}

  clear() {
    this.activeModal.dismiss('cancel');
  }

  confirmDelete(id: number) {
    this.examinationPackageService.delete(id).subscribe(response => {
      this.eventManager.broadcast({
        name: 'examinationPackageListModification',
        content: 'Deleted an examinationPackage'
      });
      this.activeModal.dismiss(true);
    });
  }
}

@Component({
  selector: 'jhi-examination-package-delete-popup',
  template: ''
})
export class ExaminationPackageDeletePopupComponent implements OnInit, OnDestroy {
  protected ngbModalRef: NgbModalRef;

  constructor(protected activatedRoute: ActivatedRoute, protected router: Router, protected modalService: NgbModal) {}

  ngOnInit() {
    this.activatedRoute.data.subscribe(({ examinationPackage }) => {
      setTimeout(() => {
        this.ngbModalRef = this.modalService.open(ExaminationPackageDeleteDialogComponent as Component, { size: 'lg', backdrop: 'static' });
        this.ngbModalRef.componentInstance.examinationPackage = examinationPackage;
        this.ngbModalRef.result.then(
          result => {
            this.router.navigate(['/examination-package', { outlets: { popup: null } }]);
            this.ngbModalRef = null;
          },
          reason => {
            this.router.navigate(['/examination-package', { outlets: { popup: null } }]);
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
