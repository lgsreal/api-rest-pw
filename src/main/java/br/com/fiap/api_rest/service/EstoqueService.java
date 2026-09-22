package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.MovimentacaoRequest;
import br.com.fiap.api_rest.dto.MovimentacaoResponse;
import br.com.fiap.api_rest.dto.SaldoEstoqueResponse;
import br.com.fiap.api_rest.mapper.EstoqueMapper;
import br.com.fiap.api_rest.model.Deposito;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.MovimentacaoEstoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.DepositoRepository;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.MovimentacaoEstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstoqueService {
    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final DepositoRepository depositoRepository;
    private final EstoqueMapper estoqueMapper;

    @Autowired
    public EstoqueService(EstoqueRepository estoqueRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository,
                          ProdutoRepository produtoRepository,
                          DepositoRepository depositoRepository,
                          EstoqueMapper estoqueMapper) {
        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.depositoRepository = depositoRepository;
        this.estoqueMapper = estoqueMapper;
    }

    @Transactional
    public MovimentacaoResponse registrarMovimentacao(MovimentacaoRequest request) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        Deposito deposito = depositoRepository.findById(request.depositoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depósito não encontrado"));
        Estoque estoque = estoqueRepository
                .findByProdutoIdAndDepositoId(produto.getId(), deposito.getId())
                .orElseGet(() -> {
                    Estoque novoEstoque = new Estoque();
                    novoEstoque.setProduto(produto);
                    novoEstoque.setDeposito(deposito);
                    novoEstoque.setQuantidade(0);
                    return novoEstoque;
                });
        estoque.setQuantidade(calcularNovoSaldo(estoque.getQuantidade(), request));
        estoque = estoqueRepository.save(estoque);

        MovimentacaoEstoque movimentacao = new MovimentacaoEstoque();
        movimentacao.setEstoque(estoque);
        movimentacao.setTipo(request.tipo());
        movimentacao.setQuantidade(request.quantidade());
        movimentacao.setDataHora(LocalDateTime.now());
        return estoqueMapper.movimentacaoToResponse(movimentacaoRepository.save(movimentacao));
    }

    public SaldoEstoqueResponse consultarSaldo(UUID produtoId, UUID depositoId) {
        Estoque estoque = estoqueRepository.findByProdutoIdAndDepositoId(produtoId, depositoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Não há estoque deste produto neste depósito"));
        return estoqueMapper.estoqueToSaldo(estoque);
    }

    public List<MovimentacaoResponse> consultarHistorico(UUID estoqueId) {
        return movimentacaoRepository.findByEstoqueIdOrderByDataHoraDesc(estoqueId)
                .stream()
                .map(estoqueMapper::movimentacaoToResponse)
                .collect(Collectors.toList());
    }

    private int calcularNovoSaldo(int saldoAtual, MovimentacaoRequest request) {
        return switch (request.tipo()) {
            case ENTRADA -> saldoAtual + request.quantidade();
            case SAIDA -> {
                int novoSaldo = saldoAtual - request.quantidade();
                if (novoSaldo < 0) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                            "Saldo insuficiente para a saída");
                }
                yield novoSaldo;
            }
            case AJUSTE -> request.quantidade();
        };
    }
}
