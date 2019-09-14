import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ProcedureUpdateComponent } from 'app/entities/procedure/procedure-update.component';
import { ProcedureService } from 'app/entities/procedure/procedure.service';
import { Procedure } from 'app/shared/model/procedure.model';

describe('Component Tests', () => {
  describe('Procedure Management Update Component', () => {
    let comp: ProcedureUpdateComponent;
    let fixture: ComponentFixture<ProcedureUpdateComponent>;
    let service: ProcedureService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ProcedureUpdateComponent],
        providers: [FormBuilder]
      })
        .overrideTemplate(ProcedureUpdateComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ProcedureUpdateComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ProcedureService);
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', fakeAsync(() => {
        // GIVEN
        const entity = new Procedure(123);
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
        const entity = new Procedure();
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
