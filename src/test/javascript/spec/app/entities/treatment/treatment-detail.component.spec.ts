import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { TreatmentDetailComponent } from 'app/entities/treatment/treatment-detail.component';
import { Treatment } from 'app/shared/model/treatment.model';

describe('Component Tests', () => {
  describe('Treatment Management Detail Component', () => {
    let comp: TreatmentDetailComponent;
    let fixture: ComponentFixture<TreatmentDetailComponent>;
    const route = ({ data: of({ treatment: new Treatment(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [TreatmentDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(TreatmentDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(TreatmentDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.treatment).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
