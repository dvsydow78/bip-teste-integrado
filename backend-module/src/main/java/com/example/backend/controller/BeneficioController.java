package com.example.backend.controller;

import com.example.backend.dto.BeneficioRequest;
import com.example.backend.dto.BeneficioResponse;
import com.example.backend.dto.TransferenciaRequest;
import com.example.backend.exception.BeneficioNotFoundException;
import com.example.ejb.service.BeneficioEjbService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller responsável pela gestão de benefícios.
 * <p>
 * Disponibiliza endpoints para operações de CRUD, consulta de benefícios
 * e transferência de valores entre benefícios.
 * </p>
 */
@RestController
@RequestMapping(value = "/api/v1/beneficios", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Benefícios", description = "CRUD e transferência de benefícios")
public class BeneficioController {

    private final BeneficioEjbService ejbService;

    public BeneficioController(BeneficioEjbService ejbService) {
        this.ejbService = ejbService;
    }

    /**
     * Lista todos os benefícios cadastrados.
     *
     * @return lista de benefícios convertidos para DTO de resposta
     */
    @GetMapping
    @Operation(summary = "Lista todos os benefícios")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public List<BeneficioResponse> listarTodos() {
        return ejbService.listarTodos()
                .stream()
                .map(BeneficioResponse::from)
                .toList();
    }

    /**
     * Lista apenas os benefícios que estão ativos.
     *
     * @return lista de benefícios ativos
     */
    @GetMapping("/ativos")
    @Operation(summary = "Lista apenas benefícios ativos")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public List<BeneficioResponse> listarAtivos() {
        return ejbService.listarAtivos()
                .stream()
                .map(BeneficioResponse::from)
                .toList();
    }

    /**
     * Busca um benefício pelo seu identificador.
     *
     * @param id identificador do benefício
     * @return benefício encontrado ou exceção caso não exista
     */
    @GetMapping("/{id}")
    @Operation(summary = "Busca um benefício por ID")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public BeneficioResponse buscarPorId(@PathVariable Long id) {
        return ejbService.buscarPorId(id)
                .map(BeneficioResponse::from)
                .orElseThrow(() -> new BeneficioNotFoundException(id));
    }

    /**
     * Cria um novo benefício com base nos dados informados.
     *
     * @param req dados do benefício a ser criado
     * @return resposta contendo o benefício criado
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cria um novo benefício")
    @ApiResponse(responseCode = "201", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<BeneficioResponse> criar(@Valid @RequestBody BeneficioRequest req) {
        var criado = ejbService.criar(req.nome(), req.descricao(), req.valor());
        return ResponseEntity.status(HttpStatus.CREATED).body(BeneficioResponse.from(criado));
    }

    /**
     * Atualiza os dados de um benefício existente.
     *
     * @param id  identificador do benefício
     * @param req dados atualizados do benefício
     * @return benefício atualizado
     */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Atualiza um benefício existente")
    @ApiResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public BeneficioResponse atualizar(@PathVariable Long id,
                                       @Valid @RequestBody BeneficioRequest req) {
        return BeneficioResponse.from(
                ejbService.atualizar(id, req.nome(), req.descricao(), req.valor()));
    }

    /**
     * Desativa logicamente um benefício (soft delete).
     *
     * @param id identificador do benefício a ser desativado
     * @return resposta sem conteúdo
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Desativa um benefício (soft delete)")
    @ApiResponse(responseCode = "204", content = @Content)
    @ApiResponse(responseCode = "404", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        ejbService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Realiza transferência de valor entre dois benefícios.
     *
     * @param req dados da transferência contendo origem, destino e valor
     * @return resposta sem conteúdo em caso de sucesso
     */
    @PostMapping(value = "/transferencia", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Transfere valor entre dois benefícios")
    @ApiResponse(responseCode = "204", content = @Content)
    @ApiResponse(responseCode = "400", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "409", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    @ApiResponse(responseCode = "422", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
    public ResponseEntity<Void> transferir(@Valid @RequestBody TransferenciaRequest req) {
        ejbService.transfer(req.fromId(), req.toId(), req.amount());
        return ResponseEntity.noContent().build();
    }
}