package org.example;/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/**/
import java.util.*;

import javax.swing.text.View;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;

public class Tbank extends ReceiverAdapter implements RequestHandler {

    JChannel canalDeComunicacao;
    MessageDispatcher despachante;
    Servico estado;
    Servico serv = new Servico();
    Conta conta = new Conta();
    RspList respostas;
    String resposta;
    Address membro;

    private static final Scanner scan = new Scanner(System.in);

    private void start() throws Exception {

        //Cria o canal de comunicação com uma configuração XML do JGroups
        canalDeComunicacao=new JChannel("C:\\Users\\Tayna\\Desktop\\facul\\Semestre_7\\Sistemas_Distribuidos\\Sistema-EBanking\\src\\main\\java\\org\\example\\cast.xml");


        despachante=new MessageDispatcher(canalDeComunicacao, null, null, this);

        canalDeComunicacao.setReceiver(this);	//quem irá lidar com as mensagens recebidas

        estado = new Servico();
        int menu = 0;

        canalDeComunicacao.connect("Tenacious Bank");
            while(menu!=3){
                System.out.println("========= Bem Vindo ao Tenacious Bank ==========");
                System.out.println("1- Criar conta.");
                System.out.println("2- Login.");
                System.out.println("3- Sair.");
                System.out.print("Digite a sua escolha:");
                menu = scan.nextInt();
                scan.nextLine(); 

                int senha;
                String contaDig;

                switch(menu){   
                    case 1:
                        System.out.print("Digite o número da sua conta:");
                        contaDig = scan.nextLine();
                        System.out.print("Digite a sua senha:");
                        senha = scan.nextInt();
                        serv.conta.senha = senha;
                        serv.conta.conta = contaDig;
                        serv.tipo = estado.tipo.CRIACONTA;
                        respostas = enviaMulticast(serv);
                        if(!respostas.containsValue(false)){
                            System.out.println("Conta criada com sucesso!");
                            aplicacao();
                        }else{
                            System.out.println("ERRO: Não foi possivel criar conta!");

                        }
                    break;
                    case 2:
                        System.out.println("Digite o número da sua conta:");
                        conta.conta = scan.nextLine();
                        System.out.println("Digite a sua senha:");
                        conta.senha = scan.nextInt();

                        serv.conta = conta;
                        serv.tipo = estado.tipo.LOGIN;
                        int num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                        membro = (Address)canalDeComunicacao.getView().getMembers().get(num);
                        resposta = enviaUnicast(membro, serv);
                        if(resposta==null){
                            aplicacao();
                        }else{
                            System.out.println("ERRO: Não foi possivel logar!");

                        }
                    break;

                    case 3:
                        System.out.print("Obrigada por usar Tenacious Bank!");
                    break;
                }
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
           estado.saldo(serv.conta.conta);
        }
        else if(serv.tipo == estado.tipo.EXTRATO){
            estado.extrato(serv.conta.conta);
        }
        else if(serv.tipo == estado.tipo.CRIACONTA) {
            estado.criaConta(serv.conta);
        }
        else if(serv.tipo == estado.tipo.TRANSFERENCIA) {
            estado.transferencia(serv.valor,serv.contaDest,serv.contaOrig); 
        }
        else if(serv.tipo == estado.tipo.LOGIN) {
            estado.autenticacao(serv.conta); 
        }

        return null;
    }

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Código-fonte da minha aplicação de exemplo

    final int TAMANHO_MINIMO_CLUSTER = 3;

    private void aplicacao() throws Exception {
        int menu = 0;
        int num;

        while(menu!=4){
            System.out.println("\n========= Bem Vindo ao Tenacious Bank ==========");
            System.out.println("1- Transferir dinheiro.");
            System.out.println("2- Conferir Saldo.");
            System.out.println("3- Conferir Extrato.");
            System.out.println("4- Sair.");
            System.out.print("Digite a sua escolha:");
            menu = scan.nextInt();

            switch(menu){
                case 1:
                    scan.nextLine(); 
                    System.out.print("Digite a conta que você deseja transferir:");
                    serv.contaDest = scan.nextLine();
                    System.out.println("Digite o valor que deseja transferir:");
                    serv.valor = scan.nextInt();
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
                    serv.contaOrig = conta.conta;
                    num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                    membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                    resposta = enviaUnicast(membro, serv);
                    if(resposta!=null){
                        System.out.println("ERRO: falha ao ver o Saldo!");

                    }
                    break;
                case 3:
                    serv.tipo = estado.tipo.EXTRATO;
                    serv.contaOrig = conta.conta;
                    num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                    membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                    resposta = enviaUnicast(membro, serv);
                    if(resposta!=null){
                        System.out.println("ERRO: falha ao ver o Extrato!");
                    }
                    break;
                case 4:
                    System.out.println("Você saiu da sua conta.");
                    break;
            }

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