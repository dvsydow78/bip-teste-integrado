import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { of, throwError } from 'rxjs';
import { describe, it, expect, beforeEach, vi } from 'vitest';

import { ListaBeneficiosComponent } from './lista-beneficios';
import { BeneficioService } from '../../../../core/services/beneficio.service';
import { Beneficio } from '../../../models/beneficio.model';
import { ActivatedRoute } from '@angular/router';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

const flushPromises = () => new Promise(resolve => setTimeout(resolve));

function makeError(status: number) {
  return throwError(() => ({ status, error: {} }));
}

const beneficiosMock: Beneficio[] = [
  { id: 1, nome: 'Vale Refeição', descricao: 'Alimentação', valor: 500, ativo: true },
  { id: 2, nome: 'Vale Transporte', descricao: '', valor: 200, ativo: true },
  { id: 3, nome: 'Plano de Saúde', descricao: 'Saúde', valor: 1000, ativo: false },
];

// ---------------------------------------------------------------------------
// Stubs
// ---------------------------------------------------------------------------

const beneficioServiceStub = {
  listar: vi.fn(),
};

const routerStub = {
  navigate: vi.fn(),
};

const activatedRouteStub = {
  snapshot: {
    paramMap: {
      get: vi.fn()
    }
  },
  params: of({}),
  queryParams: of({})
};

// ---------------------------------------------------------------------------
// Suite
// ---------------------------------------------------------------------------

describe('ListaBeneficiosComponent', () => {

  let component: ListaBeneficiosComponent;
  let fixture: ComponentFixture<ListaBeneficiosComponent>;

  beforeEach(async () => {

    vi.clearAllMocks();

    beneficioServiceStub.listar.mockReturnValue(of(beneficiosMock));

    await TestBed.configureTestingModule({
      imports: [ListaBeneficiosComponent, NoopAnimationsModule],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceStub },
        { provide: Router, useValue: routerStub },
        { provide: ActivatedRoute, useValue: activatedRouteStub } // 👈 adicionar
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(ListaBeneficiosComponent);
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

    it('deve definir as colunas da tabela corretamente', () => {
      expect(component.colunas).toEqual(['id', 'nome', 'descricao', 'valor', 'ativo', 'acoes']);
    });

    it('deve inicializar loading como false após carregar', () => {
      expect(component.loading()).toBe(false);
    });

    it('deve inicializar erro como null após carregar com sucesso', () => {
      expect(component.erro()).toBeNull();
    });

    it('deve chamar carregarBeneficios no ngOnInit', () => {
      expect(beneficioServiceStub.listar).toHaveBeenCalledTimes(1);
    });

  });

  // -------------------------------------------------------------------------
  // carregarBeneficios() — sucesso
  // -------------------------------------------------------------------------

  describe('carregarBeneficios() — sucesso', () => {

    it('deve preencher o signal beneficios com os dados retornados', () => {
      expect(component.beneficios()).toEqual(beneficiosMock);
    });

    it('deve definir loading como false após carregar', () => {
      expect(component.loading()).toBe(false);
    });

    it('deve definir erro como null após carregar', () => {
      expect(component.erro()).toBeNull();
    });

    it('deve aceitar lista vazia sem erros', async () => {

      beneficioServiceStub.listar.mockReturnValue(of([]));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.beneficios()).toEqual([]);
      expect(component.erro()).toBeNull();

    });

  });

  // -------------------------------------------------------------------------
  // carregarBeneficios() — erros
  // -------------------------------------------------------------------------

  describe('carregarBeneficios() — erro status 0', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(0));

      component.carregarBeneficios();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve definir loading como false', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(0));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.loading()).toBe(false);

    });

  });

  describe('carregarBeneficios() — erro status 503', () => {

    it('deve navegar para /erro-servico', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(503));

      component.carregarBeneficios();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

  });

  describe('carregarBeneficios() — erro genérico', () => {

    it('deve definir mensagem de erro no signal erro', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(500));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.erro()).toBe('Não foi possível carregar os benefícios.');

    });

    it('deve definir loading como false', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(500));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.loading()).toBe(false);

    });

    it('deve limpar o erro ao tentar novamente com sucesso', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(500));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.erro()).not.toBeNull();

      beneficioServiceStub.listar.mockReturnValue(of(beneficiosMock));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.erro()).toBeNull();

    });

  });

  // -------------------------------------------------------------------------
  // Estado da lista
  // -------------------------------------------------------------------------

  describe('Estado da lista', () => {

    it('deve ter beneficios com 3 itens após carregar mock', () => {
      expect(component.beneficios().length).toBe(3);
    });

    it('deve conter benefícios ativos e inativos', () => {

      const ativos = component.beneficios().filter(b => b.ativo);
      const inativos = component.beneficios().filter(b => !b.ativo);

      expect(ativos.length).toBe(2);
      expect(inativos.length).toBe(1);

    });

    it('deve resetar o signal erro para null ao iniciar novo carregamento', async () => {

      beneficioServiceStub.listar.mockReturnValue(makeError(500));

      component.carregarBeneficios();

      await flushPromises();

      expect(component.erro()).not.toBeNull();

      beneficioServiceStub.listar.mockReturnValue(of([]));

      component.carregarBeneficios();

      expect(component.erro()).toBeNull();

    });

  });

});