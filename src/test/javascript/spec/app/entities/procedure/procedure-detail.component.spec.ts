import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ProcedureDetailComponent } from 'app/entities/procedure/procedure-detail.component';
import { Procedure } from 'app/shared/model/procedure.model';

describe('Component Tests', () => {
  describe('Procedure Management Detail Component', () => {
    let comp: ProcedureDetailComponent;
    let fixture: ComponentFixture<ProcedureDetailComponent>;
    const route = ({ data: of({ procedure: new Procedure(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ProcedureDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ProcedureDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ProcedureDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.procedure).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
