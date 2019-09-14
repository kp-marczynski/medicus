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
import { IExaminationPackage, ExaminationPackage } from 'app/shared/model/examination-package.model';
import { ExaminationPackageService } from './examination-package.service';
import { IExamination } from 'app/shared/model/examination.model';
import { ExaminationService } from 'app/entities/examination/examination.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment/appointment.service';

@Component({
  selector: 'jhi-examination-package-update',
  templateUrl: './examination-package-update.component.html'
})
export class ExaminationPackageUpdateComponent implements OnInit {
  isSaving: boolean;

  examinations: IExamination[];

  users: IUser[];

  appointments: IAppointment[];
  dateDp: any;

  editForm = this.fb.group({
    id: [],
    date: [null, [Validators.required]],
    title: [null, [Validators.required]],
    examinationPackageScan: [],
    examinationPackageScanContentType: [],
    examination: [],
    user: [],
    appointment: []
  });

  constructor(
    protected dataUtils: JhiDataUtils,
    protected jhiAlertService: JhiAlertService,
    protected examinationPackageService: ExaminationPackageService,
    protected examinationService: ExaminationService,
    protected userService: UserService,
    protected appointmentService: AppointmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ examinationPackage }) => {
      this.updateForm(examinationPackage);
    });
    this.examinationService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IExamination[]>) => mayBeOk.ok),
        map((response: HttpResponse<IExamination[]>) => response.body)
      )
      .subscribe((res: IExamination[]) => (this.examinations = res), (res: HttpErrorResponse) => this.onError(res.message));
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
  }

  updateForm(examinationPackage: IExaminationPackage) {
    this.editForm.patchValue({
      id: examinationPackage.id,
      date: examinationPackage.date,
      title: examinationPackage.title,
      examinationPackageScan: examinationPackage.examinationPackageScan,
      examinationPackageScanContentType: examinationPackage.examinationPackageScanContentType,
      examination: examinationPackage.examination,
      user: examinationPackage.user,
      appointment: examinationPackage.appointment
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
    const examinationPackage = this.createFromForm();
    if (examinationPackage.id !== undefined) {
      this.subscribeToSaveResponse(this.examinationPackageService.update(examinationPackage));
    } else {
      this.subscribeToSaveResponse(this.examinationPackageService.create(examinationPackage));
    }
  }

  private createFromForm(): IExaminationPackage {
    return {
      ...new ExaminationPackage(),
      id: this.editForm.get(['id']).value,
      date: this.editForm.get(['date']).value,
      title: this.editForm.get(['title']).value,
      examinationPackageScanContentType: this.editForm.get(['examinationPackageScanContentType']).value,
      examinationPackageScan: this.editForm.get(['examinationPackageScan']).value,
      examination: this.editForm.get(['examination']).value,
      user: this.editForm.get(['user']).value,
      appointment: this.editForm.get(['appointment']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExaminationPackage>>) {
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

  trackExaminationById(index: number, item: IExamination) {
    return item.id;
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackAppointmentById(index: number, item: IAppointment) {
    return item.id;
  }
}
