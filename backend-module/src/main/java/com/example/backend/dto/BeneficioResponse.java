package com.example.backend.dto;

import com.example.ejb.model.Beneficio;
import java.math.BigDecimal;

/**
 * DTO de resposta contendo os dados de um benefício.
 * <p>
 * Utilizado para expor as informações do benefício nas respostas da API,
 * evitando o acoplamento direto com a entidade de domínio.
 * </p>
 *
 * Campos retornados:
 * <ul>
 *     <li>id - identificador do benefício</li>
 *     <li>nome - nome do benefício</li>
 *     <li>descricao - descrição do benefício</li>
 *     <li>valor - valor monetário do benefício</li>
 *     <li>ativo - indica se o benefício está ativo</li>
 *     <li>version - controle de versão para concorrência otimista</li>
 * </ul>
 */
public record BeneficioResponse(
    Long id,
    String nome,
    String descricao,
    BigDecimal valor,
    Boolean ativo,
    Long version
) {

    /**
     * Converte uma entidade {@link Beneficio} em {@link BeneficioResponse}.
     *
     * @param b entidade de domínio a ser convertida
     * @return DTO de resposta correspondente aos dados da entidade
     */
    public static BeneficioResponse from(Beneficio b) {
        return new BeneficioResponse(
            b.getId(),
            b.getNome(),
            b.getDescricao(),
            b.getValor(),
            b.getAtivo(),
            b.getVersion()
        );
    }
}