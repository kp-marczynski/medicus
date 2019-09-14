import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationTypeDetailComponent } from 'app/entities/examination-type/examination-type-detail.component';
import { ExaminationType } from 'app/shared/model/examination-type.model';

describe('Component Tests', () => {
  describe('ExaminationType Management Detail Component', () => {
    let comp: ExaminationTypeDetailComponent;
    let fixture: ComponentFixture<ExaminationTypeDetailComponent>;
    const route = ({ data: of({ examinationType: new ExaminationType(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationTypeDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ExaminationTypeDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ExaminationTypeDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.examinationType).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
