import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, throwError } from 'rxjs';

import { EditarBeneficioComponent } from './editar-beneficio';
import { BeneficioService } from '../../../../core/services/beneficio.service';

// ---------------------------------------------------------------------------
// Helpers
// ---------------------------------------------------------------------------

function makeError(status: number, body: unknown = {}) {
  return throwError(() => ({ status, error: body }));
}

async function flushPromises(): Promise<void> {
  await new Promise(resolve => setTimeout(resolve));
}

// ---------------------------------------------------------------------------
// Stubs
// ---------------------------------------------------------------------------

const beneficioServiceStub = {
  buscarPorId: vi.fn(),
  atualizar: vi.fn(),
};

const routerStub = {
  navigate: vi.fn(),
};

const snackBarStub = {
  open: vi.fn(),
};

const activatedRouteStub = {
  snapshot: {
    paramMap: {
      get: vi.fn().mockReturnValue('1'),
    },
  },
};

// ---------------------------------------------------------------------------
// Suite
// ---------------------------------------------------------------------------

describe('EditarBeneficioComponent', () => {

  let component: EditarBeneficioComponent;
  let fixture: ComponentFixture<EditarBeneficioComponent>;

  beforeEach(async () => {

    vi.clearAllMocks();

    beneficioServiceStub.buscarPorId.mockReturnValue(of({
      id: 1,
      nome: 'Vale Refeição',
      descricao: 'Benefício alimentar',
      valor: 500,
      ativo: true,
    }));

    await TestBed.configureTestingModule({
      imports: [
        EditarBeneficioComponent,
        ReactiveFormsModule,
        NoopAnimationsModule
      ],
      providers: [
        { provide: BeneficioService, useValue: beneficioServiceStub },
        { provide: Router, useValue: routerStub },
        { provide: MatSnackBar, useValue: snackBarStub },
        { provide: ActivatedRoute, useValue: activatedRouteStub },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(EditarBeneficioComponent);
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

    it('deve inicializar erroDados como null após carregar', async () => {
      await flushPromises();
      expect(component.erroDados()).toBeNull();
    });

    it('deve inicializar loadingSalvar como false', () => {
      expect(component.loadingSalvar()).toBe(false);
    });

  });

  // -------------------------------------------------------------------------
  // carregarBeneficio()
  // -------------------------------------------------------------------------

  describe('carregarBeneficio()', () => {

    it('deve preencher o formulário com os dados retornados', async () => {

      await flushPromises();

      expect(component.form.value).toMatchObject({
        nome: 'Vale Refeição',
        descricao: 'Benefício alimentar',
        valor: 500,
      });

    });

    it('deve usar string vazia quando descricao é null', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(
        of({ id: 1, nome: 'Vale Refeição', descricao: null, valor: 500, ativo: true }),
      );

      component.carregarBeneficio();

      await flushPromises();

      expect(component.form.get('descricao')!.value).toBe('');

    });

    it('deve definir loadingDados como false após carregar', async () => {

      await flushPromises();

      expect(component.loadingDados()).toBe(false);

    });

    it('deve navegar para /erro-servico quando status 0', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(0));

      component.carregarBeneficio();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve navegar para /erro-servico quando status 503', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(503));

      component.carregarBeneficio();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/erro-servico']);

    });

    it('deve definir erroDados com "Benefício não encontrado." quando status 404', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(404));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).toBe('Benefício não encontrado.');

    });

    it('deve definir erroDados com mensagem genérica para outros erros', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(500));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.erroDados()).toBe('Não foi possível carregar o benefício.');

    });

    it('deve definir loadingDados como false após erro', async () => {

      beneficioServiceStub.buscarPorId.mockReturnValue(makeError(500));

      component.carregarBeneficio();

      await flushPromises();

      expect(component.loadingDados()).toBe(false);

    });

  });

  // -------------------------------------------------------------------------
  // salvar()
  // -------------------------------------------------------------------------

  describe('salvar() sucesso', () => {

    beforeEach(async () => {

      beneficioServiceStub.atualizar.mockReturnValue(of({}));

      component.form.get('nome')!.setValue('Nome Alterado');

      component.form.markAsDirty();

      await flushPromises();

    });

    it('deve chamar o serviço com payload correto', async () => {

      component.salvar();

      await flushPromises();

      expect(beneficioServiceStub.atualizar).toHaveBeenCalledWith(1, {
        nome: 'Nome Alterado',
        descricao: 'Benefício alimentar',
        valor: 500,
      });

    });

    it('deve navegar para /beneficios após sucesso', async () => {

      component.salvar();

      await flushPromises();

      expect(routerStub.navigate).toHaveBeenCalledWith(['/beneficios']);

    });

  });

});