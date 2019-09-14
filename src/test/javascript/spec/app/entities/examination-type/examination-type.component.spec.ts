import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { HttpHeaders, HttpResponse } from '@angular/common/http';

import { MedicusTestModule } from '../../../test.module';
import { ExaminationTypeComponent } from 'app/entities/examination-type/examination-type.component';
import { ExaminationTypeService } from 'app/entities/examination-type/examination-type.service';
import { ExaminationType } from 'app/shared/model/examination-type.model';

describe('Component Tests', () => {
  describe('ExaminationType Management Component', () => {
    let comp: ExaminationTypeComponent;
    let fixture: ComponentFixture<ExaminationTypeComponent>;
    let service: ExaminationTypeService;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [MedicusTestModule],
        declarations: [ExaminationTypeComponent],
        providers: []
      })
        .overrideTemplate(ExaminationTypeComponent, '')
        .compileComponents();

      fixture = TestBed.createComponent(ExaminationTypeComponent);
      comp = fixture.componentInstance;
      service = fixture.debugElement.injector.get(ExaminationTypeService);
    });

    it('Should call load all on init', () => {
      // GIVEN
      const headers = new HttpHeaders().append('link', 'link;link');
      spyOn(service, 'query').and.returnValue(
        of(
          new HttpResponse({
            body: [new ExaminationType(123)],
            headers
          })
        )
      );

      // WHEN
      comp.ngOnInit();

      // THEN
      expect(service.query).toHaveBeenCalled();
      expect(comp.examinationTypes[0]).toEqual(jasmine.objectContaining({ id: 123 }));
    });
  });
});
