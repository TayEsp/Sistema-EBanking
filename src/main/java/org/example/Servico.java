package org.example;

import org.example.Conta;
import org.example.Servico.tipoRetorno;

import java.io.Serializable;
import java.util.*;

    class Resposta {
        float saldo;
        String extrato;
        Servico.tipoRetorno tipo;
    }


    class Pedido {
        Conta conta;
        String contaDest;
        String contaOrig;
        float valor;
        Servico.tipoServico tipo;

    }

public class Servico implements Serializable{

    public static enum tipoServico{
        TRANSFERENCIA,
        SALDO,
        EXTRATO,
        LOGIN,
        CRIACONTA;

    }


    public static enum tipoRetorno{
        TRANSFERENCIA_OKAY,
        TRANSFERENCIA_FALHA,
        LOGIN_OKAY,
        LOGIN_FALSO,
        CRIACONTA_OKAY,
        CRIACONTA_FALSO;
    }

    Map <String,Conta> contas = new HashMap();

    Conta conta;
    //contador de eventos
    public Servico(){}

    //setar as repostas de erro e acerto
    public boolean transferencia(float valor, String dest, String orig){

        Conta contaDest = contas.get(dest);
        Conta contaOrig = contas.get(orig);
        float aux = 0;

        if (contaDest.conta.equals(dest)){
            if((float)contaOrig.getSaldo()>=(float)valor){
                aux = contaDest.saldo;
                contaDest.saldo=aux+valor;
                aux = contaOrig.saldo;
                contaOrig.saldo=aux-valor;
                contas.replace(contaOrig.conta, contaOrig);
                contas.replace(contaDest.conta, contaDest);
            }else{
                System.out.print("conta sem saldo");
            }

        }else{
            System.out.print("destino invalido");
        }
        
        return true;
    }

    public void saldo(String numConta){
        Conta conta = contas.get(numConta); // pega os dados da conta

        System.out.println("Saldo: R$" + conta.getSaldo());//printa o valor
    }

    public void extrato(String numConta){
        Conta conta = contas.get(numConta); // pega os dados da conta

        System.out.println(conta.getExtrato());//printa o extrato da conta
    }

    public void criaConta(Conta conta){
        if (contas.containsValue(conta.conta)){
            System.out.println("ERRO! Numero de conta já cadastrado");
            return;
        } else {
            this.conta = conta;
            contas.put(conta.conta, conta);
        }
    }

    public boolean autenticacao(Conta conta){
        String numeroConta = conta.getConta();
        int senha = conta.getSenha();

        if (contas.containsKey(numeroConta)) {
            Conta contaArmazenada = contas.get(numeroConta);
            if (contaArmazenada.getSenha() == senha) {
                return true;
            }else{
                System.out.println("ERRO: Senha inválida");
                return false;
            }
        }else{
            System.out.println("ERRO: Número da conta inválido");
            return false;
        }
    }

    //-------FAZER------
    //funcao cria extrato


}
