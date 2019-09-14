import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { VisitedDoctorUpdateComponent } from 'app/entities/visited-doctor/visited-doctor-update.component';
import { VisitedDoctorService } from 'app/entities/visited-doctor/visited-doctor.service';
import { VisitedDoctor } from 'app/shared/model/visited-doctor.model';

describe('Component Tests', () => {
  describe('VisitedDoctor Management Update Component', () => {
    let comp: VisitedDoctorUpdateComponent;
    let fixture: ComponentFixture<VisitedDoctorUpdateComponent>;
    let service: VisitedDoctorService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [VisitedDoctorUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(VisitedDoctorUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(VisitedDoctorUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(VisitedDoctorService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new VisitedDoctor(123);
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
        const entity = new VisitedDoctor();
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
