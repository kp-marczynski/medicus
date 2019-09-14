import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Symptom } from 'app/shared/model/symptom.model';
import { SymptomService } from './symptom.service';
import { SymptomComponent } from './symptom.component';
import { SymptomDetailComponent } from './symptom-detail.component';
import { SymptomUpdateComponent } from './symptom-update.component';
import { SymptomDeletePopupComponent } from './symptom-delete-dialog.component';
import { ISymptom } from 'app/shared/model/symptom.model';

@Injectable({ providedIn: 'root' })
export class SymptomResolve implements Resolve<ISymptom> {
  constructor(private service: SymptomService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<ISymptom> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Symptom>) => response.ok),
        map((symptom: HttpResponse<Symptom>) => symptom.body)
      );
    }
    return of(new Symptom());
  }
}

export const symptomRoute: Routes = [
  {
    path: '',
    component: SymptomComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.symptom.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: SymptomDetailComponent,
    resolve: {
      symptom: SymptomResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.symptom.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: SymptomUpdateComponent,
    resolve: {
      symptom: SymptomResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.symptom.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: SymptomUpdateComponent,
    resolve: {
      symptom: SymptomResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.symptom.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const symptomPopupRoute: Routes = [
  {
    path: ':id/delete',
    component: SymptomDeletePopupComponent,
    resolve: {
      symptom: SymptomResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.symptom.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
