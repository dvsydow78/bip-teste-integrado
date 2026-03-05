import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { Router, RouterModule } from '@angular/router';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatDividerModule } from '@angular/material/divider';
import { BeneficioService } from '../../../../core/services/beneficio.service';
import { Beneficio } from '../../../models/beneficio.model';

@Component({
  selector: 'app-lista-beneficios',
  imports: [
    CommonModule,
    CurrencyPipe,
    RouterModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatChipsModule,
    MatProgressSpinnerModule,
    MatTooltipModule,
    MatDividerModule,
  ],
  templateUrl: './lista-beneficios.html',
  styleUrl: './lista-beneficios.scss',
})
export class ListaBeneficiosComponent implements OnInit {

  private service = inject(BeneficioService);
  private router  = inject(Router);

  readonly colunas = ['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes'];

  beneficios = signal<Beneficio[]>([]);
  loading    = signal(false);
  erro       = signal<string | null>(null);

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.loading.set(true);
    this.erro.set(null);

    this.service.listar().subscribe({
      next: (data) => {
        this.beneficios.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.loading.set(false);

        if (err.status === 0 || err.status === 503) {
          this.router.navigate(['/erro-servico']);
          return;
        }

        this.erro.set('Não foi possível carregar os benefícios.');
      },
    });
  }
}
