package org.example;

import org.example.Conta;
import java.util.*;

public class Servico {

    public static enum tipoServico {
        TRANSFERENCIA,
        SALDO,
        EXTRATO,
        CRIACONTA;

    }

    Map <String,Conta> contas = new HashMap();
    String contaDest;
    String contaOrig;
    float valor;
    tipoServico tipo;

    Conta conta = new Conta();

    public Servico(){}

    public boolean transferencia(float valor, String dest, String orig){
        Conta contaDest = contas.get(dest);
        Conta contaOrig = contas.get(orig);

        //verificacao
        return true;
        //debita das duas contas
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
            System.out.println("Conta criada com sucesso! Identificador: conta: " + conta.getConta());
        }

    }


}
