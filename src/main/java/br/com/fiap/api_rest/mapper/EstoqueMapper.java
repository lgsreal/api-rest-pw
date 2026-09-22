package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.dto.MovimentacaoResponse;
import br.com.fiap.api_rest.dto.SaldoEstoqueResponse;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.MovimentacaoEstoque;
import org.springframework.stereotype.Component;

@Component
public class EstoqueMapper {
    public SaldoEstoqueResponse estoqueToSaldo(Estoque estoque) {
        return new SaldoEstoqueResponse(
                estoque.getId(),
                estoque.getProduto().getId(),
                estoque.getDeposito().getId(),
                estoque.getQuantidade());
    }

    public MovimentacaoResponse movimentacaoToResponse(MovimentacaoEstoque movimentacao) {
        return new MovimentacaoResponse(
                movimentacao.getId(),
                movimentacao.getEstoque().getId(),
                movimentacao.getTipo(),
                movimentacao.getQuantidade(),
                movimentacao.getDataHora());
    }
}
