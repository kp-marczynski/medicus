import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { AppointmentUpdateComponent } from 'app/entities/appointment/appointment-update.component';
import { AppointmentService } from 'app/entities/appointment/appointment.service';
import { Appointment } from 'app/shared/model/appointment.model';

describe('Component Tests', () => {
  describe('Appointment Management Update Component', () => {
    let comp: AppointmentUpdateComponent;
    let fixture: ComponentFixture<AppointmentUpdateComponent>;
    let service: AppointmentService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [AppointmentUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(AppointmentUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(AppointmentUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(AppointmentService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Appointment(123);
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
        const entity = new Appointment();
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
