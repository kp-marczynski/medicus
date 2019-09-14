import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { ExaminationPackage } from 'app/shared/model/examination-package.model';
import { ExaminationPackageService } from './examination-package.service';
import { ExaminationPackageComponent } from './examination-package.component';
import { ExaminationPackageDetailComponent } from './examination-package-detail.component';
import { ExaminationPackageUpdateComponent } from './examination-package-update.component';
import { ExaminationPackageDeletePopupComponent } from './examination-package-delete-dialog.component';
import { IExaminationPackage } from 'app/shared/model/examination-package.model';

@Injectable({ providedIn: 'root' })
export class ExaminationPackageResolve implements Resolve<IExaminationPackage> {
  constructor(private service: ExaminationPackageService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IExaminationPackage> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<ExaminationPackage>) => response.ok),
        map((examinationPackage: HttpResponse<ExaminationPackage>) => examinationPackage.body)
      );
    }
    return of(new ExaminationPackage());
  }
}

export const examinationPackageRoute: Routes = [
  {
    path: '',
    component: ExaminationPackageComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.examinationPackage.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ExaminationPackageDetailComponent,
    resolve: {
      examinationPackage: ExaminationPackageResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationPackage.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ExaminationPackageUpdateComponent,
    resolve: {
      examinationPackage: ExaminationPackageResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationPackage.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ExaminationPackageUpdateComponent,
    resolve: {
      examinationPackage: ExaminationPackageResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationPackage.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const examinationPackagePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ExaminationPackageDeletePopupComponent,
    resolve: {
      examinationPackage: ExaminationPackageResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.examinationPackage.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
