import { Component, OnInit, OnDestroy, Input } from '@angular/core';
import { HttpErrorResponse, HttpHeaders, HttpResponse } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
// eslint-disable-next-line @typescript-eslint/no-unused-vars
import { filter, map } from 'rxjs/operators';
import { JhiEventManager, JhiParseLinks, JhiAlertService, JhiDataUtils } from 'ng-jhipster';

import { IExaminationPackage } from 'app/shared/model/examination-package.model';
import { AccountService } from 'app/core/auth/account.service';

import { ITEMS_PER_PAGE } from 'app/shared/constants/pagination.constants';
import { ExaminationPackageService } from './examination-package.service';

@Component({
  selector: 'jhi-examination-package',
  templateUrl: './examination-package.component.html'
})
export class ExaminationPackageComponent implements OnInit, OnDestroy {
  currentAccount: any;
  standaloneView: boolean;
  @Input() examinationPackages: IExaminationPackage[];
  error: any;
  success: any;
  eventSubscriber: Subscription;
  routeData: any;
  links: any;
  totalItems: any;
  itemsPerPage: any;
  page: any;
  predicate: any;
  previousPage: any;
  reverse: any;

  constructor(
    protected examinationPackageService: ExaminationPackageService,
    protected parseLinks: JhiParseLinks,
    protected jhiAlertService: JhiAlertService,
    protected accountService: AccountService,
    protected activatedRoute: ActivatedRoute,
    protected dataUtils: JhiDataUtils,
    protected router: Router,
    protected eventManager: JhiEventManager
  ) {
    this.itemsPerPage = ITEMS_PER_PAGE;
    this.routeData = this.activatedRoute.data.subscribe(data => {
      if (data.pagingParams) {
        this.standaloneView = true;
        this.page = data.pagingParams.page;
        this.previousPage = data.pagingParams.page;
        this.reverse = data.pagingParams.ascending;
        this.predicate = data.pagingParams.predicate;
      } else {
        this.standaloneView = false;
        this.page = 1;
        this.previousPage = 1;
        this.reverse = true;
        this.predicate = 'id';
      }
    });
  }

  loadAll() {
    if(this.standaloneView) {
      this.examinationPackageService
        .query({
          page: this.page - 1,
          size: this.itemsPerPage,
          sort: this.sort()
        })
        .subscribe(
          (res: HttpResponse<IExaminationPackage[]>) => this.paginateExaminationPackages(res.body, res.headers),
          (res: HttpErrorResponse) => this.onError(res.message)
        );
    }
  }

  loadPage(page: number) {
    if (page !== this.previousPage) {
      this.previousPage = page;
      this.transition();
    }
  }

  transition() {
    this.router.navigate(['/examination-package'], {
      queryParams: {
        page: this.page,
        size: this.itemsPerPage,
        sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
      }
    });
    this.loadAll();
  }

  clear() {
    this.page = 0;
    this.router.navigate([
      '/examination-package',
      {
        page: this.page,
        sort: this.predicate + ',' + (this.reverse ? 'asc' : 'desc')
      }
    ]);
    this.loadAll();
  }

  ngOnInit() {
    this.loadAll();
    this.accountService.identity().then(account => {
      this.currentAccount = account;
    });
    this.registerChangeInExaminationPackages();
  }

  ngOnDestroy() {
    this.eventManager.destroy(this.eventSubscriber);
  }

  trackId(index: number, item: IExaminationPackage) {
    return item.id;
  }

  byteSize(field) {
    return this.dataUtils.byteSize(field);
  }

  openFile(contentType, field) {
    return this.dataUtils.openFile(contentType, field);
  }

  registerChangeInExaminationPackages() {
    this.eventSubscriber = this.eventManager.subscribe('examinationPackageListModification', response => this.loadAll());
  }

  sort() {
    const result = [this.predicate + ',' + (this.reverse ? 'asc' : 'desc')];
    if (this.predicate !== 'id') {
      result.push('id');
    }
    return result;
  }

  protected paginateExaminationPackages(data: IExaminationPackage[], headers: HttpHeaders) {
    this.links = this.parseLinks.parse(headers.get('link'));
    this.totalItems = parseInt(headers.get('X-Total-Count'), 10);
    this.examinationPackages = data;
  }

  protected onError(errorMessage: string) {
    this.jhiAlertService.error(errorMessage, null, null);
  }
}
