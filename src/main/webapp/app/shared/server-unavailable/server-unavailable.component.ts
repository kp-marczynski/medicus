import {Component, OnInit} from '@angular/core';
import {NgbModalRef} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'jhi-server-unavailable',
  templateUrl: './server-unavailable.component.html',
  styleUrls: ['./server-unavailable.component.scss']
})
export class ServerUnavailableComponent implements OnInit {

  counter: number;
  modalRef: NgbModalRef;

  constructor() {
  }

  ngOnInit() {
    this.counter = 60;
    this.countdown();
  }

  countdown() {
    if (this.counter > 0) {
      setTimeout(() => {
        this.counter--;
        this.countdown()
      }, 1000);
    } else {
      this.modalRef.close();
    }
  }
}
