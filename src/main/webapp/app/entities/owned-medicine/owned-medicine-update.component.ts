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
import { IOwnedMedicine, OwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { OwnedMedicineService } from './owned-medicine.service';
import { IUser } from 'app/core/user/user.model';
import { UserService } from 'app/core/user/user.service';
import { IMedicine } from 'app/shared/model/medicine.model';
import { MedicineService } from 'app/entities/medicine/medicine.service';

@Component({
  selector: 'jhi-owned-medicine-update',
  templateUrl: './owned-medicine-update.component.html'
})
export class OwnedMedicineUpdateComponent implements OnInit {
  isSaving: boolean;

  users: IUser[];

  medicines: IMedicine[];
  expirationDateDp: any;

  editForm = this.fb.group({
    id: [],
    doses: [null, [Validators.required]],
    expirationDate: [null, [Validators.required]],
    user: [],
    medicines: []
  });

  constructor(
    protected jhiAlertService: JhiAlertService,
    protected ownedMedicineService: OwnedMedicineService,
    protected userService: UserService,
    protected medicineService: MedicineService,
    protected activatedRoute: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit() {
    this.isSaving = false;
    this.activatedRoute.data.subscribe(({ ownedMedicine }) => {
      this.updateForm(ownedMedicine);
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
  }

  updateForm(ownedMedicine: IOwnedMedicine) {
    this.editForm.patchValue({
      id: ownedMedicine.id,
      doses: ownedMedicine.doses,
      expirationDate: ownedMedicine.expirationDate,
      user: ownedMedicine.user,
      medicines: ownedMedicine.medicines
    });
  }

  previousState() {
    window.history.back();
  }

  save() {
    this.isSaving = true;
    const ownedMedicine = this.createFromForm();
    if (ownedMedicine.id !== undefined) {
      this.subscribeToSaveResponse(this.ownedMedicineService.update(ownedMedicine));
    } else {
      this.subscribeToSaveResponse(this.ownedMedicineService.create(ownedMedicine));
    }
  }

  private createFromForm(): IOwnedMedicine {
    return {
      ...new OwnedMedicine(),
      id: this.editForm.get(['id']).value,
      doses: this.editForm.get(['doses']).value,
      expirationDate: this.editForm.get(['expirationDate']).value,
      user: this.editForm.get(['user']).value,
      medicines: this.editForm.get(['medicines']).value
    };
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOwnedMedicine>>) {
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
