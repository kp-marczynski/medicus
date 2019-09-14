import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { SymptomDetailComponent } from 'app/entities/symptom/symptom-detail.component';
import { Symptom } from 'app/shared/model/symptom.model';

describe('Component Tests', () => {
  describe('Symptom Management Detail Component', () => {
    let comp: SymptomDetailComponent;
    let fixture: ComponentFixture<SymptomDetailComponent>;
    const route = ({ data: of({ symptom: new Symptom(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [SymptomDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(SymptomDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(SymptomDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.symptom).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
