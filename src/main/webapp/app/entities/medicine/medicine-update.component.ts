import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IMedicine, Medicine } from 'app/shared/model/medicine.model';
import { MedicineService } from './medicine.service';
import { ITreatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from 'app/entities/treatment/treatment.service';
import { ContentFile } from 'app/shared/model/file.model';

@Component({
  selector: 'jhi-medicine-update',
  templateUrl: './medicine-update.component.html'
})
export class MedicineUpdateComponent implements OnInit {
  isSaving: boolean;

  treatments: ITreatment[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    indication: [],
    leaflet: this.fb.group({
      id: [],
      content: [],
      contentContentType: []
    }),
    language: [],
    user: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected medicineService: MedicineService,
    protected treatmentService: TreatmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ medicine }) => {
      this.updateForm(medicine);
    });
    this.treatmentService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ITreatment[]>) => mayBeOk.ok),
        map((response: HttpResponse<ITreatment[]>) => response.body)
      )
      .subscribe((res: ITreatment[]) => (this.treatments = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(medicine: IMedicine) {
    this.editForm.patchValue({
      id: medicine.id,
      name: medicine.name,
      indication: medicine.indication,
      leaflet: medicine.leaflet ? medicine.leaflet : new ContentFile(),
      user: medicine.user
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.get('leaflet').patchValue({
              content: base64Data,
              contentContentType: file.type
            });
          });
        }
      } else {
        reject(`Base64 data was not set as file could not be extracted from passed parameter: ${event}`);
      }
    }).then(
      // eslint-disable-next-line no-console
      () => console.log('blob added'), // success
      this.onError
    );
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const medicine = this.createFromForm();
    if (medicine.id !== undefined) {
      this.subscribeToSaveResponse(this.medicineService.update(medicine));
    } else {
      this.subscribeToSaveResponse(this.medicineService.create(medicine));
    }
  }

  private createFromForm(): IMedicine {
    return {
      ...new Medicine(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      indication: this.editForm.get(['indication']).value,
      leaflet: this.editForm.get(['leaflet']).value,
      user: this.editForm.get(['user']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMedicine>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }

  trackTreatmentById(index: number, item: ITreatment) {
    return item.id;
  }

  getSelected(selectedVals: any[], option: any) {
    if (selectedVals) {
      for (let i = 0; i < selectedVals.length; i++) {
        if (option.id === selectedVals[i].id) {
          return selectedVals[i];
        }
      }
    }
    return option;
  }
}
