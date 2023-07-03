package org.example;

import java.io.Serializable;
import java.util.*;

    class Resposta implements Serializable {
        float saldo;
        List<String> extrato = new ArrayList<String>();
        String nomeConta;
        Servico.tipoRetorno tipo;
    }


    class Pedido implements Serializable{
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
        CONSULTA,
        CRIACONTA;
    }


    public static enum tipoRetorno{
        TRANSFERENCIA_OKAY,
        TRANSFERENCIA_FALHA,
        DESTINO_INVALIDO,
        SEM_SALDO,
        LOGIN_OKAY,
        SENHA_FALHA,
        CONTA_FALHA,
        CRIACONTA_OKAY,
        CRIACONTA_FALSO,
        CONTA_INVALIDA,
        SALDO_FALHA,
        SALDO_OKAY,
        EXTRATO_FALHO,
        CONSULTA_OKAY,
        CONSULTA_FALHA,
        EXTRATO_OKAY;
    }

    Map <String,Conta> contas = new HashMap();
    Resposta resposta = new Resposta(); 

    Conta conta;
    //contador de eventos
    public Servico(){}

    //setar as repostas de erro e acerto
    public Resposta transferencia(float valor, String dest, String orig){

        Conta contaDest = contas.get(dest);
        Conta contaOrig = contas.get(orig);
        float aux = 0;

        if (contaDest.conta.equals(dest)){
            if((float)contaOrig.getSaldo()>=(float)valor){
                aux = contaDest.saldo;
                contaDest.saldo=aux+valor;
                aux = contaOrig.saldo;
                contaOrig.saldo=aux-valor;

                //criando extrato
                contaDest.extrato.add(orig + " R$" + valor);
                contaOrig.extrato.add(dest + " -R$" + valor);

                contas.replace(contaOrig.conta, contaOrig);
                contas.replace(contaDest.conta, contaDest);

                resposta.tipo = tipoRetorno.TRANSFERENCIA_OKAY;
                return resposta;

            }else{
                resposta.tipo = tipoRetorno.SEM_SALDO;
                return resposta;
            }

        }else{
            resposta.tipo = tipoRetorno.DESTINO_INVALIDO;
            return resposta;
        }
 
    }

    public Resposta saldo(String numConta){
        Conta conta = contas.get(numConta); // pega os dados da conta

        if(conta == null){
            resposta.tipo = tipoRetorno.SALDO_FALHA;
            return resposta;
        }else{
            resposta.saldo = conta.getSaldo();
            resposta.tipo = tipoRetorno.SALDO_OKAY;
            return resposta;
        }

    }

    public Resposta extrato(String numConta){
        Conta conta = contas.get(numConta); // pega os dados da conta

        if (conta == null){
            resposta.tipo = tipoRetorno.EXTRATO_FALHO;
            return resposta;
        }else{
            resposta.extrato = conta.getExtrato();//printa o extrato da conta
            resposta.tipo = tipoRetorno.EXTRATO_OKAY;
            return resposta;
        }
    }

    public Resposta criaConta(Conta conta){
        if (contas.containsValue(conta.conta) || contas.containsValue(conta.CPF)){
            resposta.tipo = tipoRetorno.CRIACONTA_FALSO;
            return resposta;
        } else {
            this.conta = conta;
            contas.put(conta.conta, conta);
            resposta.tipo = tipoRetorno.CRIACONTA_OKAY;
            return resposta;
        }
    }

    public Resposta autenticacao(Conta conta){
        String numeroConta = conta.getConta();
        int senha = conta.getSenha();


        if (contas.containsKey(numeroConta)) {
            Conta contaArmazenada = contas.get(numeroConta);
            if (contaArmazenada.getSenha() == senha) {
                resposta.tipo = tipoRetorno.LOGIN_OKAY;
                return resposta;
            }else{
                resposta.tipo = tipoRetorno.SENHA_FALHA;
                return resposta;
            }
        }else{
            resposta.tipo = tipoRetorno.CONTA_FALHA;
            return resposta;
        }
    }

    public Resposta consulta(String nome){
        if(contas.containsValue(nome)){
            for(Conta c : contas.values()){
                if(c.nome == conta.nome){
                    resposta.tipo = tipoRetorno.CONSULTA_OKAY;
                    resposta.nomeConta = c.conta;
                    return resposta;
                }
            }
        }
        resposta.tipo = tipoRetorno.CONSULTA_FALHA;
        return resposta;

    }

    public Map <String,Conta> tranferenciaEstado(Servico Estado){
        int estado1 = contas.hashCode();
        int estado2 = Estado.contas.hashCode();

        if(estado1 == estado2){

        }
        return contas;
    }

}
