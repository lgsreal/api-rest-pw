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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class EstoqueService {
    private static final int MAX_TENTATIVAS = 3;

    private final EstoqueRepository estoqueRepository;
    private final MovimentacaoEstoqueRepository movimentacaoRepository;
    private final ProdutoRepository produtoRepository;
    private final DepositoRepository depositoRepository;
    private final EstoqueMapper estoqueMapper;
    private final TransactionOperations transactionTemplate;

    @Autowired
    public EstoqueService(EstoqueRepository estoqueRepository,
                          MovimentacaoEstoqueRepository movimentacaoRepository,
                          ProdutoRepository produtoRepository,
                          DepositoRepository depositoRepository,
                          EstoqueMapper estoqueMapper,
                          TransactionOperations transactionTemplate) {
        this.estoqueRepository = estoqueRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.produtoRepository = produtoRepository;
        this.depositoRepository = depositoRepository;
        this.estoqueMapper = estoqueMapper;
        this.transactionTemplate = transactionTemplate;
    }

    // Cada tentativa roda numa nova transação: lock pessimista no saldo existente e
    // retry quando duas transações criam o mesmo par produto/depósito ao mesmo tempo.
    public MovimentacaoResponse registrarMovimentacao(MovimentacaoRequest request) {
        for (int tentativa = 1; tentativa <= MAX_TENTATIVAS; tentativa++) {
            try {
                return transactionTemplate.execute(status -> executarMovimentacao(request));
            } catch (DataIntegrityViolationException | PessimisticLockingFailureException e) {
                if (tentativa == MAX_TENTATIVAS) {
                    throw e;
                }
            }
        }
        throw new IllegalStateException("Falha ao registrar movimentação");
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

    private MovimentacaoResponse executarMovimentacao(MovimentacaoRequest request) {
        Produto produto = produtoRepository.findById(request.produtoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado"));
        Deposito deposito = depositoRepository.findById(request.depositoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Depósito não encontrado"));
        Estoque estoque = estoqueRepository
                .findByProdutoIdAndDepositoIdForUpdate(produto.getId(), deposito.getId())
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
