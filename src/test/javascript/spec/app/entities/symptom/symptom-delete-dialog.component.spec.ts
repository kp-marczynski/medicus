import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { SymptomDeleteDialogComponent } from 'app/entities/symptom/symptom-delete-dialog.component';
import { SymptomService } from 'app/entities/symptom/symptom.service';

describe('Component Tests', () => {
  describe('Symptom Management Delete Component', () => {
    let comp: SymptomDeleteDialogComponent;
    let fixture: ComponentFixture<SymptomDeleteDialogComponent>;
    let service: SymptomService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [SymptomDeleteDialogComponent]
      })
        .overrideTemplate(SymptomDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SymptomDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(SymptomService);
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
