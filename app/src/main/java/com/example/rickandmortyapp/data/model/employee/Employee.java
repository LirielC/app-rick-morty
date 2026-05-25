package com.example.rickandmortyapp.data.model.employee;

import java.io.Serializable;

public class Employee implements Serializable {
    private Long id;
    private String nome;
    private String email;
    private String cargo;
    private double salario;
    private boolean ativo;
    private String dataCriacao;

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getCargo() {
        return cargo;
    }

    public double getSalario() {
        return salario;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }
}
