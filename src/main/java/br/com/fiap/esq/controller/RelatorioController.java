package br.com.fiap.esq.controller;

import br.com.fiap.esq.dto.ResumoRelatorio;
import br.com.fiap.esq.service.RelatorioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(RelatorioService service) {
        this.service = service;
    }

    //200 OK, 400 (sem idUsuario), 404 Not Found (usuário) ou 422 (mês inválido)
    @GetMapping("/resumo")
    public ResumoRelatorio resumo(@RequestParam int idUsuario,
                                  @RequestParam(required = false) Integer mes,
                                  @RequestParam(required = false) Integer ano) {
        return service.resumo(idUsuario, mes, ano);
    }
}
