import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { MedicineUpdateComponent } from 'app/entities/medicine/medicine-update.component';
import { MedicineService } from 'app/entities/medicine/medicine.service';
import { Medicine } from 'app/shared/model/medicine.model';

describe('Component Tests', () => {
  describe('Medicine Management Update Component', () => {
    let comp: MedicineUpdateComponent;
    let fixture: ComponentFixture<MedicineUpdateComponent>;
    let service: MedicineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [MedicineUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(MedicineUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MedicineUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(MedicineService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Medicine(123);
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
        const entity = new Medicine();
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
