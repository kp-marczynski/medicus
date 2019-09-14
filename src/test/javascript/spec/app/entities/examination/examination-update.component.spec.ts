import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationUpdateComponent } from 'app/entities/examination/examination-update.component';
import { ExaminationService } from 'app/entities/examination/examination.service';
import { Examination } from 'app/shared/model/examination.model';

describe('Component Tests', () => {
  describe('Examination Management Update Component', () => {
    let comp: ExaminationUpdateComponent;
    let fixture: ComponentFixture<ExaminationUpdateComponent>;
    let service: ExaminationService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ExaminationUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ExaminationUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Examination(123);
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
        const entity = new Examination();
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
