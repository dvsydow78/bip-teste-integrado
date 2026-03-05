import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';

import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatChipsModule } from '@angular/material/chips';
import { BeneficioService } from '../../../../core/services/beneficio.service';
import { Beneficio } from '../../../models/beneficio.model';

@Component({
  selector: 'app-remover-beneficio',
  imports: [
    CommonModule,
    RouterModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatTooltipModule,
    MatChipsModule,
  ],
  templateUrl: './remover-beneficio.html',
  styleUrl: './remover-beneficio.scss',
})
export class RemoverBeneficioComponent implements OnInit {

  private service = inject(BeneficioService);
  private snack   = inject(MatSnackBar);
  private router  = inject(Router);
  private route   = inject(ActivatedRoute);

  readonly id = Number(this.route.snapshot.paramMap.get('id'));

  loadingDados   = signal(false);
  loadingRemover = signal(false);
  erroDados      = signal<string | null>(null);
  beneficio      = signal<Beneficio | null>(null);

  ngOnInit(): void {
    this.carregarBeneficio();
  }

  carregarBeneficio(): void {
    this.loadingDados.set(true);
    this.erroDados.set(null);

    this.service.buscarPorId(this.id).subscribe({
      next: (b) => {
        this.beneficio.set(b);
        this.loadingDados.set(false);
      },
      error: (err) => {
        this.loadingDados.set(false);

        if (err.status === 0 || err.status === 503) {
          this.router.navigate(['/erro-servico']);
          return;
        }

        if (err.status === 404) {
          this.erroDados.set('Benefício não encontrado.');
          return;
        }

        this.erroDados.set('Não foi possível carregar o benefício.');
      },
    });
  }

  confirmarRemocao(): void {
    this.loadingRemover.set(true);

    this.service.desativar(this.id).subscribe({
      next: () => {
        this.loadingRemover.set(false);
        this.snack.open('Benefício removido com sucesso!', 'Fechar', {
          duration: 4000,
          panelClass: ['snack-success'],
        });
        this.router.navigate(['/lista']);
      },
      error: (err) => {
        this.loadingRemover.set(false);

        if (err.status === 0 || err.status === 503) {
          this.router.navigate(['/erro-servico']);
          return;
        }

        const apiError = err.error;

        this.snack.open(
          apiError?.mensagem || 'Erro ao remover benefício.',
          'Fechar',
          { duration: 5000, panelClass: ['snack-error'] },
        );
      },
    });
  }
}
