package com.example.backend.exception;

import com.example.backend.dto.ApiError;
import com.example.ejb.exception.InsufficientBalanceException;
import com.example.ejb.exception.TransferConflictException;
import com.example.ejb.exception.TransferException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handler global de exceções da aplicação.
 * <p>
 * Centraliza o tratamento de erros lançados pela camada de controle e serviço,
 * retornando respostas padronizadas no formato {@link ApiError}.
 * </p>
 *
 * Responsabilidades:
 * <ul>
 *     <li>Interceptar exceções da aplicação</li>
 *     <li>Registrar erros inesperados em log</li>
 *     <li>Retornar respostas HTTP padronizadas</li>
 * </ul>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Logger utilizado para registrar exceções inesperadas.
     */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Constrói um objeto {@link ApiError} com os dados da exceção tratada.
     *
     * @param status código HTTP da resposta
     * @param mensagem mensagem descritiva do erro
     * @param path endpoint onde o erro ocorreu
     * @param detalhes informações adicionais sobre o erro (opcional)
     * @return objeto estruturado de erro da API
     */
    private ApiError buildError(
            HttpStatus status,
            String mensagem,
            String path,
            Map<String, Object> detalhes
    ) {
        return new ApiError(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem,
                path,
                detalhes
        );
    }

    /**
     * Trata erro quando um benefício não é encontrado.
     * Retorna HTTP 404 com mensagem descritiva.
     */
    @ExceptionHandler(BeneficioNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            BeneficioNotFoundException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildError(
                        HttpStatus.NOT_FOUND,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    /**
     * Trata exceção de saldo insuficiente durante transferência.
     * Retorna HTTP 422 com detalhes adicionais da operação.
     */
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ApiError> handleInsufficientBalance(
            InsufficientBalanceException ex,
            HttpServletRequest request) {

        Map<String, Object> detalhes = Map.of(
                "codigo",     "INSUFFICIENT_BALANCE",
                "contaId",    ex.getAccountId(),
                "saldoAtual", ex.getCurrentBalance(),
                "solicitado", ex.getRequestedAmount()
        );

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        ex.getMessage(),
                        request.getRequestURI(),
                        detalhes
                ));
    }

    /**
     * Trata conflito de atualização causado por concorrência otimista.
     * Normalmente ocorre quando duas transações tentam modificar o mesmo recurso simultaneamente.
     */
    @ExceptionHandler(TransferConflictException.class)
    public ResponseEntity<ApiError> handleTransferConflict(
            TransferConflictException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildError(
                        HttpStatus.CONFLICT,
                        "Conflito ao executar transferência. Tente novamente.",
                        request.getRequestURI(),
                        Map.of("codigo", "TRANSFER_CONFLICT")
                ));
    }

    /**
     * Trata erros de regra de negócio relacionados à transferência.
     */
    @ExceptionHandler(TransferException.class)
    public ResponseEntity<ApiError> handleTransferError(
            TransferException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI(),
                        Map.of("codigo", "TRANSFER_ERROR")
                ));
    }

    /**
     * Trata requisições com parâmetros inválidos ou corpo malformado.
     */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildError(
                        HttpStatus.BAD_REQUEST,
                        ex.getMessage(),
                        request.getRequestURI(),
                        null
                ));
    }

    /**
     * Trata erros de validação Bean Validation (@Valid).
     * Extrai os erros de campo e retorna em formato estruturado.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        Map<String, String> erros = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        f -> f.getDefaultMessage() != null ? f.getDefaultMessage() : "inválido",
                        (a, b) -> a
                ));

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                .body(buildError(
                        HttpStatus.UNPROCESSABLE_ENTITY,
                        "Erro de validação",
                        request.getRequestURI(),
                        Map.of("erros", erros)
                ));
    }

    /**
     * Trata exceções inesperadas não mapeadas explicitamente.
     * Registra o erro no log e retorna resposta genérica 500.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Erro inesperado em [{}] {}: {}",
                request.getMethod(), request.getRequestURI(), ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(buildError(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        "Erro interno do servidor",
                        request.getRequestURI(),
                        null
                ));
    }
}