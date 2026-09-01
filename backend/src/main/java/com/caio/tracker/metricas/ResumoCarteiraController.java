package com.caio.tracker.metricas;

import com.caio.tracker.security.UsuarioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/carteira")
public class ResumoCarteiraController {

    private final ResumoCarteiraService resumoCarteiraService;

    public ResumoCarteiraController(ResumoCarteiraService resumoCarteiraService) {
        this.resumoCarteiraService = resumoCarteiraService;
    }

    @GetMapping("/resumo")
    public ResumoCarteiraResponse resumo(@AuthenticationPrincipal UsuarioPrincipal usuario) {
        return resumoCarteiraService.calcular(usuario.getUsuarioId());
    }
}
