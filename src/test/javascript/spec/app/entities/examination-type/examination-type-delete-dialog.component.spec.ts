import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationTypeDeleteDialogComponent } from 'app/entities/examination-type/examination-type-delete-dialog.component';
import { ExaminationTypeService } from 'app/entities/examination-type/examination-type.service';

describe('Component Tests', () => {
  describe('ExaminationType Management Delete Component', () => {
    let comp: ExaminationTypeDeleteDialogComponent;
    let fixture: ComponentFixture<ExaminationTypeDeleteDialogComponent>;
    let service: ExaminationTypeService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationTypeDeleteDialogComponent]
      })
        .overrideTemplate(ExaminationTypeDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ExaminationTypeDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationTypeService);
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
