import { NgModule } from '@angular/core';
import { MedicusSharedLibsModule } from './shared-libs.module';
import { FindLanguageFromKeyPipe } from './language/find-language-from-key.pipe';
import { JhiAlertComponent } from './alert/alert.component';
import { JhiAlertErrorComponent } from './alert/alert-error.component';
import { JhiLoginModalComponent } from './login/login.component';
import { HasAnyAuthorityDirective } from './auth/has-any-authority.directive';
import { FileViewerComponent } from './file-viewer/file-viewer.component';
import { ServerUnavailableComponent } from './server-unavailable/server-unavailable.component';

@NgModule({
  imports: [MedicusSharedLibsModule],
  declarations: [FindLanguageFromKeyPipe, JhiAlertComponent, JhiAlertErrorComponent, JhiLoginModalComponent, HasAnyAuthorityDirective, FileViewerComponent, ServerUnavailableComponent],
  entryComponents: [JhiLoginModalComponent, ServerUnavailableComponent],
  exports: [
    MedicusSharedLibsModule,
    FindLanguageFromKeyPipe,
    JhiAlertComponent,
    JhiAlertErrorComponent,
    JhiLoginModalComponent,
    HasAnyAuthorityDirective,
    FileViewerComponent,
    ServerUnavailableComponent
  ]
})
export class MedicusSharedModule {}
