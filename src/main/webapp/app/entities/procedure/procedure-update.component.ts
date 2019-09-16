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
import { IProcedure, Procedure } from 'app/shared/model/procedure.model';
import { ProcedureService } from './procedure.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from 'app/entities/visited-doctor/visited-doctor.service';
import { Appointment, IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment/appointment.service';
import {IExaminationPackage} from "app/shared/model/examination-package.model";

@Component({
  selector: 'jhi-procedure-update',
  templateUrl: './procedure-update.component.html'
})
export class ProcedureUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  visiteddoctors: IVisitedDoctor[];

  appointments: IAppointment[];
  dateDp: any;

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    description: [null, [Validators.required]],
    descriptionScan: [],
    descriptionScanContentType: [],
    user: [],
    visitedDoctors: [],
    appointment: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected procedureService: ProcedureService,
    protected userService: UserService,
    protected visitedDoctorService: VisitedDoctorService,
    protected appointmentService: AppointmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ procedure }) => {
      this.updateForm(procedure);
    });
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
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

  updateForm(procedure: IProcedure) {
    this.editForm.patchValue({
      id: procedure.id,
      date: procedure.date,
      description: procedure.description,
      descriptionScan: procedure.descriptionScan,
      descriptionScanContentType: procedure.descriptionScanContentType,
      user: procedure.user,
      visitedDoctors: procedure.visitedDoctors,
      appointment: procedure.appointment ? new Appointment(procedure.appointment) : procedure.appointment
    });
    if (procedure.appointment) {
      this.updateAppointment(procedure);
    }
  }

  updateAppointment(procedure: IProcedure) {
    if (Object.prototype.hasOwnProperty.call(procedure.appointment, 'id')) {
      this.editForm.patchValue({ appointment: procedure.appointment });
    } else {
      setTimeout(() => this.updateAppointment(procedure), 100);
    }
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
    const procedure = this.createFromForm();
    if (procedure.appointment) {
      procedure.appointment = procedure.appointment.id;
    }
    if (procedure.id !== undefined) {
      this.subscribeToSaveResponse(this.procedureService.update(procedure));
    } else {
      this.subscribeToSaveResponse(this.procedureService.create(procedure));
    }
  }

  private createFromForm(): IProcedure {
    return {
      ...new Procedure(),
      id: this.editForm.get(['id']).value,
      date: this.editForm.get(['date']).value,
      description: this.editForm.get(['description']).value,
      descriptionScanContentType: this.editForm.get(['descriptionScanContentType']).value,
      descriptionScan: this.editForm.get(['descriptionScan']).value,
      user: this.editForm.get(['user']).value,
      visitedDoctors: this.editForm.get(['visitedDoctors']).value,
      appointment: this.editForm.get(['appointment']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IProcedure>>) {
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
