// import { Component, signal } from '@angular/core';
// import { RouterModule, RouterOutlet } from '@angular/router';
// import { MatSidenavModule } from '@angular/material/sidenav';
// import { MatToolbarModule } from '@angular/material/toolbar';
// import { MatListModule } from '@angular/material/list';
// import { MatIconModule } from '@angular/material/icon';
// import { MatButtonModule } from '@angular/material/button';

// @Component({
//   selector: 'app-root',
//   imports: [RouterOutlet, RouterModule, MatSidenavModule, MatToolbarModule, MatListModule, MatIconModule, MatButtonModule],
//   templateUrl: './app.html',
//   styleUrl: './app.scss'
// })
// export class App {
//   protected readonly title = signal('bip-frontend');
// }

import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, RouterOutlet } from '@angular/router';

import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatListModule } from '@angular/material/list';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';

interface NavItem {
  label: string;
  icon:  string;
  route: string;
}

@Component({
  standalone: true,
  selector: 'app-root',
  imports: [
    CommonModule,
    RouterModule,
    RouterOutlet,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    MatTooltipModule,
    MatDividerModule,
  ],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {

  private router = inject(Router);

  sidenavOpen = signal(true);

  readonly navItems: NavItem[] = [
    { label: 'Lista de Benefícios', icon: 'list_alt',     route: '/lista'         },
    // { label: 'Novo Benefício',      icon: 'add_card',     route: '/novo'    },
    { label: 'Transferência',       icon: 'swap_horiz',   route: '/transferencia'      },
  ];

  paginaAtual = signal('Lista de Benefícios');

  constructor() {
    this.router.events.subscribe(() => {
      const url = this.router.url;
      const item = this.navItems.find(n => url.startsWith(n.route) && n.route !== '/beneficios')
        ?? this.navItems.find(n => url === n.route || url === '/beneficios');

      // Rota de edição não está no nav mas precisa de título
      if (url.includes('/editar')) {
        this.paginaAtual.set('Editar Benefício');
        return;
      }

      this.paginaAtual.set(item?.label ?? 'BIP Sistema');
    });
  }

  toggleSidenav(): void {
    this.sidenavOpen.update(v => !v);
  }

  recarregar(): void {
    const url = this.router.url;
    this.router.navigateByUrl('/', { skipLocationChange: true })
      .then(() => this.router.navigateByUrl(url));
  }
}
