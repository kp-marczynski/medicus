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
import { IAppointment, Appointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from './appointment.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { ITreatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from 'app/entities/treatment/treatment.service';
import { ISymptom } from 'app/shared/model/symptom.model';
import { SymptomService } from 'app/entities/symptom/symptom.service';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from 'app/entities/visited-doctor/visited-doctor.service';

@Component({
  selector: 'jhi-appointment-update',
  templateUrl: './appointment-update.component.html'
})
export class AppointmentUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  treatments: ITreatment[];

  symptoms: ISymptom[];

  visiteddoctors: IVisitedDoctor[];
  dateDp: any;

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    title: [null, [Validators.required]],
    description: [],
    descriptionScan: [],
    descriptionScanContentType: [],
    user: [],
    treatments: [],
    symptoms: [],
    visitedDoctors: [],
    procedures: [],
    examinationPackages: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected appointmentService: AppointmentService,
    protected userService: UserService,
    protected treatmentService: TreatmentService,
    protected symptomService: SymptomService,
    protected visitedDoctorService: VisitedDoctorService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ appointment }) => {
      this.updateForm(appointment);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.treatmentService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ITreatment[]>) => mayBeOk.ok),
        map((response: HttpResponse<ITreatment[]>) => response.body)
      )
      .subscribe((res: ITreatment[]) => (this.treatments = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.symptomService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ISymptom[]>) => mayBeOk.ok),
        map((response: HttpResponse<ISymptom[]>) => response.body)
      )
      .subscribe((res: ISymptom[]) => (this.symptoms = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.visitedDoctorService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IVisitedDoctor[]>) => mayBeOk.ok),
        map((response: HttpResponse<IVisitedDoctor[]>) => response.body)
      )
      .subscribe((res: IVisitedDoctor[]) => (this.visiteddoctors = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(appointment: IAppointment) {
    this.editForm.patchValue({
      id: appointment.id,
      date: appointment.date,
      title: appointment.title,
      description: appointment.description,
      descriptionScan: appointment.descriptionScan,
      descriptionScanContentType: appointment.descriptionScanContentType,
      user: appointment.user,
      treatments: appointment.treatments,
      symptoms: appointment.symptoms,
      visitedDoctors: appointment.visitedDoctors,
      procedures: appointment.procedures,
      examinationPackages: appointment.examinationPackages
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
    const appointment = this.createFromForm();
    if (appointment.id !== undefined) {
      this.subscribeToSaveResponse(this.appointmentService.update(appointment));
    } else {
      this.subscribeToSaveResponse(this.appointmentService.create(appointment));
    }
  }

  private createFromForm(): IAppointment {
    return {
      ...new Appointment(),
      id: this.editForm.get(['id']).value,
      date: this.editForm.get(['date']).value,
      title: this.editForm.get(['title']).value,
      description: this.editForm.get(['description']).value,
      descriptionScanContentType: this.editForm.get(['descriptionScanContentType']).value,
      descriptionScan: this.editForm.get(['descriptionScan']).value,
      user: this.editForm.get(['user']).value,
      treatments: this.editForm.get(['treatments']).value,
      symptoms: this.editForm.get(['symptoms']).value,
      visitedDoctors: this.editForm.get(['visitedDoctors']).value,
      procedures: this.editForm.get(['procedures']).value,
      examinationPackages: this.editForm.get(['examinationPackages']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IAppointment>>) {
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

  trackTreatmentById(index: number, item: ITreatment) {
    return item.id;
  }

  trackSymptomById(index: number, item: ISymptom) {
    return item.id;
  }

  trackVisitedDoctorById(index: number, item: IVisitedDoctor) {
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
