package br.com.fiap.api_rest.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "TB_ESTOQUES",
        uniqueConstraints = @UniqueConstraint(columnNames = {"id_produto", "id_deposito"}))
public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @JoinColumn(name = "id_produto")
    private Produto produto;
    @ManyToOne
    @JoinColumn(name = "id_deposito")
    private Deposito deposito;
    private int quantidade;
    @OneToMany(mappedBy = "estoque", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MovimentacaoEstoque> movimentacoes;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }

    public Deposito getDeposito() {
        return deposito;
    }

    public void setDeposito(Deposito deposito) {
        this.deposito = deposito;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public List<MovimentacaoEstoque> getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(List<MovimentacaoEstoque> movimentacoes) {
        this.movimentacoes = movimentacoes;
    }
}
