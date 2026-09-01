package com.caio.tracker.web;

import com.caio.tracker.posicao.ArquivoImportacaoInvalidoException;
import com.caio.tracker.posicao.PosicaoNotFoundException;
import com.caio.tracker.usuario.EmailJaCadastradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(PosicaoNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(PosicaoNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(corpoErro(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(EmailJaCadastradoException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicado(EmailJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(corpoErro(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Map<String, Object>> handleCredenciaisInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(corpoErro(HttpStatus.UNAUTHORIZED, "Email ou senha inválidos"));
    }

    @ExceptionHandler(ArquivoImportacaoInvalidoException.class)
    public ResponseEntity<Map<String, Object>> handleArquivoInvalido(ArquivoImportacaoInvalidoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErro(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(erro ->
                erros.put(erro.getField(), erro.getDefaultMessage()));

        Map<String, Object> corpo = corpoErro(HttpStatus.BAD_REQUEST, "Erro de validação");
        corpo.put("erros", erros);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpo);
    }

    private Map<String, Object> corpoErro(HttpStatus status, String mensagem) {
        Map<String, Object> corpo = new LinkedHashMap<>();
        corpo.put("timestamp", Instant.now().toString());
        corpo.put("status", status.value());
        corpo.put("mensagem", mensagem);
        return corpo;
    }
}
