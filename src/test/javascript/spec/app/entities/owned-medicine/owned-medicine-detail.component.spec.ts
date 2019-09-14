import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { OwnedMedicineDetailComponent } from 'app/entities/owned-medicine/owned-medicine-detail.component';
import { OwnedMedicine } from 'app/shared/model/owned-medicine.model';

describe('Component Tests', () => {
  describe('OwnedMedicine Management Detail Component', () => {
    let comp: OwnedMedicineDetailComponent;
    let fixture: ComponentFixture<OwnedMedicineDetailComponent>;
    const route = ({ data: of({ ownedMedicine: new OwnedMedicine(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [OwnedMedicineDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(OwnedMedicineDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(OwnedMedicineDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.ownedMedicine).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
