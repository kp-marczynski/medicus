import { ComponentFixture, TestBed, inject, fakeAsync, tick } from '@angular/core/testing';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { of } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationPackageDeleteDialogComponent } from 'app/entities/examination-package/examination-package-delete-dialog.component';
import { ExaminationPackageService } from 'app/entities/examination-package/examination-package.service';

describe('Component Tests', () => {
  describe('ExaminationPackage Management Delete Component', () => {
    let comp: ExaminationPackageDeleteDialogComponent;
    let fixture: ComponentFixture<ExaminationPackageDeleteDialogComponent>;
    let service: ExaminationPackageService;
    let mockEventManager: any;
    let mockActiveModal: any;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationPackageDeleteDialogComponent]
      })
        .overrideTemplate(ExaminationPackageDeleteDialogComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ExaminationPackageDeleteDialogComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationPackageService);
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
