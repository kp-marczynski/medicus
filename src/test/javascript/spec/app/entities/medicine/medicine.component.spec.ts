import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { MedicusTestModule } from '../../../test.module';
import { MedicineComponent } from 'app/entities/medicine/medicine.component';
import { MedicineService } from 'app/entities/medicine/medicine.service';
import { Medicine } from 'app/shared/model/medicine.model';

describe('Component Tests', () => {
  describe('Medicine Management Component', () => {
    let comp: MedicineComponent;
    let fixture: ComponentFixture<MedicineComponent>;
    let service: MedicineService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [MedicineComponent],
        providers: []
      })
        .overrideTemplate(MedicineComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(MedicineComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(MedicineService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new Medicine(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.medicines[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
