package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.CarrinhoRequest;
import br.com.fiap.api_rest.dto.ItemCarrinhoRequest;
import br.com.fiap.api_rest.exception.EstoqueInsuficienteException;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.mapper.CarrinhoMapper;
import br.com.fiap.api_rest.model.Carrinho;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.Usuario;
import br.com.fiap.api_rest.repository.CarrinhoRepository;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private CarrinhoMapper carrinhoMapper;
    @InjectMocks
    private CarrinhoService carrinhoService;

    private final UUID usuarioId = UUID.randomUUID();
    private final UUID produtoId = UUID.randomUUID();

    private Usuario usuario() {
        Usuario usuario = new Usuario("admin", "senha", UserRole.ADMIN);
        usuario.setId(usuarioId);
        return usuario;
    }

    private Produto produto() {
        Produto produto = new Produto();
        produto.setId(produtoId);
        produto.setNome("Cha verde");
        produto.setPreco(new BigDecimal("10.00"));
        return produto;
    }

    private Estoque estoque(int quantidade) {
        Estoque estoque = new Estoque();
        estoque.setQuantidade(quantidade);
        return estoque;
    }

    @Test
    void deveCriarCarrinhoComPrecoUnitarioDoProduto() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto()));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque(10)));
        when(carrinhoRepository.save(any(Carrinho.class))).thenAnswer(invocation -> invocation.getArgument(0));

        carrinhoService.create(new CarrinhoRequest(usuarioId, List.of(new ItemCarrinhoRequest(produtoId, 3))));

        ArgumentCaptor<Carrinho> captor = ArgumentCaptor.forClass(Carrinho.class);
        verify(carrinhoRepository).save(captor.capture());
        Carrinho carrinho = captor.getValue();
        assertThat(carrinho.getItens()).hasSize(1);
        assertThat(carrinho.getItens().getFirst().getPrecoUnitario()).isEqualByComparingTo("10.00");
        assertThat(carrinho.getItens().getFirst().getCarrinho()).isSameAs(carrinho);
        assertThat(carrinho.getTotal()).isEqualByComparingTo("30.00");
    }

    @Test
    void deveFalharQuandoEstoqueInsuficiente() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto()));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque(2)));

        CarrinhoRequest request = new CarrinhoRequest(usuarioId, List.of(new ItemCarrinhoRequest(produtoId, 3)));
        assertThatThrownBy(() -> carrinhoService.create(request))
                .isInstanceOf(EstoqueInsuficienteException.class)
                .hasMessageContaining(produtoId.toString());
        verify(carrinhoRepository, never()).save(any(Carrinho.class));
    }

    @Test
    void deveFalharQuandoProdutoNaoPossuiEstoqueCadastrado() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto()));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());

        CarrinhoRequest request = new CarrinhoRequest(usuarioId, List.of(new ItemCarrinhoRequest(produtoId, 1)));
        assertThatThrownBy(() -> carrinhoService.create(request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveFalharQuandoUsuarioNaoExiste() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        CarrinhoRequest request = new CarrinhoRequest(usuarioId, List.of(new ItemCarrinhoRequest(produtoId, 1)));
        assertThatThrownBy(() -> carrinhoService.create(request))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveSubstituirOsItensAoAtualizarCarrinho() {
        UUID carrinhoId = UUID.randomUUID();
        Carrinho carrinho = new Carrinho();
        carrinho.setId(carrinhoId);
        when(carrinhoRepository.findById(carrinhoId)).thenReturn(Optional.of(carrinho));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario()));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto()));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(estoque(10)));
        when(carrinhoRepository.save(carrinho)).thenReturn(carrinho);

        carrinhoService.update(carrinhoId, new CarrinhoRequest(usuarioId, List.of(new ItemCarrinhoRequest(produtoId, 2))));

        assertThat(carrinho.getItens()).hasSize(1);
        assertThat(carrinho.getTotal()).isEqualByComparingTo("20.00");
    }

    @Test
    void deveFalharAoExcluirCarrinhoInexistente() {
        UUID carrinhoId = UUID.randomUUID();
        when(carrinhoRepository.findById(carrinhoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> carrinhoService.delete(carrinhoId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }
}
