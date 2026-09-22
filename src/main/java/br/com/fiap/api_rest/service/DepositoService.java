package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.dto.DepositoRequest;
import br.com.fiap.api_rest.model.Deposito;
import br.com.fiap.api_rest.repository.DepositoRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepositoService {
    private final DepositoRepository depositoRepository;

    @Autowired
    public DepositoService(DepositoRepository depositoRepository) {
        this.depositoRepository = depositoRepository;
    }

    public Deposito create(DepositoRequest depositoRequest) {
        Deposito deposito = new Deposito();
        BeanUtils.copyProperties(depositoRequest, deposito);
        return depositoRepository.save(deposito);
    }

    public List<Deposito> readAll() {
        return depositoRepository.findAll();
    }
}
