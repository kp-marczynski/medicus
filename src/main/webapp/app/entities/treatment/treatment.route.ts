import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Treatment } from 'app/shared/model/treatment.model';
import { TreatmentService } from './treatment.service';
import { TreatmentComponent } from './treatment.component';
import { TreatmentDetailComponent } from './treatment-detail.component';
import { TreatmentUpdateComponent } from './treatment-update.component';
import { TreatmentDeletePopupComponent } from './treatment-delete-dialog.component';
import { ITreatment } from 'app/shared/model/treatment.model';

@Injectable({ providedIn: 'root' })
export class TreatmentResolve implements Resolve<ITreatment> {
  constructor(private service: TreatmentService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ITreatment> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Treatment>) => response.ok),
        map((treatment: HttpResponse<Treatment>) => treatment.body)
      );
    }
    return of(new Treatment());
  }
}

export const treatmentRoute: Routes = [
  {
    path: '',
    component: TreatmentComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.treatment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: TreatmentDetailComponent,
    resolve: {
      treatment: TreatmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.treatment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: TreatmentUpdateComponent,
    resolve: {
      treatment: TreatmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.treatment.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: TreatmentUpdateComponent,
    resolve: {
      treatment: TreatmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.treatment.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const treatmentPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: TreatmentDeletePopupComponent,
    resolve: {
      treatment: TreatmentResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.treatment.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
