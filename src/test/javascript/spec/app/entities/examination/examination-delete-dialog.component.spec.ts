import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationDeleteDialogComponent } from 'app/entities/examination/examination-delete-dialog.component';
import { ExaminationService } from 'app/entities/examination/examination.service';

describe('Component Tests', () => {
  describe('Examination Management Delete Component', () => {
    let comp: ExaminationDeleteDialogComponent;
    let fixture: ComponentFixture<ExaminationDeleteDialogComponent>;
    let service: ExaminationService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationDeleteDialogComponent]
      })
        .overrideTemplate(ExaminationDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ExaminationDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationService);
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
