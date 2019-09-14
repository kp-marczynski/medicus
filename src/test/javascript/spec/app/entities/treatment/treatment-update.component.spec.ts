import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { TreatmentUpdateComponent } from 'app/entities/treatment/treatment-update.component';
import { TreatmentService } from 'app/entities/treatment/treatment.service';
import { Treatment } from 'app/shared/model/treatment.model';

describe('Component Tests', () => {
  describe('Treatment Management Update Component', () => {
    let comp: TreatmentUpdateComponent;
    let fixture: ComponentFixture<TreatmentUpdateComponent>;
    let service: TreatmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [TreatmentUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(TreatmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(TreatmentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(TreatmentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Treatment(123);
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
        const entity = new Treatment();
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
