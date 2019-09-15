import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { ITreatment, Treatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from './treatment.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IMedicine } from 'app/shared/model/medicine.model';
import { MedicineService } from 'app/entities/medicine/medicine.service';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from 'app/entities/visited-doctor/visited-doctor.service';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment/appointment.service';

@Component({
  selector: 'jhi-treatment-update',
  templateUrl: './treatment-update.component.html'
})
export class TreatmentUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  medicines: IMedicine[];

  visiteddoctors: IVisitedDoctor[];

  appointments: IAppointment[];
  startDateDp: any;
  endDateDp: any;

  editForm = this.fb.group({
    id: [],
    startDate: [null, [Validators.required]],
    endDate: [],
    description: [null, [Validators.required]],
    descriptionScan: [],
    descriptionScanContentType: [],
    user: [],
    medicines: [],
    visitedDoctors: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected treatmentService: TreatmentService,
    protected userService: UserService,
    protected medicineService: MedicineService,
    protected visitedDoctorService: VisitedDoctorService,
    protected appointmentService: AppointmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ treatment }) => {
      this.updateForm(treatment);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.medicineService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IMedicine[]>) => mayBeOk.ok),
        map((response: HttpResponse<IMedicine[]>) => response.body)
      )
      .subscribe((res: IMedicine[]) => (this.medicines = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.visitedDoctorService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IVisitedDoctor[]>) => mayBeOk.ok),
        map((response: HttpResponse<IVisitedDoctor[]>) => response.body)
      )
      .subscribe((res: IVisitedDoctor[]) => (this.visiteddoctors = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.appointmentService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IAppointment[]>) => mayBeOk.ok),
        map((response: HttpResponse<IAppointment[]>) => response.body)
      )
      .subscribe((res: IAppointment[]) => (this.appointments = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(treatment: ITreatment) {
    this.editForm.patchValue({
      id: treatment.id,
      startDate: treatment.startDate,
      endDate: treatment.endDate,
      description: treatment.description,
      descriptionScan: treatment.descriptionScan,
      descriptionScanContentType: treatment.descriptionScanContentType,
      user: treatment.user,
      medicines: treatment.medicines,
      visitedDoctors: treatment.visitedDoctors
    });
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  setFileData(event, field: string, isImage) {
    return new Promise((resolve, reject) => {
      if (event && event.target && event.target.files && event.target.files[0]) {
        const file: File = event.target.files[0];
        if (isImage && !file.type.startsWith('image/')) {
          reject(`File was expected to be an image but was found to be ${file.type}`);
        } else {
          const filedContentType: string = field + 'ContentType';
          this.dataUtils.toBase64(file, base64Data => {
            this.editForm.patchValue({
              [field]: base64Data,
              [filedContentType]: file.type
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
    const treatment = this.createFromForm();
    if (treatment.id !== undefined) {
      this.subscribeToSaveResponse(this.treatmentService.update(treatment));
    } else {
      this.subscribeToSaveResponse(this.treatmentService.create(treatment));
    }
  }

  private createFromForm(): ITreatment {
    return {
      ...new Treatment(),
      id: this.editForm.get(['id']).value,
      startDate: this.editForm.get(['startDate']).value,
      endDate: this.editForm.get(['endDate']).value,
      description: this.editForm.get(['description']).value,
      descriptionScanContentType: this.editForm.get(['descriptionScanContentType']).value,
      descriptionScan: this.editForm.get(['descriptionScan']).value,
      user: this.editForm.get(['user']).value,
      medicines: this.editForm.get(['medicines']).value,
      visitedDoctors: this.editForm.get(['visitedDoctors']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ITreatment>>) {
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

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackMedicineById(index: number, item: IMedicine) {
    return item.id;
  }

  trackVisitedDoctorById(index: number, item: IVisitedDoctor) {
    return item.id;
  }

  trackAppointmentById(index: number, item: IAppointment) {
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
