package com.example.backend;

import com.example.backend.controller.BeneficioController;
import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.backend.exception.GlobalExceptionHandler;
import com.example.ejb.exception.InsufficientBalanceException;
import com.example.ejb.exception.TransferConflictException;
import com.example.ejb.exception.TransferException;
import com.example.ejb.model.Beneficio;
import com.example.ejb.service.BeneficioEjbService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração do BeneficioController usando MockMvc.
 *
 * Utiliza @WebMvcTest para carregar apenas a camada web (controllers, filters,
 * exception handlers), sem subir o contexto completo do Spring.
 * O BeneficioEjbService é mockado via @MockBean.
 */
@WebMvcTest(BeneficioController.class)
@Import(GlobalExceptionHandler.class)
@DisplayName("BeneficioController")
class BeneficioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BeneficioEjbService ejbService;

    // ──────────────────────────────────────────────────────────────
    // Fixtures reutilizáveis
    // ──────────────────────────────────────────────────────────────

    private Beneficio beneficioAtivo;
    private Beneficio beneficioInativo;

    @BeforeEach
    void setUp() {
        beneficioAtivo = new Beneficio();
        beneficioAtivo.setId(1L);
        beneficioAtivo.setNome("Vale Refeição");
        beneficioAtivo.setDescricao("Benefício de alimentação");
        beneficioAtivo.setValor(new BigDecimal("1000.00"));
        beneficioAtivo.setAtivo(true);
        beneficioAtivo.setVersion(0L);

        beneficioInativo = new Beneficio();
        beneficioInativo.setId(2L);
        beneficioInativo.setNome("Vale Transporte");
        beneficioInativo.setDescricao("Benefício de locomoção");
        beneficioInativo.setValor(new BigDecimal("300.00"));
        beneficioInativo.setAtivo(false);
        beneficioInativo.setVersion(1L);
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/v1/beneficios
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/beneficios — listarTodos")
    class ListarTodos {

        @Test
        @DisplayName("Deve retornar 200 com lista de benefícios")
        void deveRetornar200ComLista() throws Exception {
            when(ejbService.listarTodos()).thenReturn(List.of(beneficioAtivo, beneficioInativo));

            mockMvc.perform(get("/api/v1/beneficios")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].nome", is("Vale Refeição")))
                    .andExpect(jsonPath("$[0].valor", is(1000.00)))
                    .andExpect(jsonPath("$[0].ativo", is(true)))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].ativo", is(false)));
        }

        @Test
        @DisplayName("Deve retornar 200 com lista vazia quando não há benefícios")
        void deveRetornar200ComListaVazia() throws Exception {
            when(ejbService.listarTodos()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/beneficios")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Deve retornar 500 quando o serviço lança exceção inesperada")
        void deveRetornar500QuandoServicoFalha() throws Exception {
            when(ejbService.listarTodos()).thenThrow(new RuntimeException("Erro inesperado"));

            mockMvc.perform(get("/api/v1/beneficios")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.mensagem", is("Erro interno do servidor")));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/v1/beneficios/ativos
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/beneficios/ativos — listarAtivos")
    class ListarAtivos {

        @Test
        @DisplayName("Deve retornar 200 apenas com benefícios ativos")
        void deveRetornar200ApenasAtivos() throws Exception {
            when(ejbService.listarAtivos()).thenReturn(List.of(beneficioAtivo));

            mockMvc.perform(get("/api/v1/beneficios/ativos")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].ativo", is(true)));
        }

        @Test
        @DisplayName("Deve retornar 200 com lista vazia quando não há ativos")
        void deveRetornar200ComListaVazia() throws Exception {
            when(ejbService.listarAtivos()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/beneficios/ativos")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // GET /api/v1/beneficios/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("GET /api/v1/beneficios/{id} — buscarPorId")
    class BuscarPorId {

        @Test
        @DisplayName("Deve retornar 200 com o benefício quando encontrado")
        void deveRetornar200QuandoEncontrado() throws Exception {
            when(ejbService.buscarPorId(1L)).thenReturn(Optional.of(beneficioAtivo));

            mockMvc.perform(get("/api/v1/beneficios/1")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nome", is("Vale Refeição")))
                    .andExpect(jsonPath("$.descricao", is("Benefício de alimentação")))
                    .andExpect(jsonPath("$.valor", is(1000.00)))
                    .andExpect(jsonPath("$.ativo", is(true)));
        }

        @Test
        @DisplayName("Deve retornar 404 quando benefício não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            when(ejbService.buscarPorId(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/beneficios/99")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.mensagem", containsString("99")));
        }

        @Test
        @DisplayName("Deve retornar 404 com estrutura ApiError correta")
        void deveRetornar404ComEstruturaApiError() throws Exception {
            when(ejbService.buscarPorId(99L)).thenReturn(Optional.empty());

            mockMvc.perform(get("/api/v1/beneficios/99")
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.timestamp", notNullValue()))
                    .andExpect(jsonPath("$.status", is(404)))
                    .andExpect(jsonPath("$.erro", is("Not Found")))
                    .andExpect(jsonPath("$.path", is("/api/v1/beneficios/99")));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // POST /api/v1/beneficios
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/v1/beneficios — criar")
    class Criar {

        @Test
        @DisplayName("Deve retornar 201 com o benefício criado")
        void deveRetornar201ComBeneficioCriado() throws Exception {
            BeneficioRequest req = new BeneficioRequest("Vale Refeição", "Alimentação", new BigDecimal("1000.00"));
            when(ejbService.criar("Vale Refeição", "Alimentação", new BigDecimal("1000.00")))
                    .thenReturn(beneficioAtivo);

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.nome", is("Vale Refeição")));
        }

        @Test
        @DisplayName("Deve retornar 422 quando nome está em branco")
        void deveRetornar422QuandoNomeEmBranco() throws Exception {
            BeneficioRequest req = new BeneficioRequest("", "Alimentação", new BigDecimal("1000.00"));

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.nome", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando nome é nulo")
        void deveRetornar422QuandoNomeNulo() throws Exception {
            BeneficioRequest req = new BeneficioRequest(null, "Alimentação", new BigDecimal("1000.00"));

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.nome", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando valor é nulo")
        void deveRetornar422QuandoValorNulo() throws Exception {
            BeneficioRequest req = new BeneficioRequest("Vale Refeição", "Alimentação", null);

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.valor", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando valor é zero")
        void deveRetornar422QuandoValorZero() throws Exception {
            BeneficioRequest req = new BeneficioRequest("Vale Refeição", "Alimentação", BigDecimal.ZERO);

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.valor", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando nome excede 100 caracteres")
        void deveRetornar422QuandoNomeMuitoLongo() throws Exception {
            BeneficioRequest req = new BeneficioRequest("A".repeat(101), "desc", new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.nome", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 400 quando body está ausente")
        void deveRetornar400QuandoBodyAusente() throws Exception {
            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Não deve invocar o serviço quando a requisição é inválida")
        void naoDeveInvocarServicoQuandoInvalido() throws Exception {
            BeneficioRequest req = new BeneficioRequest("", null, null);

            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).criar(any(), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // PUT /api/v1/beneficios/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("PUT /api/v1/beneficios/{id} — atualizar")
    class Atualizar {

        @Test
        @DisplayName("Deve retornar 200 com o benefício atualizado")
        void deveRetornar200ComBeneficioAtualizado() throws Exception {
            BeneficioRequest req = new BeneficioRequest("Vale Refeição Atualizado", "Nova desc",
                    new BigDecimal("1200.00"));
            Beneficio atualizado = new Beneficio();
            atualizado.setId(1L);
            atualizado.setNome("Vale Refeição Atualizado");
            atualizado.setDescricao("Nova desc");
            atualizado.setValor(new BigDecimal("1200.00"));
            atualizado.setAtivo(true);

            when(ejbService.atualizar(eq(1L), eq("Vale Refeição Atualizado"), eq("Nova desc"),
                    eq(new BigDecimal("1200.00"))))
                    .thenReturn(atualizado);

            mockMvc.perform(put("/api/v1/beneficios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.nome", is("Vale Refeição Atualizado")))
                    .andExpect(jsonPath("$.valor", is(1200.00)));
        }

        @Test
        @DisplayName("Deve retornar 404 quando benefício não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            BeneficioRequest req = new BeneficioRequest("Nome", "desc", new BigDecimal("100.00"));
            when(ejbService.atualizar(any(), any(), any(), any()))
                    .thenThrow(new BeneficioNotFoundException(99L));

            mockMvc.perform(put("/api/v1/beneficios/99")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Deve retornar 422 quando dados inválidos")
        void deveRetornar422QuandoDadosInvalidos() throws Exception {
            BeneficioRequest req = new BeneficioRequest("", null, null);

            mockMvc.perform(put("/api/v1/beneficios/1")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).atualizar(any(), any(), any(), any());
        }
    }

    // ══════════════════════════════════════════════════════════════
    // DELETE /api/v1/beneficios/{id}
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("DELETE /api/v1/beneficios/{id} — desativar")
    class Desativar {

        @Test
        @DisplayName("Deve retornar 204 quando desativado com sucesso")
        void deveRetornar204QuandoDesativado() throws Exception {
            doNothing().when(ejbService).desativar(1L);

            mockMvc.perform(delete("/api/v1/beneficios/1"))
                    .andExpect(status().isNoContent());

            verify(ejbService).desativar(1L);
        }

        @Test
        @DisplayName("Deve retornar 404 quando benefício não encontrado")
        void deveRetornar404QuandoNaoEncontrado() throws Exception {
            doThrow(new BeneficioNotFoundException(99L)).when(ejbService).desativar(99L);

            mockMvc.perform(delete("/api/v1/beneficios/99"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status", is(404)));
        }

        @Test
        @DisplayName("Deve invocar o serviço com o ID correto")
        void deveInvocarServicoComIdCorreto() throws Exception {
            doNothing().when(ejbService).desativar(5L);

            mockMvc.perform(delete("/api/v1/beneficios/5"))
                    .andExpect(status().isNoContent());

            verify(ejbService, times(1)).desativar(5L);
        }
    }

    // ══════════════════════════════════════════════════════════════
    // POST /api/v1/beneficios/transferencia
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("POST /api/v1/beneficios/transferencia — transferir")
    class Transferir {

        @Test
        @DisplayName("Deve retornar 204 quando transferência bem-sucedida")
        void deveRetornar204QuandoSucesso() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("100.00"));
            doNothing().when(ejbService).transfer(1L, 2L, new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Deve retornar 422 quando saldo insuficiente")
        void deveRetornar422QuandoSaldoInsuficiente() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("9999.00"));
            doThrow(new InsufficientBalanceException(1L, new BigDecimal("500.00"), new BigDecimal("9999.00")))
                    .when(ejbService).transfer(1L, 2L, new BigDecimal("9999.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.codigo", is("INSUFFICIENT_BALANCE")))
                    .andExpect(jsonPath("$.detalhes.contaId", is(1)))
                    .andExpect(jsonPath("$.detalhes.saldoAtual", is(500.00)))
                    .andExpect(jsonPath("$.detalhes.solicitado", is(9999.00)));
        }

        @Test
        @DisplayName("Deve retornar 409 quando há conflito de concorrência")
        void deveRetornar409QuandoConflito() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("100.00"));
            doThrow(new TransferConflictException("Conflito"))
                    .when(ejbService).transfer(1L, 2L, new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.detalhes.codigo", is("TRANSFER_CONFLICT")));
        }

        @Test
        @DisplayName("Deve retornar 400 quando regra de negócio é violada")
        void deveRetornar400QuandoRegraViolada() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("100.00"));
            doThrow(new TransferException("Conta de origem inativa"))
                    .when(ejbService).transfer(1L, 2L, new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.detalhes.codigo", is("TRANSFER_ERROR")))
                    .andExpect(jsonPath("$.mensagem", is("Conta de origem inativa")));
        }

        @Test
        @DisplayName("Deve retornar 422 quando fromId é nulo")
        void deveRetornar422QuandoFromIdNulo() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(null, 2L, new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.fromId", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando toId é nulo")
        void deveRetornar422QuandoToIdNulo() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, null, new BigDecimal("100.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.toId", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando valor da transferência é zero")
        void deveRetornar422QuandoValorZero() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, BigDecimal.ZERO);

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.amount", notNullValue()));
        }

        @Test
        @DisplayName("Deve retornar 422 quando valor da transferência é nulo")
        void deveRetornar422QuandoValorNulo() throws Exception {
            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, null);

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity())
                    .andExpect(jsonPath("$.detalhes.erros.amount", notNullValue()));
        }

        @Test
        @DisplayName("Não deve invocar serviço quando IDs são nulos")
        void naoDeveInvocarServicoQuandoIdsNulos() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(null, null, BigDecimal.TEN);

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).transfer(any(), any(), any());
        }

        @Test
        @DisplayName("Não deve invocar serviço quando valor é nulo")
        void naoDeveInvocarServicoQuandoValorNulo() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, null);

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).transfer(any(), any(), any());
        }

        @Test
        @DisplayName("Não deve invocar serviço quando valor é zero")
        void naoDeveInvocarServicoQuandoValorZero() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, BigDecimal.ZERO);

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).transfer(any(), any(), any());
        }

        @Test
        @DisplayName("Não deve invocar serviço quando valor é negativo")
        void naoDeveInvocarServicoQuandoValorNegativo() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("-10"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).transfer(any(), any(), any());
        }

        @Test
        @DisplayName("Não deve invocar serviço quando valor tem mais de duas casas decimais")
        void naoDeveInvocarServicoQuandoMaisDeDuasCasas() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("10.12345"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isUnprocessableEntity());

            verify(ejbService, never()).transfer(any(), any(), any());
        }

        @Test
        @DisplayName("Deve invocar serviço quando requisição é válida")
        void deveInvocarServicoQuandoValido() throws Exception {

            TransferenciaRequest req = new TransferenciaRequest(1L, 2L, new BigDecimal("10.00"));

            mockMvc.perform(post("/api/v1/beneficios/transferencia")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(req)))
                    .andExpect(status().isNoContent());

            verify(ejbService).transfer(1L, 2L, new BigDecimal("10.00"));
        }
    }

    // ══════════════════════════════════════════════════════════════
    // Content-Type e Accept
    // ══════════════════════════════════════════════════════════════

    @Nested
    @DisplayName("Content-Type e Accept")
    class ContentType {

        @Test
        @DisplayName("Deve retornar Content-Type application/json nos GETs")
        void deveRetornarContentTypeJson() throws Exception {
            when(ejbService.listarTodos()).thenReturn(List.of());

            mockMvc.perform(get("/api/v1/beneficios"))
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @Test
        @DisplayName("Deve retornar 415 quando Content-Type do POST não é JSON")
        void deveRetornar415QuandoContentTypeErrado() throws Exception {
            mockMvc.perform(post("/api/v1/beneficios")
                    .contentType(MediaType.TEXT_PLAIN)
                    .content("texto simples"))
                    .andExpect(status().isUnsupportedMediaType());
        }
    }
}