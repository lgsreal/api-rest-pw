package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.ProdutoRequest;
import br.com.fiap.api_rest.dto.ProdutoResponse;
import br.com.fiap.api_rest.exception.RecursoNaoEncontradoException;
import br.com.fiap.api_rest.mapper.ProdutoMapper;
import br.com.fiap.api_rest.model.Produto;
import br.com.fiap.api_rest.repository.ProdutoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;
    @Mock
    private ProdutoMapper produtoMapper;
    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveCriarProdutoComOsDadosDoRequest() {
        ProdutoRequest request = new ProdutoRequest("Cha verde", new BigDecimal("9.90"), LocalDate.of(2030, 1, 1));
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produto salvo = produtoService.create(request);

        ArgumentCaptor<Produto> captor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoRepository).save(captor.capture());
        assertThat(captor.getValue().getNome()).isEqualTo("Cha verde");
        assertThat(salvo.getPreco()).isEqualByComparingTo("9.90");
        assertThat(salvo.getExpiracao()).isEqualTo(LocalDate.of(2030, 1, 1));
    }

    @Test
    void deveRetornarProdutoPorId() {
        UUID id = UUID.randomUUID();
        Produto produto = new Produto();
        produto.setId(id);
        ProdutoResponse response = new ProdutoResponse(id, "Cha verde", BigDecimal.ONE, null);
        when(produtoRepository.findById(id)).thenReturn(Optional.of(produto));
        when(produtoMapper.produtoToResponse(produto)).thenReturn(response);

        assertThat(produtoService.read(id)).isEqualTo(response);
    }

    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {
        UUID id = UUID.randomUUID();
        when(produtoRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> produtoService.read(id))
                .isInstanceOf(RecursoNaoEncontradoException.class)
                .hasMessageContaining(id.toString());
    }

    @Test
    void deveLancarExcecaoAoAtualizarProdutoInexistente() {
        Produto produto = new Produto();
        produto.setId(UUID.randomUUID());
        when(produtoRepository.existsById(produto.getId())).thenReturn(false);

        assertThatThrownBy(() -> produtoService.update(produto))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    void deveLancarExcecaoAoExcluirProdutoInexistente() {
        UUID id = UUID.randomUUID();
        when(produtoRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> produtoService.delete(id))
                .isInstanceOf(RecursoNaoEncontradoException.class);
        verify(produtoRepository, never()).deleteById(id);
    }
}
