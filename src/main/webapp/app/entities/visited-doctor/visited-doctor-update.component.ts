import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService, JhiDataUtils } from 'ng-jhipster';
import { IVisitedDoctor, VisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from './visited-doctor.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment/appointment.service';
import { IProcedure } from 'app/shared/model/procedure.model';
import { ProcedureService } from 'app/entities/procedure/procedure.service';
import { ITreatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from 'app/entities/treatment/treatment.service';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { ExaminationPackageService } from 'app/entities/examination-package/examination-package.service';

@Component({
  selector: 'jhi-visited-doctor-update',
  templateUrl: './visited-doctor-update.component.html'
})
export class VisitedDoctorUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  appointments: IAppointment[];

  procedures: IProcedure[];

  treatments: ITreatment[];

  examinationpackages: IExaminationPackage[];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    specialization: [null, [Validators.required]],
    opinion: [],
    user: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected visitedDoctorService: VisitedDoctorService,
    protected userService: UserService,
    protected appointmentService: AppointmentService,
    protected procedureService: ProcedureService,
    protected treatmentService: TreatmentService,
    protected examinationPackageService: ExaminationPackageService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ visitedDoctor }) => {
      this.updateForm(visitedDoctor);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.appointmentService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IAppointment[]>) => mayBeOk.ok),
        map((response: HttpResponse<IAppointment[]>) => response.body)
      )
      .subscribe((res: IAppointment[]) => (this.appointments = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.procedureService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IProcedure[]>) => mayBeOk.ok),
        map((response: HttpResponse<IProcedure[]>) => response.body)
      )
      .subscribe((res: IProcedure[]) => (this.procedures = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.treatmentService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<ITreatment[]>) => mayBeOk.ok),
        map((response: HttpResponse<ITreatment[]>) => response.body)
      )
      .subscribe((res: ITreatment[]) => (this.treatments = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.examinationPackageService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IExaminationPackage[]>) => mayBeOk.ok),
        map((response: HttpResponse<IExaminationPackage[]>) => response.body)
      )
      .subscribe((res: IExaminationPackage[]) => (this.examinationpackages = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(visitedDoctor: IVisitedDoctor) {
    this.editForm.patchValue({
      id: visitedDoctor.id,
      name: visitedDoctor.name,
      specialization: visitedDoctor.specialization,
      opinion: visitedDoctor.opinion,
      user: visitedDoctor.user
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
    const visitedDoctor = this.createFromForm();
    if (visitedDoctor.id !== undefined) {
      this.subscribeToSaveResponse(this.visitedDoctorService.update(visitedDoctor));
    } else {
      this.subscribeToSaveResponse(this.visitedDoctorService.create(visitedDoctor));
    }
  }

  private createFromForm(): IVisitedDoctor {
    return {
      ...new VisitedDoctor(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      specialization: this.editForm.get(['specialization']).value,
      opinion: this.editForm.get(['opinion']).value,
      user: this.editForm.get(['user']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IVisitedDoctor>>) {
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

  trackAppointmentById(index: number, item: IAppointment) {
    return item.id;
  }

  trackProcedureById(index: number, item: IProcedure) {
    return item.id;
  }

  trackTreatmentById(index: number, item: ITreatment) {
    return item.id;
  }

  trackExaminationPackageById(index: number, item: IExaminationPackage) {
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
