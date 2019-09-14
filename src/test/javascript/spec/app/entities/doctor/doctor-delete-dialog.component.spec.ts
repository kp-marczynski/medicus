import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { DoctorDeleteDialogComponent } from 'app/entities/doctor/doctor-delete-dialog.component';
import { DoctorService } from 'app/entities/doctor/doctor.service';

describe('Component Tests', () => {
  describe('Doctor Management Delete Component', () => {
    let comp: DoctorDeleteDialogComponent;
    let fixture: ComponentFixture<DoctorDeleteDialogComponent>;
    let service: DoctorService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [DoctorDeleteDialogComponent]
      })
        .overrideTemplate(DoctorDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(DoctorDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(DoctorService);
      mockEventManager = fixture.debugElement.injector.get(JhiEventManager);
      mockActiveModal = fixture.debugElement.injector.get(NgbActiveModal);
    });

    describe('confirmDelete', () => {
      it('Should call delete service on confirmDelete', inject(
        [],
        fakeAsync(() => {
          // GIVEN
          spyOn(service, 'delete').and.returnValue(of({}));

          // WHEN
          comp.confirmDelete(123);
          tick();

          // THEN
          expect(service.delete).toHaveBeenCalledWith(123);
          expect(mockActiveModal.dismissSpy).toHaveBeenCalled();
          expect(mockEventManager.broadcastSpy).toHaveBeenCalled();
        })
      ));
    });
  });
});
