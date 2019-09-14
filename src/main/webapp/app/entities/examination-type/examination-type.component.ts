import { Component, OnInit, OnDestroy } from '@angular/core';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { IExaminationType } from 'app/shared/model/examination-type.model';
import { AccountService } from 'app/core/auth/account.service';
import { ExaminationTypeService } from './examination-type.service';

@Component({
  selector: 'jhi-examination-type',
  templateUrl: './examination-type.component.html'
})
export class ExaminationTypeComponent implements OnInit, OnDestroy {
  examinationTypes: IExaminationType[];
  currentAccount: any;
  eventSubscriber: Subscription;

  constructor(
    protected examinationTypeService: ExaminationTypeService,
    protected jhiAlertService: JhiAlertService,
    protected eventManager: JhiEventManager,
    protected accountService: AccountService
  ) {}

  loadAll() {
    this.examinationTypeService
      .query()
      .pipe(
        filter((res: HttpResponse<IExaminationType[]>) => res.ok),
        map((res: HttpResponse<IExaminationType[]>) => res.body)
      )
      .subscribe(
        (res: IExaminationType[]) => {
          this.examinationTypes = res;
        },
        (res: HttpErrorResponse) => this.onError(res.message)
      );
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInExaminationTypes();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IExaminationType) {
    return item.id;
  }

  registerChangeInExaminationTypes() {
    this.eventSubscriber = this.eventManager.subscribe('examinationTypeListModification', response => this.loadAll());
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
