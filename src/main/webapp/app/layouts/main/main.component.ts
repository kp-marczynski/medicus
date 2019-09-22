import {Component, OnInit} from '@angular/core';
import {ActivatedRouteSnapshot, NavigationEnd, NavigationError, Router} from '@angular/router';

import {JhiLanguageHelper} from 'app/core/language/language.helper';
import {SERVER_API_URL} from "app/app.constants";
import {IMedicalHistoryReport} from "app/shared/model/medical-history-report.model";
import {HttpClient, HttpResponse} from '@angular/common/http';
import {Observable} from 'rxjs';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ServerUnavailableComponent} from "app/shared/server-unavailable/server-unavailable.component";

type EntityResponseType = HttpResponse<any>;

@Component({
  selector: 'jhi-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.scss']
})
export class JhiMainComponent implements OnInit {
  constructor(
    private jhiLanguageHelper: JhiLanguageHelper,
    private router: Router, protected http: HttpClient,
    protected modalService: NgbModal) {
  }

  private getPageTitle(routeSnapshot: ActivatedRouteSnapshot) {
    let title: string = routeSnapshot.data && routeSnapshot.data['pageTitle'] ? routeSnapshot.data['pageTitle'] : 'medicusApp';
    if (routeSnapshot.firstChild) {
      title = this.getPageTitle(routeSnapshot.firstChild) || title;
    }
    return title;
  }

  ngOnInit() {
    this.router.events.subscribe(event => {
      if (event instanceof NavigationEnd) {
        this.jhiLanguageHelper.updateTitle(this.getPageTitle(this.router.routerState.snapshot.root));
      }
      if (event instanceof NavigationError && event.error.status === 404) {
        this.router.navigate(['/404']);
      }
    });
    this.checkServerAvailability();

  }

  checkServerAvailability() {
    this.pingServer().subscribe(null, error => {
      if (error.status === 504) {
        const modalRef = this.modalService.open(ServerUnavailableComponent);
        modalRef.componentInstance.modalRef = modalRef;
        modalRef.result.then(() => this.checkServerAvailability(),() => this.checkServerAvailability())
      }
    })
  }

  pingServer(): Observable<EntityResponseType> {
    return this.http.get<IMedicalHistoryReport>(`${SERVER_API_URL + 'api'}`, {observe: 'response'});
  }
}
