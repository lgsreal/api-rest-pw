package br.com.fiap.api_rest.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "TB_ESTOQUES")
public class Estoque {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private int quantidade;
    private int quantidadeMinima;
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "id_produto", unique = true)
    private Produto produto;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public int getQuantidadeMinima() {
        return quantidadeMinima;
    }

    public void setQuantidadeMinima(int quantidadeMinima) {
        this.quantidadeMinima = quantidadeMinima;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
