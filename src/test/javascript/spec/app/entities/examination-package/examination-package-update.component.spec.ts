import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationPackageUpdateComponent } from 'app/entities/examination-package/examination-package-update.component';
import { ExaminationPackageService } from 'app/entities/examination-package/examination-package.service';
import { ExaminationPackage } from 'app/shared/model/examination-package.model';

describe('Component Tests', () => {
  describe('ExaminationPackage Management Update Component', () => {
    let comp: ExaminationPackageUpdateComponent;
    let fixture: ComponentFixture<ExaminationPackageUpdateComponent>;
    let service: ExaminationPackageService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationPackageUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ExaminationPackageUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ExaminationPackageUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationPackageService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ExaminationPackage(123);
        spyOn(service, 'update').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.update).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));

      it('Should call create service on save for new entity', fakeAsync(() => {
        // GIVEN
        const entity = new ExaminationPackage();
        spyOn(service, 'create').and.returnValue(of(new HttpResponse({ body: entity })));
        comp.updateForm(entity);
        // WHEN
        comp.save();
        tick(); // simulate async

        // THEN
        expect(service.create).toHaveBeenCalledWith(entity);
        expect(comp.isSaving).toEqual(false);
      }));
    });
  });
});
