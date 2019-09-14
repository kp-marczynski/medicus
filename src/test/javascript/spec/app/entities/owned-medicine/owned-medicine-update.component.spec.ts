import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { OwnedMedicineUpdateComponent } from 'app/entities/owned-medicine/owned-medicine-update.component';
import { OwnedMedicineService } from 'app/entities/owned-medicine/owned-medicine.service';
import { OwnedMedicine } from 'app/shared/model/owned-medicine.model';

describe('Component Tests', () => {
  describe('OwnedMedicine Management Update Component', () => {
    let comp: OwnedMedicineUpdateComponent;
    let fixture: ComponentFixture<OwnedMedicineUpdateComponent>;
    let service: OwnedMedicineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [OwnedMedicineUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(OwnedMedicineUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(OwnedMedicineUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(OwnedMedicineService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new OwnedMedicine(123);
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
        const entity = new OwnedMedicine();
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
