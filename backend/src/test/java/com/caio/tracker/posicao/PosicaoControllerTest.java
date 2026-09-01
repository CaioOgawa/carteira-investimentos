package com.caio.tracker.posicao;

import com.caio.tracker.security.JwtService;
import com.caio.tracker.security.SecurityConfig;
import com.caio.tracker.security.UsuarioDetailsService;
import com.caio.tracker.security.UsuarioPrincipal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PosicaoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SecurityConfig.class)
class PosicaoControllerTest {

    private static final Long USUARIO_ID = 1L;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PosicaoService posicaoService;

    @MockitoBean
    private PosicaoImportService posicaoImportService;

    // Necessários apenas para satisfazer a construção do JwtAuthenticationFilter
    // (bean do tipo Filter, incluído no slice do @WebMvcTest mesmo com addFilters=false)
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UsuarioDetailsService usuarioDetailsService;

    @BeforeEach
    void autentica() {
        UsuarioPrincipal principal = new UsuarioPrincipal(USUARIO_ID, "caio@teste.com", "hash");
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
    }

    @AfterEach
    void limpaContexto() {
        SecurityContextHolder.clearContext();
    }

    private Posicao posicaoPetr4() {
        return new Posicao("PETR4", BigDecimal.TEN, new BigDecimal("32.50"), LocalDate.of(2026, 1, 15), null);
    }

    @Test
    void listarTodas_devolveJsonComAsPosicoesDoUsuarioAutenticado() throws Exception {
        when(posicaoService.listarTodas(USUARIO_ID)).thenReturn(List.of(posicaoPetr4()));

        mockMvc.perform(get("/posicoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].ativo").value("PETR4"));
    }

    @Test
    void buscarPorId_quandoNaoExiste_devolve404ComMensagem() throws Exception {
        when(posicaoService.buscarPorId(eq(99L), eq(USUARIO_ID))).thenThrow(new PosicaoNotFoundException(99L));

        mockMvc.perform(get("/posicoes/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Posição não encontrada: 99"));
    }

    @Test
    void criar_comDadosValidos_devolve201ComPosicaoCriada() throws Exception {
        when(posicaoService.criar(any(), eq(USUARIO_ID))).thenReturn(posicaoPetr4());

        String corpo = """
                {"ativo":"PETR4","quantidade":10,"precoCompra":32.50,"dataCompra":"2026-01-15"}""";

        mockMvc.perform(post("/posicoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ativo").value("PETR4"));
    }

    @Test
    void criar_comQuantidadeNegativa_devolve400ComErrosDeValidacao() throws Exception {
        String corpo = """
                {"ativo":"","quantidade":-1,"precoCompra":10,"dataCompra":"2026-01-01"}""";

        mockMvc.perform(post("/posicoes").contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.erros.ativo").exists())
                .andExpect(jsonPath("$.erros.quantidade").exists());
    }

    @Test
    void remover_devolve204SemConteudo() throws Exception {
        mockMvc.perform(delete("/posicoes/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void importar_comCsvValido_devolveImportadasEErros() throws Exception {
        var resposta = new ImportacaoResponse(
                List.of(PosicaoResponse.from(posicaoPetr4())),
                List.of(new ErroImportacao(3, "quantidade deve ser maior que zero"))
        );
        when(posicaoImportService.importar(any(), eq(USUARIO_ID))).thenReturn(resposta);

        var arquivo = new org.springframework.mock.web.MockMultipartFile(
                "arquivo", "carteira.csv", "text/csv",
                "ativo,quantidade,precoCompra,dataCompra\nPETR4,10,32.50,2026-01-15\n".getBytes());

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/posicoes/importar").file(arquivo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importadas[0].ativo").value("PETR4"))
                .andExpect(jsonPath("$.erros[0].linha").value(3));
    }
}
