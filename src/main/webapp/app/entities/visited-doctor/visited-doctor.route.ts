import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { VisitedDoctor } from 'app/shared/model/visited-doctor.model';
import { VisitedDoctorService } from './visited-doctor.service';
import { VisitedDoctorComponent } from './visited-doctor.component';
import { VisitedDoctorDetailComponent } from './visited-doctor-detail.component';
import { VisitedDoctorUpdateComponent } from './visited-doctor-update.component';
import { VisitedDoctorDeletePopupComponent } from './visited-doctor-delete-dialog.component';
import { IVisitedDoctor } from 'app/shared/model/visited-doctor.model';

@Injectable({ providedIn: 'root' })
export class VisitedDoctorResolve implements Resolve<IVisitedDoctor> {
  constructor(private service: VisitedDoctorService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IVisitedDoctor> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<VisitedDoctor>) => response.ok),
        map((visitedDoctor: HttpResponse<VisitedDoctor>) => visitedDoctor.body)
      );
    }
    return of(new VisitedDoctor());
  }
}

export const visitedDoctorRoute: Routes = [
  {
    path: '',
    component: VisitedDoctorComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.visitedDoctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: VisitedDoctorDetailComponent,
    resolve: {
      visitedDoctor: VisitedDoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.visitedDoctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: VisitedDoctorUpdateComponent,
    resolve: {
      visitedDoctor: VisitedDoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.visitedDoctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: VisitedDoctorUpdateComponent,
    resolve: {
      visitedDoctor: VisitedDoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.visitedDoctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const visitedDoctorPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: VisitedDoctorDeletePopupComponent,
    resolve: {
      visitedDoctor: VisitedDoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.visitedDoctor.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
