import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { JhiAlertService } from 'ng-jhipster';
import { IExamination, Examination } from 'app/shared/model/examination.model';
import { ExaminationService } from './examination.service';
import { IExaminationType } from 'app/shared/model/examination-type.model';
import { ExaminationTypeService } from 'app/entities/examination-type/examination-type.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { ExaminationPackageService } from 'app/entities/examination-package/examination-package.service';

@Component({
  selector: 'jhi-examination-update',
  templateUrl: './examination-update.component.html'
})
export class ExaminationUpdateComponent implements OnInit {
  isSaving: boolean;

  examinationtypes: IExaminationType[];

  users: IUser[];

  examinationpackages: IExaminationPackage[];

  editForm = this.fb.group({
    id: [],
    value: [null, [Validators.required]],
    valueModificator: [],
    examinationType: [],
    user: [],
    examinationPackage: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected examinationService: ExaminationService,
    protected examinationTypeService: ExaminationTypeService,
    protected userService: UserService,
    protected examinationPackageService: ExaminationPackageService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ examination }) => {
      this.updateForm(examination);
    });
    this.examinationTypeService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IExaminationType[]>) => mayBeOk.ok),
        map((response: HttpResponse<IExaminationType[]>) => response.body)
      )
      .subscribe((res: IExaminationType[]) => (this.examinationtypes = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.userService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IUser[]>) => mayBeOk.ok),
        map((response: HttpResponse<IUser[]>) => response.body)
      )
      .subscribe((res: IUser[]) => (this.users = res), (res: HttpErrorResponse) => this.onError(res.message));
    this.examinationPackageService
      .query()
      .pipe(
        filter((mayBeOk: HttpResponse<IExaminationPackage[]>) => mayBeOk.ok),
        map((response: HttpResponse<IExaminationPackage[]>) => response.body)
      )
      .subscribe((res: IExaminationPackage[]) => (this.examinationpackages = res), (res: HttpErrorResponse) => this.onError(res.message));
  }

  updateForm(examination: IExamination) {
    this.editForm.patchValue({
      id: examination.id,
      value: examination.value,
      valueModificator: examination.valueModificator,
      examinationType: examination.examinationType,
      user: examination.user,
      examinationPackage: examination.examinationPackage
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const examination = this.createFromForm();
    if (examination.examinationPackage && examination.examinationPackage.appointment) {
      examination.examinationPackage.appointment = examination.examinationPackage.appointment.id;
    }
    if (examination.id !== undefined) {
      this.subscribeToSaveResponse(this.examinationService.update(examination));
    } else {
      this.subscribeToSaveResponse(this.examinationService.create(examination));
    }
  }

  private createFromForm(): IExamination {
    return {
      ...new Examination(),
      id: this.editForm.get(['id']).value,
      value: this.editForm.get(['value']).value,
      valueModificator: this.editForm.get(['valueModificator']).value,
      examinationType: this.editForm.get(['examinationType']).value,
      user: this.editForm.get(['user']).value,
      examinationPackage: this.editForm.get(['examinationPackage']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExamination>>) {
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

  trackExaminationTypeById(index: number, item: IExaminationType) {
    return item.id;
  }

  trackUserById(index: number, item: IUser) {
    return item.id;
  }

  trackExaminationPackageById(index: number, item: IExaminationPackage) {
    return item.id;
  }
}
