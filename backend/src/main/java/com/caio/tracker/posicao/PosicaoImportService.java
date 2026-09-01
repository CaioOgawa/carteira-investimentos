package com.caio.tracker.posicao;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Importa posições em lote a partir de um CSV exportado (ou preparado) pelo usuário.
 * Formato esperado, com cabeçalho: ativo,quantidade,precoCompra,dataCompra
 * Exemplo:      PETR4,100,32.50,2026-01-15
 */
@Service
public class PosicaoImportService {

    private static final List<String> CABECALHO_ESPERADO = List.of("ativo", "quantidade", "precocompra", "datacompra");

    private final PosicaoService posicaoService;

    public PosicaoImportService(PosicaoService posicaoService) {
        this.posicaoService = posicaoService;
    }

    public ImportacaoResponse importar(MultipartFile arquivo, Long usuarioId) {
        if (arquivo.isEmpty()) {
            throw new ArquivoImportacaoInvalidoException("Arquivo vazio");
        }

        List<PosicaoResponse> importadas = new ArrayList<>();
        List<ErroImportacao> erros = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(arquivo.getInputStream(), StandardCharsets.UTF_8))) {
            validarCabecalho(reader.readLine());

            String linha;
            int numeroLinha = 1;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) {
                    continue;
                }
                try {
                    PosicaoRequest request = parsearLinha(linha);
                    Posicao posicao = posicaoService.criar(request, usuarioId);
                    importadas.add(PosicaoResponse.from(posicao));
                } catch (IllegalArgumentException e) {
                    erros.add(new ErroImportacao(numeroLinha, e.getMessage()));
                }
            }
        } catch (IOException e) {
            throw new ArquivoImportacaoInvalidoException("Não foi possível ler o arquivo: " + e.getMessage());
        }

        return new ImportacaoResponse(importadas, erros);
    }

    private void validarCabecalho(String cabecalho) {
        if (cabecalho == null) {
            throw new ArquivoImportacaoInvalidoException("Arquivo vazio");
        }

        List<String> colunas = Arrays.stream(cabecalho.split(","))
                .map(c -> c.trim().toLowerCase())
                .toList();

        if (!colunas.equals(CABECALHO_ESPERADO)) {
            throw new ArquivoImportacaoInvalidoException(
                    "Cabeçalho inválido. Esperado: ativo,quantidade,precoCompra,dataCompra");
        }
    }

    private PosicaoRequest parsearLinha(String linha) {
        String[] campos = linha.split(",", -1);
        if (campos.length != 4) {
            throw new IllegalArgumentException("linha deve ter 4 colunas (tem " + campos.length + ")");
        }

        String ativo = campos[0].trim();
        if (ativo.isBlank()) {
            throw new IllegalArgumentException("ativo não pode ser vazio");
        }

        BigDecimal quantidade = parsearDecimal(campos[1], "quantidade");
        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("quantidade deve ser maior que zero");
        }

        BigDecimal precoCompra = parsearDecimal(campos[2], "precoCompra");
        if (precoCompra.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("precoCompra não pode ser negativo");
        }

        LocalDate dataCompra;
        try {
            dataCompra = LocalDate.parse(campos[3].trim());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("dataCompra inválida, use AAAA-MM-DD: '" + campos[3].trim() + "'");
        }

        return new PosicaoRequest(ativo, quantidade, precoCompra, dataCompra);
    }

    private BigDecimal parsearDecimal(String valor, String nomeCampo) {
        try {
            return new BigDecimal(valor.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(nomeCampo + " inválido: '" + valor.trim() + "'");
        }
    }
}
