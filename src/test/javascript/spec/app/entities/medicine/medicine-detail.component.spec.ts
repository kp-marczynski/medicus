import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { MedicineDetailComponent } from 'app/entities/medicine/medicine-detail.component';
import { Medicine } from 'app/shared/model/medicine.model';

describe('Component Tests', () => {
  describe('Medicine Management Detail Component', () => {
    let comp: MedicineDetailComponent;
    let fixture: ComponentFixture<MedicineDetailComponent>;
    const route = ({ data: of({ medicine: new Medicine(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [MedicineDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(MedicineDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(MedicineDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.medicine).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
