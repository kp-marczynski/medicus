import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Doctor } from 'app/shared/model/doctor.model';
import { DoctorService } from './doctor.service';
import { DoctorComponent } from './doctor.component';
import { DoctorDetailComponent } from './doctor-detail.component';
import { DoctorUpdateComponent } from './doctor-update.component';
import { DoctorDeletePopupComponent } from './doctor-delete-dialog.component';
import { IDoctor } from 'app/shared/model/doctor.model';

@Injectable({ providedIn: 'root' })
export class DoctorResolve implements Resolve<IDoctor> {
  constructor(private service: DoctorService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IDoctor> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Doctor>) => response.ok),
        map((doctor: HttpResponse<Doctor>) => doctor.body)
      );
    }
    return of(new Doctor());
  }
}

export const doctorRoute: Routes = [
  {
    path: '',
    component: DoctorComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.doctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: DoctorDetailComponent,
    resolve: {
      doctor: DoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.doctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: DoctorUpdateComponent,
    resolve: {
      doctor: DoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.doctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: DoctorUpdateComponent,
    resolve: {
      doctor: DoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.doctor.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const doctorPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: DoctorDeletePopupComponent,
    resolve: {
      doctor: DoctorResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.doctor.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
