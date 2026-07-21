package com.demo.conta.service;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/** Base genérica com as operações de CRUD comuns a todas as entidades. */
public abstract class CrudService<T, ID> {

    protected abstract JpaRepository<T, ID> getRepository();

    public T salvar(T entidade) {
        return getRepository().save(entidade);
    }

    public Optional<T> buscarPorId(ID id) {
        return getRepository().findById(id);
    }

    public List<T> listarTodos() {
        return getRepository().findAll();
    }

    public boolean existePorId(ID id) {
        return getRepository().existsById(id);
    }

    public void deletar(ID id) {
        getRepository().deleteById(id);
    }
}
