package org.example;/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/**/
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import javax.swing.text.View;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;


public class Tbank extends ReceiverAdapter implements RequestHandler {

    JChannel canalDeComunicacao;
    MessageDispatcher  despachante;
    Servico estado;
    Servico serv = new Servico();
    Conta conta = new Conta();

    private static final Scanner scan = new Scanner(System.in);

    private void start() throws Exception {

        //Cria o canal de comunicação com uma configuração XML do JGroups
        canalDeComunicacao=new JChannel("cast.xml");


        despachante=new MessageDispatcher(canalDeComunicacao, null, null, this);

        canalDeComunicacao.setReceiver(this);	//quem irá lidar com as mensagens recebidas

        estado = new Servico();

        canalDeComunicacao.connect("Tenacious Bank");
            System.out.println("========= Bem Vindo ao Tenacious Bank ==========");
            System.out.println("1- Criar conta.");
            System.out.println("2- Login.");
            System.out.println("3- Sair.");
            System.out.print("Digite a sua escolha:");
            int menu = scan.nextInt();

            switch(menu){
                case 1:
                //-------------FAZER-----------
                //print perguntando a conta e a senha
                //chamar a funcao de criar conta
                //se conseguir criar entra em aplicao, se nao sair som mensagem de erro
                aplicacao();
                break;
                case 2:
                //-------------FAZER-----------
                //print perguntando a conta e a senha
                //chamar a funcao de autenticacao
                //se conseguir criar, se nao sair som mensagem de erro
                aplicacao();
                break;
            };

        canalDeComunicacao.close();

    }

    // extends ReceiverAdapter
    public void receive(Message msg) { //exibe mensagens recebidas
        System.out.println("" + msg.getSrc() + ": " + msg.getObject()); // DEBUG
    }

    // extends ReceiverAdapter
    public void viewAccepted(View new_view) { //exibe alterações na composição do cluster
        System.out.println("\t** nova View do cluster: " + new_view);   // DEBUG
    }


    // implements RequestHandler
    public Object handle(Message msg) throws Exception{ // responde requisições recebidas

        Servico serv = (Servico) msg.getObject();

        if(serv.tipo == estado.tipo.SALDO){

        }
        else if(serv.tipo == estado.tipo.EXTRATO){

        }
        else if(serv.tipo == estado.tipo.CRIACONTA) {

        }
        else if(serv.tipo == estado.tipo.TRANSFERENCIA) {
            return estado.transferencia(serv.valor,serv.contaDest,serv.contaOrig); // resposta padrão desse helloworld à requisição contida na mensagem
        }

        return null;
    }

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Código-fonte da minha aplicação de exemplo

    final int TAMANHO_MINIMO_CLUSTER = 5;

    private void aplicacao() throws Exception {

        RspList respostas;

        System.out.println("========= Bem Vindo ao Tenacious Bank ==========");
        System.out.println("1- Transferir dinheiro.");
        System.out.println("2- Conferir Saldo.");
        System.out.println("3- Conferir Extrato.");
        System.out.println("4- Sair.");
        System.out.print("Digite a sua escolha:");
        int menu = scan.nextInt();

        switch(menu){
            case 1:
                //-------------FAZER-----------
                //print pedindo a conta dest e o valor
                serv.contaDest = contadestdig;
                serv.contaOrig = contaorgdig;
                serv.valor = valordig;
                serv.tipo = estado.tipo.TRANSFERENCIA;
                respostas = enviaMulticast(serv);
                if(!respostas.containsValue(false)){
                    System.out.println("Tranfencia realizada com sucesso");
                }else{
                    System.out.println("ERRO: Tranfencia nao realizada");
                }
                break;
            case 2:
                serv.tipo = estado.tipo.SALDO;
                int num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                Address membro = (Address)canalDeComunicacao.getView().getMembers().get(num);
                respostas = enviaUnicast(membro, serv);
                break;
            case 3:
                serv.tipo = estado.tipo.EXTRATO;
                break;
            case 4:
                //-------------FAZER-----------
                //print dizendo Tchau
                break;

        }


    }//aplicacao

    private RspList enviaMulticast(Object conteudo) throws Exception{

        Address cluster = null; //OBS.: não definir um destinatário significa enviar a TODOS os membros do cluster
        Message mensagem=new Message(cluster, conteudo);

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta da MAIORIA dos membros (MAJORITY) // Outras opções: ALL, FIRST, NONE
        opcoes.setAnycasting(false);


        RspList respList = despachante.castMessage(null, mensagem, opcoes); //envia o MULTICAST

        return respList;
    }


    private RspList enviaAnycast(Collection<Address> subgrupo, String conteudo) throws Exception{

        Message mensagem=new Message(null, conteudo); //apesar do endereço ser null, se as opcoes contiverem anycasting==true enviará somente aos destinos listados

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_FIRST); // só ESPERA receber a primeira resposta do subgrupo (FIRST) // Outras opções: ALL, MAJORITY, NONE
        opcoes.setAnycasting(true);

        RspList respList = despachante.castMessage(subgrupo, mensagem, opcoes); //envia o ANYCAST

        return respList;
    }


    private String enviaUnicast(Address destino, Object conteudo) throws Exception{

        Message mensagem=new Message(destino, conteudo);

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta do destino // Outras opções: ALL, MAJORITY, FIRST, NONE
        // opcoes.setMode(ResponseMode.GET_NONE); // não ESPERA receber a resposta do destino // Outras opções: ALL, MAJORITY, FIRST

        String resp = despachante.sendMessage(mensagem, opcoes); //envia o UNICAST

        return resp;
    }


    public static void main(String[] args) throws Exception {
        new Tbank().start();
    }

}//class
