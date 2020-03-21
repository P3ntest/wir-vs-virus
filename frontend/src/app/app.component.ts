import {Component} from '@angular/core';
import {MatIconRegistry} from '@angular/material/icon';
import {DomSanitizer} from '@angular/platform-browser';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  constructor(private matIconRegistry: MatIconRegistry, private domSanitizer: DomSanitizer) {
    this.matIconRegistry.addSvgIcon(
      'WHATSAPP',
      this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/whatsapp.svg'))
      .addSvgIcon(
        'FACETIME',
        this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/voice_chat.svg'))
      .addSvgIcon(
        'PHONE',
        this.domSanitizer.bypassSecurityTrustResourceUrl('../assets/phone.svg')
      );
  }
}
