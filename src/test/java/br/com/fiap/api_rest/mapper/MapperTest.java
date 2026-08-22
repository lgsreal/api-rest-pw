package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.dto.CarrinhoLista;
import br.com.fiap.api_rest.dto.CarrinhoResponse;
import br.com.fiap.api_rest.dto.EstoqueLista;
import br.com.fiap.api_rest.dto.EstoqueResponse;
import br.com.fiap.api_rest.dto.ProdutoLista;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.model.Carrinho;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.ItemCarrinho;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.Usuario;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MapperTest {

    private final ProdutoMapper produtoMapper = new ProdutoMapper();
    private final EstoqueMapper estoqueMapper = new EstoqueMapper();
    private final CarrinhoMapper carrinhoMapper = new CarrinhoMapper();

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    private Produto produto() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        produto.setNome("Cha verde");
        produto.setPreco(new BigDecimal("10.00"));
        return produto;
    }

    @Test
    void deveMapearProduto() {
        Produto produto = produto();

        ProdutoResponse response = produtoMapper.produtoToResponse(produto);
        ProdutoLista lista = produtoMapper.produtoToProdutoLista(produto);

        assertThat(response.id()).isEqualTo(produto.getId());
        assertThat(response.nome()).isEqualTo("Cha verde");
        assertThat(response.link()).isNotNull();
        assertThat(lista.nome()).isEqualTo("Cha verde");
        assertThat(lista.link().getHref()).contains(produto.getId().toString());
    }

    @Test
    void deveMapearEstoque() {
        Produto produto = produto();
        Estoque estoque = new Estoque();
        estoque.setId(UUID.randomUUID());
        estoque.setProduto(produto);
        estoque.setQuantidade(7);
        estoque.setQuantidadeMinima(2);

        EstoqueResponse response = estoqueMapper.estoqueToResponse(estoque);
        EstoqueLista lista = estoqueMapper.estoqueToEstoqueLista(estoque);

        assertThat(response.produtoId()).isEqualTo(produto.getId());
        assertThat(response.produtoNome()).isEqualTo("Cha verde");
        assertThat(response.quantidade()).isEqualTo(7);
        assertThat(response.quantidadeMinima()).isEqualTo(2);
        assertThat(lista.link().getHref()).contains(estoque.getId().toString());
    }

    @Test
    void deveMapearEstoqueSemProduto() {
        Estoque estoque = new Estoque();
        estoque.setId(UUID.randomUUID());

        EstoqueResponse response = estoqueMapper.estoqueToResponse(estoque);

        assertThat(response.produtoId()).isNull();
        assertThat(response.produtoNome()).isNull();
    }

    @Test
    void deveMapearCarrinhoComTotal() {
        Usuario usuario = new Usuario("admin", "senha", UserRole.ADMIN);
        usuario.setId(UUID.randomUUID());
        Carrinho carrinho = new Carrinho();
        carrinho.setId(UUID.randomUUID());
        carrinho.setUsuario(usuario);
        ItemCarrinho item = new ItemCarrinho();
        item.setId(UUID.randomUUID());
        item.setProduto(produto());
        item.setQuantidade(3);
        item.setPrecoUnitario(new BigDecimal("10.00"));
        carrinho.adicionarItem(item);

        CarrinhoResponse response = carrinhoMapper.carrinhoToResponse(carrinho);
        CarrinhoLista lista = carrinhoMapper.carrinhoToCarrinhoLista(carrinho);

        assertThat(response.usuarioId()).isEqualTo(usuario.getId());
        assertThat(response.itens()).hasSize(1);
        assertThat(response.itens().getFirst().subtotal()).isEqualByComparingTo("30.00");
        assertThat(response.total()).isEqualByComparingTo("30.00");
        assertThat(lista.quantidadeItens()).isEqualTo(1);
        assertThat(lista.link().getHref()).contains(carrinho.getId().toString());
    }
}
