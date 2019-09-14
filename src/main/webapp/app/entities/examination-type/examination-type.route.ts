import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ExaminationType } from 'app/shared/model/examination-type.model';
import { ExaminationTypeService } from './examination-type.service';
import { ExaminationTypeComponent } from './examination-type.component';
import { ExaminationTypeDetailComponent } from './examination-type-detail.component';
import { ExaminationTypeUpdateComponent } from './examination-type-update.component';
import { ExaminationTypeDeletePopupComponent } from './examination-type-delete-dialog.component';
import { IExaminationType } from 'app/shared/model/examination-type.model';

@Injectable({ providedIn: 'root' })
export class ExaminationTypeResolve implements Resolve<IExaminationType> {
  constructor(private service: ExaminationTypeService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IExaminationType> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<ExaminationType>) => response.ok),
        map((examinationType: HttpResponse<ExaminationType>) => examinationType.body)
      );
    }
    return of(new ExaminationType());
  }
}

export const examinationTypeRoute: Routes = [
  {
    path: '',
    component: ExaminationTypeComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationType.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ExaminationTypeDetailComponent,
    resolve: {
      examinationType: ExaminationTypeResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationType.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ExaminationTypeUpdateComponent,
    resolve: {
      examinationType: ExaminationTypeResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationType.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ExaminationTypeUpdateComponent,
    resolve: {
      examinationType: ExaminationTypeResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationType.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const examinationTypePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ExaminationTypeDeletePopupComponent,
    resolve: {
      examinationType: ExaminationTypeResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationType.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
