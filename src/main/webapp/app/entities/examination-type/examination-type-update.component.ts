import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IExaminationType, ExaminationType } from 'app/shared/model/examination-type.model';
import { ExaminationTypeService } from './examination-type.service';

@Component({
  selector: 'jhi-examination-type-update',
  templateUrl: './examination-type-update.component.html'
})
export class ExaminationTypeUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    unit: [],
    minValue: [],
    maxValue: [],
    user: []
  });

  constructor(
    protected examinationTypeService: ExaminationTypeService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ examinationType }) => {
      this.updateForm(examinationType);
    });
  }

  updateForm(examinationType: IExaminationType) {
    this.editForm.patchValue({
      id: examinationType.id,
      name: examinationType.name,
      unit: examinationType.unit,
      minValue: examinationType.minValue,
      maxValue: examinationType.maxValue,
      user: examinationType.user
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const examinationType = this.createFromForm();
    if (examinationType.id !== undefined) {
      this.subscribeToSaveResponse(this.examinationTypeService.update(examinationType));
    } else {
      this.subscribeToSaveResponse(this.examinationTypeService.create(examinationType));
    }
  }

  private createFromForm(): IExaminationType {
    return {
      ...new ExaminationType(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      unit: this.editForm.get(['unit']).value,
      minValue: this.editForm.get(['minValue']).value,
      maxValue: this.editForm.get(['maxValue']).value,
      user: this.editForm.get(['user']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IExaminationType>>) {
    result.subscribe(() => this.onSaveSuccess(), () => this.onSaveError());
  }

  protected onSaveSuccess() {
    this.isSaving = false;
    this.previousState();
  }

  protected onSaveError() {
    this.isSaving = false;
  }
}
