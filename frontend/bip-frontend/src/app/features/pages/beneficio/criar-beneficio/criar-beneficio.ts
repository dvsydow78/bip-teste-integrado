import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Router } from '@angular/router';


import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDividerModule } from '@angular/material/divider';
import { MatTooltipModule } from '@angular/material/tooltip';
import { BeneficioService } from '../../../../core/services/beneficio.service';

@Component({
  selector: 'app-criar-beneficio',
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
  templateUrl: './criar-beneficio.html',
  styleUrl: './criar-beneficio.scss',
})
export class CriarBeneficioComponent {

  private fb      = inject(FormBuilder);
  private service = inject(BeneficioService);
  private snack   = inject(MatSnackBar);
  private router  = inject(Router);

  loading = signal(false);

  form = this.fb.group({
    nome: [
      '',
      [Validators.required, Validators.maxLength(100)],
    ],
    descricao: [
      '',
      [Validators.maxLength(255)],
    ],
    valor: [
      null as number | null,
      [Validators.required, Validators.min(0.01)],
    ],
  });

  salvar(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);

    const payload = {
      nome:      this.form.value.nome!,
      descricao: this.form.value.descricao ?? '',
      valor:     this.form.value.valor!,
    };

    this.service.criar(payload).subscribe({
      next: () => {
        this.loading.set(false);
        this.form.reset();
        this.snack.open('Benefício criado com sucesso!', 'Fechar', {
          duration: 4000,
          panelClass: ['snack-success'],
        });
        this.router.navigate(['/lista']);
      },
      error: (err) => {
        this.loading.set(false);

        // Serviço indisponível
        if (err.status === 0 || err.status === 503) {
          this.router.navigate(['/erro-servico']);
          return;
        }

        const apiError = err.error;

        // 422 com erros de validação por campo
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

        // Demais erros de negócio
        this.snack.open(
          apiError?.mensagem || 'Erro ao salvar benefício.',
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

    if (control.errors['required'])   return 'Campo obrigatório';
    if (control.errors['maxlength'])  return `Máximo de ${control.errors['maxlength'].requiredLength} caracteres`;
    if (control.errors['min'])        return 'Valor deve ser maior que R$ 0,01';

    return 'Campo inválido';
  }
}
