package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.EstoqueRequest;
import br.com.fiap.api_rest.dto.EstoqueResponse;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.exception.RegraDeNegocioException;
import br.com.fiap.api_rest.mapper.EstoqueMapper;
import br.com.fiap.api_rest.model.Estoque;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.EstoqueRepository;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstoqueServiceTest {

    @Mock
    private EstoqueRepository estoqueRepository;
    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private EstoqueMapper estoqueMapper;
    @InjectMocks
    private EstoqueService estoqueService;

    private Produto produto(UUID id) {
        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome("Cha verde");
        return produto;
    }

    @Test
    void deveCriarEstoqueParaProdutoSemEstoque() {
        UUID produtoId = UUID.randomUUID();
        Produto produto = produto(produtoId);
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(invocation -> invocation.getArgument(0));
        EstoqueResponse esperado = new EstoqueResponse(UUID.randomUUID(), produtoId, "Cha verde", 10, 2, null);
        when(estoqueMapper.estoqueToResponse(any(Estoque.class))).thenReturn(esperado);

        EstoqueResponse response = estoqueService.create(new EstoqueRequest(produtoId, 10, 2));

        ArgumentCaptor<Estoque> captor = ArgumentCaptor.forClass(Estoque.class);
        verify(estoqueRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantidade()).isEqualTo(10);
        assertThat(captor.getValue().getQuantidadeMinima()).isEqualTo(2);
        assertThat(captor.getValue().getProduto()).isEqualTo(produto);
        assertThat(response).isEqualTo(esperado);
    }

    @Test
    void deveUsarZeroQuandoQuantidadeMinimaNaoInformada() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto(produtoId)));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());
        when(estoqueRepository.save(any(Estoque.class))).thenAnswer(invocation -> invocation.getArgument(0));

        estoqueService.create(new EstoqueRequest(produtoId, 5, null));

        ArgumentCaptor<Estoque> captor = ArgumentCaptor.forClass(Estoque.class);
        verify(estoqueRepository).save(captor.capture());
        assertThat(captor.getValue().getQuantidadeMinima()).isZero();
    }

    @Test
    void deveFalharAoCriarEstoqueParaProdutoInexistente() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.create(new EstoqueRequest(produtoId, 1, 0)))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(estoqueRepository, never()).save(any(Estoque.class));
    }

    @Test
    void deveFalharAoCriarEstoqueDuplicado() {
        UUID produtoId = UUID.randomUUID();
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto(produtoId)));
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.of(new Estoque()));

        assertThatThrownBy(() -> estoqueService.create(new EstoqueRequest(produtoId, 1, 0)))
                .isInstanceOf(RegraDeNegocioException.class);
    }

    @Test
    void deveAtualizarQuantidadeDoEstoque() {
        UUID estoqueId = UUID.randomUUID();
        UUID produtoId = UUID.randomUUID();
        Estoque estoque = new Estoque();
        estoque.setId(estoqueId);
        estoque.setQuantidade(1);
        when(estoqueRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));
        when(produtoRepository.findById(produtoId)).thenReturn(Optional.of(produto(produtoId)));
        when(estoqueRepository.save(estoque)).thenReturn(estoque);

        estoqueService.update(estoqueId, new EstoqueRequest(produtoId, 42, 7));

        assertThat(estoque.getQuantidade()).isEqualTo(42);
        assertThat(estoque.getQuantidadeMinima()).isEqualTo(7);
    }

    @Test
    void deveFalharAoBuscarEstoquePorProdutoSemEstoque() {
        UUID produtoId = UUID.randomUUID();
        when(estoqueRepository.findByProdutoId(produtoId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> estoqueService.readByProduto(produtoId))
                .isInstanceOf(RecursoNaoEncontradoException.class);
    }

    @Test
    void deveExcluirEstoqueExistente() {
        UUID estoqueId = UUID.randomUUID();
        Estoque estoque = new Estoque();
        estoque.setId(estoqueId);
        when(estoqueRepository.findById(estoqueId)).thenReturn(Optional.of(estoque));

        estoqueService.delete(estoqueId);

        verify(estoqueRepository).delete(estoque);
    }
}
