import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { Notice } from './core/notice';
import { DemoBanner } from './demo-data/demo-banner';
import { SiteHeader } from './layout/site-header';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, DemoBanner, SiteHeader],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  protected readonly notice = inject(Notice);
}
