import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationPackageDetailComponent } from 'app/entities/examination-package/examination-package-detail.component';
import { ExaminationPackage } from 'app/shared/model/examination-package.model';

describe('Component Tests', () => {
  describe('ExaminationPackage Management Detail Component', () => {
    let comp: ExaminationPackageDetailComponent;
    let fixture: ComponentFixture<ExaminationPackageDetailComponent>;
    const route = ({ data: of({ examinationPackage: new ExaminationPackage(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationPackageDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(ExaminationPackageDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ExaminationPackageDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.examinationPackage).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
