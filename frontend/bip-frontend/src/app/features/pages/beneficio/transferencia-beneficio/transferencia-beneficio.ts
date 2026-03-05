import { Component, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl } from '@angular/forms';

import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { Router } from '@angular/router';
import { BeneficioService } from '../../../../core/services/beneficio.service';

@Component({
  selector: 'app-transferencia-beneficio',
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    MatDividerModule,
    MatTooltipModule,
  ],
  templateUrl: './transferencia-beneficio.html',
  styleUrl: './transferencia-beneficio.scss',
})
export class TransferenciaBeneficioComponent {

  private fb      = inject(FormBuilder);
  private service = inject(BeneficioService);
  private snack   = inject(MatSnackBar);
  private router  = inject(Router);

  /** Controla o estado de carregamento do botão */
  loading = signal(false);

  form = this.fb.group(
    {
      fromId: [null as number | null, [Validators.required, Validators.min(1)]],
      toId:   [null as number | null, [Validators.required, Validators.min(1)]],
      valor:  [null as number | null, [Validators.required, Validators.min(0.01)]],
    },
    { validators: this.contasDiferentesValidator }
  );

  transferir(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    const payload = {
      fromId: this.form.value.fromId!,
      toId:   this.form.value.toId!,
      amount:  this.form.value.valor!,
    };

    this.service.transferir(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.form.reset();
        this.snack.open('Transferência realizada com sucesso!', 'Fechar', {
          duration: 4000,
          panelClass: ['snack-success'],
        });
        this.router.navigate(['/lista']);
      },
      error: (err) => {
        this.loading.set(false);
        const apiError = err.error;

        // 422 com erros de validação detalhados
        if (apiError?.detalhes?.erros) {
          const mensagens = Object.values(apiError.detalhes.erros)
            .map(msg => `• ${msg}`)
            .join('\n');
          this.snack.open(mensagens, 'Fechar', { duration: 6000, panelClass: ['snack-error'] });
          return;
        }

        // Demais erros de negócio (400, 409, 422 simples)
        this.snack.open(
          apiError?.mensagem || 'Erro ao realizar transferência.',
          'Fechar',
          { duration: 5000, panelClass: ['snack-error'] }
        );
      },
    });
  }

  resetar(): void {
    this.form.reset();
  }

  markTouched(field: string): void {
    this.form.get(field)?.markAsTouched();
  }

  getError(field: string): string {
    const control = this.form.get(field);
    if (!control?.errors || !control.touched) return '';

    if (control.errors['required']) return 'Campo obrigatório';
    if (control.errors['min'])      return field === 'valor'
      ? 'Valor deve ser maior que R$ 0,01'
      : 'ID deve ser maior que zero';

    return 'Campo inválido';
  }

  /** Validador customizado: origem e destino não podem ser iguais */
  contasDiferentesValidator(control: AbstractControl) {
    const fromId = control.get('fromId')?.value;
    const toId   = control.get('toId')?.value;
    if (fromId && toId && fromId === toId) {
      return { contasIguais: true };
    }
    return null;
  }
}
