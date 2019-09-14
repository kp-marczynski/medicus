import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { JhiResolvePagingParams } from 'ng-jhipster';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { OwnedMedicine } from 'app/shared/model/owned-medicine.model';
import { OwnedMedicineService } from './owned-medicine.service';
import { OwnedMedicineComponent } from './owned-medicine.component';
import { OwnedMedicineDetailComponent } from './owned-medicine-detail.component';
import { OwnedMedicineUpdateComponent } from './owned-medicine-update.component';
import { OwnedMedicineDeletePopupComponent } from './owned-medicine-delete-dialog.component';
import { IOwnedMedicine } from 'app/shared/model/owned-medicine.model';

@Injectable({ providedIn: 'root' })
export class OwnedMedicineResolve implements Resolve<IOwnedMedicine> {
  constructor(private service: OwnedMedicineService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IOwnedMedicine> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<OwnedMedicine>) => response.ok),
        map((ownedMedicine: HttpResponse<OwnedMedicine>) => ownedMedicine.body)
      );
    }
    return of(new OwnedMedicine());
  }
}

export const ownedMedicineRoute: Routes = [
  {
    path: '',
    component: OwnedMedicineComponent,
    resolve: {
      pagingParams: JhiResolvePagingParams
    },
    data: {
      authorities: ['ROLE_USER'],
      defaultSort: 'id,asc',
      pageTitle: 'medicusApp.ownedMedicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: OwnedMedicineDetailComponent,
    resolve: {
      ownedMedicine: OwnedMedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.ownedMedicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: OwnedMedicineUpdateComponent,
    resolve: {
      ownedMedicine: OwnedMedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.ownedMedicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: OwnedMedicineUpdateComponent,
    resolve: {
      ownedMedicine: OwnedMedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.ownedMedicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const ownedMedicinePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: OwnedMedicineDeletePopupComponent,
    resolve: {
      ownedMedicine: OwnedMedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.ownedMedicine.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
