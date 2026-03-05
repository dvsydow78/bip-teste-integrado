import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { CriarBeneficioComponent } from './criar-beneficio';
import { BeneficioService } from '../../../../core/services/beneficio.service';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const flushPromises = () => new Promise(resolve => setTimeout(resolve));

function makeError(status: number, body: unknown = {}) {
  return throwError(() => ({ status, error: body }));
}

// ---------------------------------------------------------------------------
// Stubs
// ---------------------------------------------------------------------------

const beneficioServiceStub = {
  criar: vi.fn(),
};

const routerStub = {
  navigate: vi.fn(),
};

const snackBarStub = {
  open: vi.fn(),
};

const activatedRouteStub = {
  snapshot: { paramMap: { get: () => null } },
};

// ---------------------------------------------------------------------------
// Suite
// ---------------------------------------------------------------------------

describe('CriarBeneficioComponent', () => {

  let component: CriarBeneficioComponent;
  let fixture: ComponentFixture<CriarBeneficioComponent>;

  beforeEach(async () => {

    vi.clearAllMocks();

    await TestBed.configureTestingModule({
      imports: [CriarBeneficioComponent, ReactiveFormsModule, NoopAnimationsModule],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceStub },
        { provide: Router,           useValue: routerStub },
        { provide: ActivatedRoute,   useValue: activatedRouteStub },
      ],
    })
    .overrideProvider(MatSnackBar, { useValue: snackBarStub })
    .compileComponents();

    fixture = TestBed.createComponent(CriarBeneficioComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();
  });

  // -------------------------------------------------------------------------
  // Inicialização
  // -------------------------------------------------------------------------

  describe('Inicialização', () => {

    it('deve criar o componente', () => {
      expect(component).toBeTruthy();
    });

    it('deve inicializar o formulário com valores vazios/nulos', () => {
      expect(component.form.value).toEqual({ nome: '', descricao: '', valor: null });
    });

    it('deve inicializar loading como false', () => {
      expect(component.loading()).toBe(false);
    });

    it('formulário deve estar inválido ao ser criado (campos obrigatórios vazios)', () => {
      expect(component.form.invalid).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // Validação — campo "nome"
  // -------------------------------------------------------------------------

  describe('Validação: nome', () => {

    it('deve ser obrigatório', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('');
      ctrl.markAsTouched();
      expect(ctrl.hasError('required')).toBe(true);
    });

    it('deve respeitar maxlength de 100 caracteres', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('a'.repeat(101));
      expect(ctrl.hasError('maxlength')).toBe(true);
    });

    it('deve ser válido com valor dentro do limite', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('Vale Refeição');
      expect(ctrl.valid).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // Validação — campo "descricao"
  // -------------------------------------------------------------------------

  describe('Validação: descricao', () => {

    it('deve ser opcional (válido mesmo vazio)', () => {
      const ctrl = component.form.get('descricao')!;
      ctrl.setValue('');
      expect(ctrl.valid).toBe(true);
    });

    it('deve respeitar maxlength de 255 caracteres', () => {
      const ctrl = component.form.get('descricao')!;
      ctrl.setValue('x'.repeat(256));
      expect(ctrl.hasError('maxlength')).toBe(true);
    });

    it('deve ser válido com 255 caracteres exatos', () => {
      const ctrl = component.form.get('descricao')!;
      ctrl.setValue('x'.repeat(255));
      expect(ctrl.valid).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // Validação — campo "valor"
  // -------------------------------------------------------------------------

  describe('Validação: valor', () => {

    it('deve ser obrigatório', () => {
      const ctrl = component.form.get('valor')!;
      ctrl.setValue(null);
      ctrl.markAsTouched();
      expect(ctrl.hasError('required')).toBe(true);
    });

    it('deve rejeitar valor zero', () => {
      const ctrl = component.form.get('valor')!;
      ctrl.setValue(0);
      expect(ctrl.hasError('min')).toBe(true);
    });

    it('deve rejeitar valor negativo', () => {
      const ctrl = component.form.get('valor')!;
      ctrl.setValue(-1);
      expect(ctrl.hasError('min')).toBe(true);
    });

    it('deve aceitar valor mínimo de 0.01', () => {
      const ctrl = component.form.get('valor')!;
      ctrl.setValue(0.01);
      expect(ctrl.valid).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // getError()
  // -------------------------------------------------------------------------

  describe('getError()', () => {

    it('deve retornar string vazia quando o campo não foi tocado', () => {
      component.form.get('nome')!.setValue('');
      expect(component.getError('nome')).toBe('');
    });

    it('deve retornar "Campo obrigatório" para erro required', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('');
      ctrl.markAsTouched();
      expect(component.getError('nome')).toBe('Campo obrigatório');
    });

    it('deve retornar mensagem com limite para erro maxlength', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('a'.repeat(101));
      ctrl.markAsTouched();
      expect(component.getError('nome')).toBe('Máximo de 100 caracteres');
    });

    it('deve retornar mensagem de valor mínimo para erro min', () => {
      const ctrl = component.form.get('valor')!;
      ctrl.setValue(0);
      ctrl.markAsTouched();
      expect(component.getError('valor')).toBe('Valor deve ser maior que R$ 0,01');
    });

    it('deve retornar string vazia quando não há erros', () => {
      const ctrl = component.form.get('nome')!;
      ctrl.setValue('Benefício válido');
      ctrl.markAsTouched();
      expect(component.getError('nome')).toBe('');
    });

  });

  // -------------------------------------------------------------------------
  // markTouched()
  // -------------------------------------------------------------------------

  describe('markTouched()', () => {

    it('deve marcar o campo como touched', () => {
      const ctrl = component.form.get('nome')!;
      expect(ctrl.touched).toBe(false);
      component.markTouched('nome');
      expect(ctrl.touched).toBe(true);
    });

    it('não deve lançar erro para campo inexistente', () => {
      expect(() => component.markTouched('campoInexistente')).not.toThrow();
    });

  });

  // -------------------------------------------------------------------------
  // resetar()
  // -------------------------------------------------------------------------

  describe('resetar()', () => {

    it('deve limpar todos os campos do formulário', () => {
      component.form.setValue({ nome: 'Teste', descricao: 'Desc', valor: 100 });
      component.resetar();
      expect(component.form.value).toEqual({ nome: null, descricao: null, valor: null });
    });

    it('deve tornar o formulário pristine e untouched após reset', () => {
      component.form.markAllAsTouched();
      component.resetar();
      expect(component.form.untouched).toBe(true);
      expect(component.form.pristine).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // salvar() — formulário inválido
  // -------------------------------------------------------------------------

  describe('salvar() — formulário inválido', () => {

    it('não deve chamar o serviço quando o formulário é inválido', () => {
      component.salvar();
      expect(beneficioServiceStub.criar).not.toHaveBeenCalled();
    });

    it('deve marcar todos os campos como touched ao tentar salvar com formulário inválido', () => {
      component.salvar();
      expect(component.form.get('nome')!.touched).toBe(true);
      expect(component.form.get('valor')!.touched).toBe(true);
    });

  });

  // -------------------------------------------------------------------------
  // salvar() — sucesso
  // -------------------------------------------------------------------------

  describe('salvar() — sucesso', () => {

    beforeEach(() => {
      beneficioServiceStub.criar.mockReturnValue(of({}));
      component.form.setValue({ nome: 'Vale Refeição', descricao: 'Benefício alimentar', valor: 500 });
    });

    it('deve chamar o serviço com o payload correto', async () => {

      component.salvar();

      await flushPromises();

      expect(beneficioServiceStub.criar).toHaveBeenCalledWith({
        nome:      'Vale Refeição',
        descricao: 'Benefício alimentar',
        valor:     500,
      });

    });

    it('deve usar string vazia para descricao quando o campo está nulo', async () => {

      component.form.get('descricao')!.setValue(null as any);

      component.salvar();

      await flushPromises();

      const payload = beneficioServiceStub.criar.mock.calls[0][0];
      expect(payload.descricao).toBe('');

    });

    it('deve definir loading como false após sucesso', async () => {

      component.salvar();

      await flushPromises();

      expect(component.loading()).toBe(false);

    });

    it('deve resetar o formulário após sucesso', async () => {

      component.salvar();

      await flushPromises();

      expect(component.form.value).toEqual({ nome: null, descricao: null, valor: null });

    });

    it('deve exibir snackbar de sucesso', async () => {

      component.salvar();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Benefício criado com sucesso!',
        'Fechar',
        expect.objectContaining({ duration: 4000, panelClass: ['snack-success'] }),
      );

    });

    it('deve navegar para /lista após sucesso', async () => {

      component.salvar();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/lista']);

    });

  });

  // -------------------------------------------------------------------------
  // salvar() — erros
  // -------------------------------------------------------------------------

  describe('salvar() — erro de serviço indisponível (status 0)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.criar.mockReturnValue(makeError(0));
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve definir loading como false', async () => {

      beneficioServiceStub.criar.mockReturnValue(makeError(0));
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      expect(component.loading()).toBe(false);

    });

  });

  describe('salvar() — erro de serviço indisponível (status 503)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.criar.mockReturnValue(makeError(503));
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

  });

  describe('salvar() — erro 422 com erros de validação por campo', () => {

    it('deve exibir snackbar com as mensagens de erro formatadas', async () => {

      beneficioServiceStub.criar.mockReturnValue(
        makeError(422, {
          detalhes: { erros: { nome: 'Nome já cadastrado', valor: 'Valor inválido' } },
        }),
      );
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      const [mensagem, , options] = snackBarStub.open.mock.calls[0];
      expect(mensagem).toContain('• Nome já cadastrado');
      expect(mensagem).toContain('• Valor inválido');
      expect(options).toMatchObject({ duration: 6000, panelClass: ['snack-error'] });

    });

  });

  describe('salvar() — erro genérico com mensagem da API', () => {

    it('deve exibir a mensagem retornada pela API', async () => {

      beneficioServiceStub.criar.mockReturnValue(
        makeError(400, { mensagem: 'Operação não permitida.' }),
      );
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Operação não permitida.',
        'Fechar',
        expect.objectContaining({ duration: 5000, panelClass: ['snack-error'] }),
      );

    });

  });

  describe('salvar() — erro genérico sem mensagem da API', () => {

    it('deve exibir mensagem padrão de fallback', async () => {

      beneficioServiceStub.criar.mockReturnValue(makeError(500, {}));
      component.form.setValue({ nome: 'X', descricao: '', valor: 1 });

      component.salvar();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Erro ao salvar benefício.',
        'Fechar',
        expect.any(Object),
      );

    });

  });

});