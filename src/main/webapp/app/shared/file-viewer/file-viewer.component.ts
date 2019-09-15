import {Component, OnInit, AfterViewInit, Input} from '@angular/core';

@Component({
  selector: 'jhi-file-viewer',
  templateUrl: './file-viewer.component.html',
  styleUrls: ['./file-viewer.component.scss']
})
export class FileViewerComponent implements OnInit, AfterViewInit {

  @Input() contentType: string;
  @Input() data: string;

  constructor() {
  }

  ngOnInit() {
  }

  ngAfterViewInit(): void {
    this.displayFile(this.contentType, this.data);
  }

  displayFile(contentType: string, data: string) {
    const byteCharacters = atob(data);
    const byteNumbers = new Array(byteCharacters.length);
    for (let i = 0; i < byteCharacters.length; i++) {
      byteNumbers[i] = byteCharacters.charCodeAt(i);
    }
    const byteArray = new Uint8Array(byteNumbers);
    const blob = new Blob([byteArray], {
      type: contentType
    });
    const file = window.URL.createObjectURL(blob);
    const iframe = document.getElementById("iframe") as HTMLObjectElement;
    const href = document.getElementById("alternative-href") as HTMLLinkElement;
    if (iframe) {
      iframe.data = file;
      href.href = file;
      iframe.height = (
        500 > iframe.offsetWidth
        ? 500
        : iframe.offsetWidth
      ).toString();
    }
  }
}
