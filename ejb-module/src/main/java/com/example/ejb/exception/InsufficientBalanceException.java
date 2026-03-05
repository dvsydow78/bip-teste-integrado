package com.example.ejb.exception;

import java.math.BigDecimal;

/**
 * Exceção lançada quando uma transferência falha por saldo insuficiente.
 * <p>
 * Esta exceção estende {@link TransferException} e contém informações detalhadas
 * sobre a conta envolvida na operação, o saldo atual disponível e o valor solicitado
 * para transferência.
 * </p>
 */
public class InsufficientBalanceException extends TransferException {

    /** Identificador da conta afetada pela operação */
    private final Long accountId;

    /** Saldo atual disponível na conta */
    private final BigDecimal currentBalance;

    /** Valor solicitado para transferência */
    private final BigDecimal requestedAmount;

    /**
     * Constrói a exceção com os detalhes da operação que falhou.
     *
     * @param accountId identificador da conta
     * @param currentBalance saldo disponível no momento da operação
     * @param requestedAmount valor solicitado para transferência
     */
    public InsufficientBalanceException(Long accountId, BigDecimal currentBalance, BigDecimal requestedAmount) {
        super(String.format(
            "Saldo insuficiente na conta %d. Saldo atual: %s | Solicitado: %s",
            accountId, currentBalance, requestedAmount));
        this.accountId = accountId;
        this.currentBalance = currentBalance;
        this.requestedAmount = requestedAmount;
    }

    /** @return identificador da conta */
    public Long getAccountId() { return accountId; }

    /** @return saldo atual disponível */
    public BigDecimal getCurrentBalance() { return currentBalance; }

    /** @return valor solicitado para transferência */
    public BigDecimal getRequestedAmount() { return requestedAmount; }
}
