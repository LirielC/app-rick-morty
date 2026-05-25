package com.example.rickandmortyapp.data.model.employee;

public class EmployeeRequest {
    private final String nome;
    private final String email;
    private final String cargo;
    private final double salario;
    private final boolean ativo;

    public EmployeeRequest(String nome, String email, String cargo, double salario, boolean ativo) {
        this.nome = nome;
        this.email = email;
        this.cargo = cargo;
        this.salario = salario;
        this.ativo = ativo;
    }
}
