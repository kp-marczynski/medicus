import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { MedicusTestModule } from '../../../test.module';
import { VisitedDoctorDetailComponent } from 'app/entities/visited-doctor/visited-doctor-detail.component';
import { VisitedDoctor } from 'app/shared/model/visited-doctor.model';

describe('Component Tests', () => {
  describe('VisitedDoctor Management Detail Component', () => {
    let comp: VisitedDoctorDetailComponent;
    let fixture: ComponentFixture<VisitedDoctorDetailComponent>;
    const route = ({ data: of({ visitedDoctor: new VisitedDoctor(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [VisitedDoctorDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }]
      })
        .overrideTemplate(VisitedDoctorDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(VisitedDoctorDetailComponent);
      comp = fixture.componentInstance;
    });

    describe('OnInit', () => {
      it('Should call load all on init', () => {
        // GIVEN

        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.visitedDoctor).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });
  });
});
