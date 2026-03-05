package com.example.ejb.service;

import com.example.ejb.exception.InsufficientBalanceException;
import com.example.ejb.exception.TransferConflictException;
import com.example.ejb.exception.TransferException;
import com.example.ejb.model.Beneficio;
import com.example.ejb.repository.BeneficioRepository;
import jakarta.ejb.Stateless;
import jakarta.persistence.OptimisticLockException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Serviço EJB responsável pelas operações de domínio relacionadas a benefícios.
 * <p>
 * Esta classe contém a lógica de negócio para:
 * <ul>
 *     <li>Consulta de benefícios</li>
 *     <li>Persistência de benefícios</li>
 *     <li>Atualização e desativação lógica</li>
 *     <li>Transferência de valores entre benefícios com controle de concorrência</li>
 * </ul>
 *
 * O serviço utiliza:
 * <ul>
 *     <li>Arquitetura EJB Stateless</li>
 *     <li>Transações gerenciadas via Spring Transactional</li>
 *     <li>Retry automático para conflitos de concorrência otimista ou pessimista</li>
 * </ul>
 */
@Stateless
public class BeneficioEjbService {

    /**
     * Repositório de acesso aos dados dos benefícios.
     * Deve ser injetado via setter.
     */
    private BeneficioRepository repository;

    /**
     * Define o repositório utilizado pelo serviço.
     *
     * @param repository repositório de benefícios
     */
    public void setRepository(BeneficioRepository repository) {
        this.repository = repository;
    }

    // ─────────────────────────────────────────────
    // Consultas
    // ─────────────────────────────────────────────

    /**
     * Lista todos os benefícios cadastrados.
     *
     * @return lista de benefícios
     */
    @Transactional(readOnly = true)
    public List<Beneficio> listarTodos() {
        return repository.findAll();
    }

    /**
     * Lista apenas benefícios ativos.
     *
     * @return lista de benefícios ativos
     */
    @Transactional(readOnly = true)
    public List<Beneficio> listarAtivos() {
        return repository.findAtivos();
    }

    /**
     * Busca um benefício pelo identificador.
     *
     * @param id identificador do benefício
     * @return benefício encontrado ou Optional vazio
     */
    @Transactional(readOnly = true)
    public Optional<Beneficio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    // ─────────────────────────────────────────────
    // Persistência
    // ─────────────────────────────────────────────

    /**
     * Cria um novo benefício.
     *
     * @param nome nome do benefício
     * @param descricao descrição do benefício
     * @param valor valor inicial
     * @return benefício criado
     */
    @Transactional(rollbackFor = Exception.class)
    public Beneficio criar(String nome, String descricao, BigDecimal valor) {
        Beneficio b = new Beneficio(nome, descricao, valor);
        return repository.save(b);
    }

    /**
     * Atualiza um benefício existente.
     *
     * @param id identificador do benefício
     * @param nome novo nome
     * @param descricao nova descrição
     * @param valor novo valor
     * @return benefício atualizado
     */
    @Transactional(rollbackFor = Exception.class)
    public Beneficio atualizar(Long id, String nome, String descricao, BigDecimal valor) {
        Beneficio b = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado: " + id));

        b.setNome(nome);
        b.setDescricao(descricao);
        b.setValor(valor);

        return repository.save(b);
    }

    /**
     * Desativa logicamente um benefício (soft delete).
     *
     * @param id identificador do benefício
     */
    @Transactional
    public void desativar(Long id) {
        Beneficio b = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Benefício não encontrado: " + id));

        b.setAtivo(false);
        repository.save(b);
    }

    // ─────────────────────────────────────────────
    // Transferência com controle de concorrência
    // ─────────────────────────────────────────────

    /** Número máximo de tentativas para retry em conflitos de lock */
    private static final int MAX_TRANSFER_RETRIES = 3;

    /** Tempo base de espera entre retries (ms) */
    private static final long RETRY_DELAY_MS = 50;

