import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { TreatmentDeleteDialogComponent } from 'app/entities/treatment/treatment-delete-dialog.component';
import { TreatmentService } from 'app/entities/treatment/treatment.service';

describe('Component Tests', () => {
  describe('Treatment Management Delete Component', () => {
    let comp: TreatmentDeleteDialogComponent;
    let fixture: ComponentFixture<TreatmentDeleteDialogComponent>;
    let service: TreatmentService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [TreatmentDeleteDialogComponent]
      })
        .overrideTemplate(TreatmentDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TreatmentDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TreatmentService);
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
