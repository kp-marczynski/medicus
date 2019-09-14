import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import * as moment from 'moment';
import { JhiAlertService } from 'ng-jhipster';
import { ISymptom, Symptom } from 'app/shared/model/symptom.model';
import { SymptomService } from './symptom.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IAppointment } from 'app/shared/model/appointment.model';
import { AppointmentService } from 'app/entities/appointment/appointment.service';

@Component({
  selector: 'jhi-symptom-update',
  templateUrl: './symptom-update.component.html'
})
export class SymptomUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  appointments: IAppointment[];
  startDateDp: any;
  endDateDp: any;

  editForm = this.fb.group({
    id: [],
    startDate: [null, [Validators.required]],
    endDate: [],
    description: [null, [Validators.required]],
    user: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected symptomService: SymptomService,
    protected userService: UserService,
    protected appointmentService: AppointmentService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ symptom }) => {
      this.updateForm(symptom);
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
  }

  updateForm(symptom: ISymptom) {
    this.editForm.patchValue({
      id: symptom.id,
      startDate: symptom.startDate,
      endDate: symptom.endDate,
      description: symptom.description,
      user: symptom.user
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const symptom = this.createFromForm();
    if (symptom.id !== undefined) {
      this.subscribeToSaveResponse(this.symptomService.update(symptom));
    } else {
      this.subscribeToSaveResponse(this.symptomService.create(symptom));
    }
  }

  private createFromForm(): ISymptom {
    return {
      ...new Symptom(),
      id: this.editForm.get(['id']).value,
      startDate: this.editForm.get(['startDate']).value,
      endDate: this.editForm.get(['endDate']).value,
      description: this.editForm.get(['description']).value,
      user: this.editForm.get(['user']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISymptom>>) {
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
