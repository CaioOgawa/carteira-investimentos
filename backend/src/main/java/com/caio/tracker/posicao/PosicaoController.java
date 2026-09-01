package com.caio.tracker.posicao;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/posicoes")
public class PosicaoController {

    private final PosicaoService posicaoService;

    public PosicaoController(PosicaoService posicaoService) {
        this.posicaoService = posicaoService;
    }

    @GetMapping
    public List<PosicaoResponse> listarTodas() {
        return posicaoService.listarTodas().stream()
                .map(PosicaoResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public PosicaoResponse buscarPorId(@PathVariable Long id) {
        return PosicaoResponse.from(posicaoService.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PosicaoResponse criar(@Valid @RequestBody PosicaoRequest request) {
        return PosicaoResponse.from(posicaoService.criar(request));
    }

    @PutMapping("/{id}")
    public PosicaoResponse atualizar(@PathVariable Long id, @Valid @RequestBody PosicaoRequest request) {
        return PosicaoResponse.from(posicaoService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        posicaoService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