    /**
     * Executa transferência de valor entre benefícios com mecanismo de retry.
     *
     * @param fromId identificador de origem
     * @param toId identificador de destino
     * @param amount valor a ser transferido
     */
    @Transactional(rollbackFor = {
            TransferException.class,
            InsufficientBalanceException.class,
            TransferConflictException.class,
            Exception.class
    })
    public void transfer(Long fromId, Long toId, BigDecimal amount) {

        validateTransferInput(fromId, toId, amount);

        int retryCount = 0;

        while (retryCount < MAX_TRANSFER_RETRIES) {
            try {
                performTransfer(fromId, toId, amount);

                logSuccess(fromId, toId, amount);
                return;

            } catch (OptimisticLockException |
                     jakarta.persistence.PessimisticLockException e) {

                retryCount++;

                if (retryCount >= MAX_TRANSFER_RETRIES) {
                    throw new TransferConflictException(
                            String.format("Falha após %d tentativas", MAX_TRANSFER_RETRIES), e);
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS * retryCount);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new TransferException("Transferência interrompida", ie);
                }
            }
        }
    }

    /**
     * Valida os parâmetros de entrada da transferência.
     *
     * @param fromId origem
     * @param toId destino
     * @param amount valor
     */
    private void validateTransferInput(Long fromId, Long toId, BigDecimal amount) {
        if (fromId == null || toId == null) {
            throw new TransferException("IDs de origem e destino não podem ser nulos");
        }

        if (fromId.equals(toId)) {
            throw new TransferException("Não é permitido transferir para a mesma conta");
        }

        if (amount == null) {
            throw new TransferException("Valor de transferência não pode ser nulo");
        }

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new TransferException("Valor de transferência deve ser positivo");
        }

        if (amount.scale() > 2) {
            throw new TransferException("Valor de transferência não pode ter mais de 2 casas decimais");
        }
    }

    /**
     * Executa a lógica principal de transferência com lock pessimista.
     *
     * @param fromId origem
     * @param toId destino
     * @param amount valor transferido
     */
    private void performTransfer(Long fromId, Long toId, BigDecimal amount) {

        Beneficio from = repository.findByIdForUpdate(fromId)
                .orElseThrow(() -> new TransferException("Benefício origem não encontrado: " + fromId));

        Beneficio to = repository.findByIdForUpdate(toId)
                .orElseThrow(() -> new TransferException("Benefício destino não encontrado: " + toId));

        if (!from.getAtivo()) {
            throw new TransferException("Conta de origem inativa: " + fromId);
        }

        if (!to.getAtivo()) {
            throw new TransferException("Conta de destino inativa: " + toId);
        }

        if (from.getValor().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(fromId, from.getValor(), amount);
        }

        BigDecimal newFromBalance = from.getValor().subtract(amount);
        BigDecimal newToBalance = to.getValor().add(amount);

        from.setValor(newFromBalance);
        to.setValor(newToBalance);

        repository.save(from);
        repository.save(to);

        logTransfer(fromId, toId, amount, newFromBalance, newToBalance);
    }

    /**
     * Registra log da transferência realizada com detalhes do saldo atualizado.
     */
    private void logTransfer(Long fromId, Long toId, BigDecimal amount,
                             BigDecimal newFromBalance, BigDecimal newToBalance) {

        System.out.printf(
                "[TRANSFER_SUCCESS] De: %d (novo saldo: %s) | Para: %d (novo saldo: %s) | Valor: %s%n",
                fromId, newFromBalance, toId, newToBalance, amount);
    }

    /**
     * Registra log simplificado de sucesso da transferência.
     */
    private void logSuccess(Long fromId, Long toId, BigDecimal amount) {
        System.out.printf("[TRANSFER_SUCCESS] De: %d | Para: %d | Valor: %s%n",
                fromId, toId, amount);
    }
}