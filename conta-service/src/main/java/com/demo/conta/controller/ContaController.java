package com.demo.conta.controller;

import com.demo.conta.dto.ContaResumo;
import com.demo.conta.mapper.ContaMapper;
import com.demo.conta.service.ContaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/contas")
public class ContaController {

    private final ContaService contaService;
    private final ContaMapper contaMapper;

    public ContaController(ContaService contaService, ContaMapper contaMapper) {
        this.contaService = contaService;
        this.contaMapper = contaMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContaResumo> buscar(@PathVariable Long id) {
        return contaService.buscarPorId(id)
                .map(contaMapper::toResumo)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
