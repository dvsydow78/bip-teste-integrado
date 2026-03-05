import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, ActivatedRoute } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { RemoverBeneficioComponent } from './remover-beneficio';
import { BeneficioService } from '../../../../core/services/beneficio.service';
import { Beneficio } from '../../../models/beneficio.model';

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
  buscarPorId: vi.fn(),
  desativar:   vi.fn(),
};

const routerStub = {
  navigate: vi.fn(),
};

const snackBarStub = {
  open: vi.fn(),
};

const activatedRouteStub = {
  snapshot: { paramMap: { get: () => '1' } },
};

// ---------------------------------------------------------------------------
// Suite
// ---------------------------------------------------------------------------

describe('RemoverBeneficioComponent', () => {

  let component: RemoverBeneficioComponent;
  let fixture: ComponentFixture<RemoverBeneficioComponent>;

  const beneficioMock: Beneficio = {
    id: 1,
    nome: 'Vale Refeição',
    descricao: 'Benefício alimentar',
    valor: 500,
    ativo: true,
  };

  beforeEach(async () => {

    vi.clearAllMocks();

    beneficioServiceStub.buscarPorId.mockReturnValue(of(beneficioMock));

    await TestBed.configureTestingModule({
      imports: [RemoverBeneficioComponent, NoopAnimationsModule],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceStub },
        { provide: Router,           useValue: routerStub },
        { provide: ActivatedRoute,   useValue: activatedRouteStub },
      ],
    })
    .overrideProvider(MatSnackBar, { useValue: snackBarStub })
    .compileComponents();

    fixture = TestBed.createComponent(RemoverBeneficioComponent);
    component = fixture.componentInstance;

    fixture.detectChanges();

    await flushPromises();
  });

  // -------------------------------------------------------------------------
  // Inicialização
  // -------------------------------------------------------------------------

  describe('Inicialização', () => {

    it('deve criar o componente', () => {
      expect(component).toBeTruthy();
    });

    it('deve ler o id da rota corretamente', () => {
      expect(component.id).toBe(1);
    });

    it('deve inicializar loadingDados como false após carregar', () => {
      expect(component.loadingDados()).toBe(false);
    });

    it('deve inicializar loadingRemover como false', () => {
      expect(component.loadingRemover()).toBe(false);
    });

    it('deve inicializar erroDados como null após carregar com sucesso', () => {
      expect(component.erroDados()).toBeNull();
    });

    it('deve chamar carregarBeneficio no ngOnInit', () => {
      expect(beneficioServiceStub.buscarPorId).toHaveBeenCalledTimes(1);
    });

  });

  // -------------------------------------------------------------------------
  // carregarBeneficio() — sucesso
  // -------------------------------------------------------------------------

  describe('carregarBeneficio() — sucesso', () => {

    it('deve preencher o signal beneficio com os dados retornados', () => {
      expect(component.beneficio()).toEqual(beneficioMock);
    });

    it('deve definir loadingDados como false após carregar', () => {
      expect(component.loadingDados()).toBe(false);
    });

    it('deve definir erroDados como null após carregar', () => {
      expect(component.erroDados()).toBeNull();
    });

  });

  // -------------------------------------------------------------------------
  // carregarBeneficio() — erros
  // -------------------------------------------------------------------------

  describe('carregarBeneficio() — erro de serviço indisponível (status 0)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(0));

      component.carregarBeneficio();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve definir loadingDados como false', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(0));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.loadingDados()).toBe(false);

    });

  });

  describe('carregarBeneficio() — erro de serviço indisponível (status 503)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(503));

      component.carregarBeneficio();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

  });

  describe('carregarBeneficio() — erro 404 benefício não encontrado', () => {

    it('deve definir erroDados com "Benefício não encontrado."', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(404));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).toBe('Benefício não encontrado.');

    });

    it('deve definir loadingDados como false', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(404));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.loadingDados()).toBe(false);

    });

  });

  describe('carregarBeneficio() — erro genérico', () => {

    it('deve definir erroDados com mensagem genérica', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(500));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).toBe('Não foi possível carregar o benefício.');

    });

    it('deve definir loadingDados como false', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(500));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.loadingDados()).toBe(false);

    });

    it('deve limpar erroDados ao tentar novamente com sucesso', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(500));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).not.toBeNull();

      beneficioServiceStub.buscarPorId.mockReturnValue(of(beneficioMock));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).toBeNull();

    });

  });

  // -------------------------------------------------------------------------
  // confirmarRemocao() — sucesso
  // -------------------------------------------------------------------------

  describe('confirmarRemocao() — sucesso', () => {

    beforeEach(() => {
      beneficioServiceStub.desativar.mockReturnValue(of({}));
    });

    it('deve chamar o serviço com o id correto', async () => {

      component.confirmarRemocao();

      await flushPromises();

      expect(beneficioServiceStub.desativar).toHaveBeenCalledWith(1);

    });

    it('deve definir loadingRemover como false após sucesso', async () => {

      component.confirmarRemocao();

      await flushPromises();

      expect(component.loadingRemover()).toBe(false);

    });

    it('deve exibir snackbar de sucesso', async () => {

      component.confirmarRemocao();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Benefício removido com sucesso!',
        'Fechar',
        expect.objectContaining({ duration: 4000, panelClass: ['snack-success'] }),
      );

    });

    it('deve navegar para /lista após sucesso', async () => {

      component.confirmarRemocao();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/lista']);

    });

  });

  // -------------------------------------------------------------------------
  // confirmarRemocao() — erros
  // -------------------------------------------------------------------------

  describe('confirmarRemocao() — erro de serviço indisponível (status 0)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.desativar.mockReturnValue(makeError(0));

      component.confirmarRemocao();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve definir loadingRemover como false', async () => {

      beneficioServiceStub.desativar.mockReturnValue(makeError(0));

      component.confirmarRemocao();

      await flushPromises();

      expect(component.loadingRemover()).toBe(false);

    });

  });

  describe('confirmarRemocao() — erro de serviço indisponível (status 503)', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.desativar.mockReturnValue(makeError(503));

      component.confirmarRemocao();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

  });

  describe('confirmarRemocao() — erro genérico com mensagem da API', () => {

    it('deve exibir a mensagem retornada pela API', async () => {

      beneficioServiceStub.desativar.mockReturnValue(
        makeError(400, { mensagem: 'Operação não permitida.' }),
      );

      component.confirmarRemocao();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Operação não permitida.',
        'Fechar',
        expect.objectContaining({ duration: 5000, panelClass: ['snack-error'] }),
      );

    });

  });

  describe('confirmarRemocao() — erro genérico sem mensagem da API', () => {

    it('deve exibir mensagem padrão de fallback', async () => {

      beneficioServiceStub.desativar.mockReturnValue(makeError(500, {}));

      component.confirmarRemocao();

      await flushPromises();

      expect(snackBarStub.open).toHaveBeenCalledWith(
        'Erro ao remover benefício.',
        'Fechar',
        expect.any(Object),
      );

    });

  });

});