import { Component, inject, signal, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';


import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';
import { BeneficioService } from '../../../../core/services/beneficio.service';

@Component({
  selector: 'app-editar-beneficio',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatTooltipModule,
    MatSlideToggleModule,
  ],
  templateUrl: './editar-beneficio.html',
  styleUrl: './editar-beneficio.scss',
})
export class EditarBeneficioComponent implements OnInit {

  private fb      = inject(FormBuilder);
  private service = inject(BeneficioService);
  private snack   = inject(MatSnackBar);
  private router  = inject(Router);
  private route   = inject(ActivatedRoute);

  readonly id = Number(this.route.snapshot.paramMap.get('id'));

  loadingDados  = signal(false);
  loadingSalvar = signal(false);
  erroDados     = signal<string | null>(null);

  form = this.fb.group({
    nome:     ['', [Validators.required, Validators.maxLength(100)]],
    descricao: ['', [Validators.maxLength(255)]],
    valor:    [null as number | null, [Validators.required, Validators.min(0.01)]],
    ativo: [{ value: true, disabled: true }],
  });

  ngOnInit(): void {
    this.carregarBeneficio();
  }

  carregarBeneficio(): void {
    this.loadingDados.set(true);
    this.erroDados.set(null);

    this.service.buscarPorId(this.id).subscribe({
      next: (b) => {
        this.form.patchValue({
          nome:      b.nome,
          descricao: b.descricao ?? '',
          valor:     b.valor,
          ativo:     b.ativo,
        });
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

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loadingSalvar.set(true);

    const payload = {
      nome:      this.form.value.nome!,
      descricao: this.form.value.descricao ?? '',
      valor:     this.form.value.valor!,
    };

    this.service.atualizar(this.id, payload).subscribe({
      next: () => {
        this.loadingSalvar.set(false);
        this.form.markAsPristine();
        this.snack.open('Benefício atualizado com sucesso!', 'Fechar', {
          duration: 4000,
          panelClass: ['snack-success'],
        });
        this.router.navigate(['/beneficios']);
      },
      error: (err) => {
        this.loadingSalvar.set(false);

        if (err.status === 0 || err.status === 503) {
          this.router.navigate(['/erro-servico']);
          return;
        }

        const apiError = err.error;

        if (apiError?.detalhes?.erros) {
          const mensagens = Object.values(apiError.detalhes.erros)
            .map(msg => `• ${msg}`)
            .join('\n');
          this.snack.open(mensagens, 'Fechar', {
            duration: 6000,
            panelClass: ['snack-error'],
          });
          return;
        }

        this.snack.open(
          apiError?.mensagem || 'Erro ao atualizar benefício.',
          'Fechar',
          { duration: 5000, panelClass: ['snack-error'] },
        );
      },
    });
  }

  markTouched(field: string): void {
    this.form.get(field)?.markAsTouched();
  }

  getError(field: string): string {
    const control = this.form.get(field);
    if (!control?.errors || !control.touched) return '';

    if (control.errors['required'])  return 'Campo obrigatório';
    if (control.errors['maxlength']) return `Máximo de ${control.errors['maxlength'].requiredLength} caracteres`;
    if (control.errors['min'])       return 'Valor deve ser maior que R$ 0,01';

    return 'Campo inválido';
  }

  limparSeZero(): void {
    if (this.form.get('valor')?.value === 0) {
        this.form.get('valor')?.setValue(null);
    }
  }
}
