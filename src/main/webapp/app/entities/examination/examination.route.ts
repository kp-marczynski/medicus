import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Examination } from 'app/shared/model/examination.model';
import { ExaminationService } from './examination.service';
import { ExaminationComponent } from './examination.component';
import { ExaminationDetailComponent } from './examination-detail.component';
import { ExaminationUpdateComponent } from './examination-update.component';
import { ExaminationDeletePopupComponent } from './examination-delete-dialog.component';
import { IExamination } from 'app/shared/model/examination.model';

@Injectable({ providedIn: 'root' })
export class ExaminationResolve implements Resolve<IExamination> {
  constructor(private service: ExaminationService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IExamination> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Examination>) => response.ok),
        map((examination: HttpResponse<Examination>) => examination.body)
      );
    }
    return of(new Examination());
  }
}

export const examinationRoute: Routes = [
  {
    path: '',
    component: ExaminationComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.examination.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ExaminationDetailComponent,
    resolve: {
      examination: ExaminationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examination.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ExaminationUpdateComponent,
    resolve: {
      examination: ExaminationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examination.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ExaminationUpdateComponent,
    resolve: {
      examination: ExaminationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examination.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const examinationPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ExaminationDeletePopupComponent,
    resolve: {
      examination: ExaminationResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examination.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
