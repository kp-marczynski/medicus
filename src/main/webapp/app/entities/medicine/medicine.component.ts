import {Component, OnInit, OnDestroy, Input} from '@angular/core';
import {HttpErrorResponse, HttpResponse} from '@angular/common/http';
import {Subscription} from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import {filter, map} from 'rxjs/operators';
import {JhiEventManager, JhiAlertService, JhiDataUtils} from 'ng-jhipster';

import {IMedicine} from 'app/shared/model/medicine.model';
import {AccountService} from 'app/core/auth/account.service';
import {MedicineService} from './medicine.service';

@Component({
  selector: 'jhi-medicine',
  templateUrl: './medicine.component.html'
})
export class MedicineComponent implements OnInit, OnDestroy {
  standaloneView: boolean;
  @Input() medicines: IMedicine[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected medicineService: MedicineService,
    protected jhiAlertService: JhiAlertService,
    protected dataUtils: JhiDataUtils,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {
  }

  loadAll() {
    this.medicineService
      .query()
      .pipe(
        filter((res: HttpResponse<IMedicine[]>) => res.ok),
        map((res: HttpResponse<IMedicine[]>) => res.body)
      )
      .subscribe(
        (res: IMedicine[]) => {
          this.medicines = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    if (!this.medicines || this.medicines.length === 0) {
      this.standaloneView = true;
      this.loadAll();
    } else {
      this.standaloneView = false;
    }
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInMedicines();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IMedicine) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInMedicines() {
    this.eventSubscriber = this.eventManager.subscribe('medicineListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
