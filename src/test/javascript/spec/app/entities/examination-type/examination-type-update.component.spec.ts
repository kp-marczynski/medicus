import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationTypeUpdateComponent } from 'app/entities/examination-type/examination-type-update.component';
import { ExaminationTypeService } from 'app/entities/examination-type/examination-type.service';
import { ExaminationType } from 'app/shared/model/examination-type.model';

describe('Component Tests', () => {
  describe('ExaminationType Management Update Component', () => {
    let comp: ExaminationTypeUpdateComponent;
    let fixture: ComponentFixture<ExaminationTypeUpdateComponent>;
    let service: ExaminationTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationTypeUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ExaminationTypeUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ExaminationTypeUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationTypeService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new ExaminationType(123);
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
        const entity = new ExaminationType();
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
