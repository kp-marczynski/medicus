import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Procedure } from 'app/shared/model/procedure.model';
import { ProcedureService } from './procedure.service';
import { ProcedureComponent } from './procedure.component';
import { ProcedureDetailComponent } from './procedure-detail.component';
import { ProcedureUpdateComponent } from './procedure-update.component';
import { ProcedureDeletePopupComponent } from './procedure-delete-dialog.component';
import { IProcedure } from 'app/shared/model/procedure.model';

@Injectable({ providedIn: 'root' })
export class ProcedureResolve implements Resolve<IProcedure> {
  constructor(private service: ProcedureService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IProcedure> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Procedure>) => response.ok),
        map((procedure: HttpResponse<Procedure>) => procedure.body)
      );
    }
    return of(new Procedure());
  }
}

export const procedureRoute: Routes = [
  {
    path: '',
    component: ProcedureComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.procedure.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: ProcedureDetailComponent,
    resolve: {
      procedure: ProcedureResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.procedure.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: ProcedureUpdateComponent,
    resolve: {
      procedure: ProcedureResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.procedure.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: ProcedureUpdateComponent,
    resolve: {
      procedure: ProcedureResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.procedure.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const procedurePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: ProcedureDeletePopupComponent,
    resolve: {
      procedure: ProcedureResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.procedure.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
