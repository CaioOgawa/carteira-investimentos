package com.caio.tracker.posicao;

import com.caio.tracker.security.UsuarioPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/posicoes")
public class PosicaoController {

    private final PosicaoService posicaoService;
    private final PosicaoImportService posicaoImportService;

    public PosicaoController(PosicaoService posicaoService, PosicaoImportService posicaoImportService) {
        this.posicaoService = posicaoService;
        this.posicaoImportService = posicaoImportService;
    }

    @GetMapping
    public List<PosicaoResponse> listarTodas(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return posicaoService.listarTodas(usuario.getUsuarioId()).stream()
                .map(PosicaoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PosicaoResponse buscarPorId(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return PosicaoResponse.from(posicaoService.buscarPorId(id, usuario.getUsuarioId()));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PosicaoResponse criar(@Valid @RequestBody PosicaoRequest request, @AuthenticationPrincipal UsuarioPrincipal usuario) {
        return PosicaoResponse.from(posicaoService.criar(request, usuario.getUsuarioId()));
    }

    @PutMapping("/{id}")
    public PosicaoResponse atualizar(
            @PathVariable Long id,
            @Valid @RequestBody PosicaoRequest request,
            @AuthenticationPrincipal UsuarioPrincipal usuario
    ) {
        return PosicaoResponse.from(posicaoService.atualizar(id, request, usuario.getUsuarioId()));
    }

    @PostMapping(value = "/importar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ImportacaoResponse importar(
            @RequestParam("arquivo") MultipartFile arquivo,
            @AuthenticationPrincipal UsuarioPrincipal usuario
    ) {
        return posicaoImportService.importar(arquivo, usuario.getUsuarioId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal usuario) {
        posicaoService.remover(id, usuario.getUsuarioId());
        return ResponseEntity.noContent().build();
    }
}
