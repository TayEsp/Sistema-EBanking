package org.example;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Conta implements Serializable{
    String conta;
    String nome;
    String CPF;
    int senha;
    float saldo = 1000;
    List<String> extrato = new ArrayList<String>();

    public Conta(String conta,int senha){
        this.conta = conta;
        this.senha = senha;
    }

    public Conta() {

    }

    public boolean equals(Object outra){
        if(outra instanceof Conta){
            if(((Conta)outra).conta.equals(this.conta) && ((Conta)outra).senha == (this.senha) && ((Conta)outra).extrato.equals(this.extrato)){
                return true;
            }
        }
        return false;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }
    public void setSenha(int senha) {
        this.senha = senha;
    }
    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }
    public void setExtrato(List<String> extrato) {
        this.extrato = extrato;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public void setCPF(String CPF) {
        this.CPF = CPF;
    }

    public String getConta() {
        return conta;
    }
    public int getSenha() {
        return senha;
    }
    public float getSaldo() {
        return saldo;
    }
    public List<String> getExtrato() {
        return extrato;
    }
    public String getNome() {
        return nome;
    }
    public String getCPF() {
        return CPF;
    }

}
