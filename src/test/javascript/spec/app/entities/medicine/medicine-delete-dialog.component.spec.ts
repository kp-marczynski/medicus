import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { MedicineDeleteDialogComponent } from 'app/entities/medicine/medicine-delete-dialog.component';
import { MedicineService } from 'app/entities/medicine/medicine.service';

describe('Component Tests', () => {
  describe('Medicine Management Delete Component', () => {
    let comp: MedicineDeleteDialogComponent;
    let fixture: ComponentFixture<MedicineDeleteDialogComponent>;
    let service: MedicineService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [MedicineDeleteDialogComponent]
      })
        .overrideTemplate(MedicineDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MedicineDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(MedicineService);
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
