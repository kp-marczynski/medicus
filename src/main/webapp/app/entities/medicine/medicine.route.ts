import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core/auth/user-route-access-service';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Medicine } from 'app/shared/model/medicine.model';
import { MedicineService } from './medicine.service';
import { MedicineComponent } from './medicine.component';
import { MedicineDetailComponent } from './medicine-detail.component';
import { MedicineUpdateComponent } from './medicine-update.component';
import { MedicineDeletePopupComponent } from './medicine-delete-dialog.component';
import { IMedicine } from 'app/shared/model/medicine.model';

@Injectable({ providedIn: 'root' })
export class MedicineResolve implements Resolve<IMedicine> {
  constructor(private service: MedicineService) {}

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<IMedicine> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        filter((response: HttpResponse<Medicine>) => response.ok),
        map((medicine: HttpResponse<Medicine>) => medicine.body)
      );
    }
    return of(new Medicine());
  }
}

export const medicineRoute: Routes = [
  {
    path: '',
    component: MedicineComponent,
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.medicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/view',
    component: MedicineDetailComponent,
    resolve: {
      medicine: MedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.medicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: 'new',
    component: MedicineUpdateComponent,
    resolve: {
      medicine: MedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.medicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  },
  {
    path: ':id/edit',
    component: MedicineUpdateComponent,
    resolve: {
      medicine: MedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.medicine.home.title'
    },
    canActivate: [UserRouteAccessService]
  }
];

export const medicinePopupRoute: Routes = [
  {
    path: ':id/delete',
    component: MedicineDeletePopupComponent,
    resolve: {
      medicine: MedicineResolve
    },
    data: {
      authorities: ['ROLE_USER'],
      pageTitle: 'medicusApp.medicine.home.title'
    },
    canActivate: [UserRouteAccessService],
    outlet: 'popup'
  }
];
