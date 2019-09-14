import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { AppointmentDeleteDialogComponent } from 'app/entities/appointment/appointment-delete-dialog.component';
import { AppointmentService } from 'app/entities/appointment/appointment.service';

describe('Component Tests', () => {
  describe('Appointment Management Delete Component', () => {
    let comp: AppointmentDeleteDialogComponent;
    let fixture: ComponentFixture<AppointmentDeleteDialogComponent>;
    let service: AppointmentService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [AppointmentDeleteDialogComponent]
      })
        .overrideTemplate(AppointmentDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(AppointmentDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AppointmentService);
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
