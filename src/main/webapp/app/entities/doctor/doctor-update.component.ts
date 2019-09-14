import { Component, OnInit } from '@angular/core';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { HttpResponse, HttpErrorResponse } from '@angular/common/http';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { IDoctor, Doctor } from 'app/shared/model/doctor.model';
import { DoctorService } from './doctor.service';

@Component({
  selector: 'jhi-doctor-update',
  templateUrl: './doctor-update.component.html'
})
export class DoctorUpdateComponent implements OnInit {
  isSaving: boolean;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    specialization: [null, [Validators.required]],
    language: []
  });

  constructor(protected doctorService: DoctorService, protected activatedRoute: ActivatedRoute, private fb: FormBuilder) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ doctor }) => {
      this.updateForm(doctor);
    });
  }

  updateForm(doctor: IDoctor) {
    this.editForm.patchValue({
      id: doctor.id,
      name: doctor.name,
      specialization: doctor.specialization,
      language: doctor.language
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const doctor = this.createFromForm();
    if (doctor.id !== undefined) {
      this.subscribeToSaveResponse(this.doctorService.update(doctor));
    } else {
      this.subscribeToSaveResponse(this.doctorService.create(doctor));
    }
  }

  private createFromForm(): IDoctor {
    return {
      ...new Doctor(),
      id: this.editForm.get(['id']).value,
      name: this.editForm.get(['name']).value,
      specialization: this.editForm.get(['specialization']).value,
      language: this.editForm.get(['language']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDoctor>>) {
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
