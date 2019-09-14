import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { ProcedureDeleteDialogComponent } from 'app/entities/procedure/procedure-delete-dialog.component';
import { ProcedureService } from 'app/entities/procedure/procedure.service';

describe('Component Tests', () => {
  describe('Procedure Management Delete Component', () => {
    let comp: ProcedureDeleteDialogComponent;
    let fixture: ComponentFixture<ProcedureDeleteDialogComponent>;
    let service: ProcedureService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ProcedureDeleteDialogComponent]
      })
        .overrideTemplate(ProcedureDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ProcedureDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ProcedureService);
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
