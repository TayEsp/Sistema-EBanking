package org.example;/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/**/
import java.util.*;
import java.io.*;

import javax.swing.text.View;
import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;

//Object0utputStream
//ObjectInputStream

public class Tbank extends ReceiverAdapter implements RequestHandler {

    JChannel canalDeComunicacao;
    MessageDispatcher despachante;
    Servico estado;
    Pedido serv = new Pedido();
    Conta conta;
    RspList <Resposta> respostas;
    Resposta resposta;
    ObjectOutputStream fileOut;
    ObjectInputStream fileOpen;
    Address membro;

    private static final Scanner scan = new Scanner(System.in);

    private void start() throws Exception {

        //Cria o canal de comunicação com uma configuração XML do JGroups
        canalDeComunicacao=new JChannel("cast.xml");


        despachante=new MessageDispatcher(canalDeComunicacao, null, null, this);

        canalDeComunicacao.setReceiver(this);	//quem irá lidar com as mensagens recebidas

        estado = new Servico();
        try{
            fileOpen = new ObjectInputStream(new FileInputStream("StateBank.dat"));
            estado = (Servico)fileOpen.readObject();
            fileOpen.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        int menu = 0;

        canalDeComunicacao.connect("Tenacious Bank");

        if (!canalDeComunicacao.getView().getMembers().get(0).equals(canalDeComunicacao.getAddress())) {
            Address coordinator = canalDeComunicacao.getView().getMembers().get(0); // Obtém o endereço do coordenador
            fileOut = new ObjectOutputStream(new FileOutputStream("C:\\Users\\tayna\\Desktop\\facul\\Semestre_7\\Sistema_Distribuidos\\Sistema-EBanking-lusca1\\src\\main\\java\\org\\example\\StateBank.dat"));
            // Chama a função getStateFromApplication() para receber o estado do coordenador
            getStateFromApplication(coordinator, fileOut, true);
            fileOut.close();
        }

            while(menu!=3){
                conta = new Conta();

                System.out.println("\n========= Bem Vindo ao Tenacious Bank ==========");
                System.out.println("1- Criar conta.");
                System.out.println("2- Login.");
                System.out.println("3- Sair.");
                System.out.print("Digite a sua escolha:");
                menu = scan.nextInt();
                scan.nextLine(); 

                switch(menu){   
                    case 1:
                        System.out.print("Digite o seu nome:");
                        conta.nome = scan.nextLine();
                        System.out.print("Digite o seu CPF:");
                        conta.CPF = scan.nextLine();
                        System.out.print("Digite o número da sua conta:");
                        conta.conta = scan.nextLine();
                        System.out.print("Digite a sua senha:");
                        conta.senha = scan.nextInt();
                        serv.conta = conta;
                        serv.tipo = Servico.tipoServico.CRIACONTA;

                        respostas = enviaMulticast(serv);

                        boolean flag=true;
                        for(Rsp<Resposta> r : respostas.values()){
                            if(r.getValue().tipo == Servico.tipoRetorno.CRIACONTA_FALSO){
                                    System.out.println("ERRO: Não foi possivel criar a conta");
                                    flag = false;
                                    break;
                            }else if(r.getValue().tipo==Servico.tipoRetorno.CONTA_INVALIDA){
                                    System.out.println("ERRO! Numero de conta já cadastrado");
                                    flag = false;
                                    break;
                            }
                        }
                        if(flag){
                            System.out.println("Conta criada com sucesso");
                            salvar();
                            aplicacao();
                        }
                    break;
                    case 2:
                        System.out.print("Digite o número da sua conta:");
                        conta.conta = scan.nextLine();
                        System.out.print("Digite a sua senha:");
                        conta.senha = scan.nextInt();
                        serv.conta = conta;
                        serv.tipo = Servico.tipoServico.LOGIN;

                        int num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                        membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                        resposta = enviaUnicast(membro, serv);

                        if(resposta.tipo==Servico.tipoRetorno.LOGIN_OKAY){
                            System.out.println("Login bem-sucedido!");
                            aplicacao();
                        }else if(resposta.tipo==Servico.tipoRetorno.SENHA_FALHA){
                            System.out.println("ERRO: Senha inválida");
                        }else if(resposta.tipo==Servico.tipoRetorno.CONTA_FALHA){
                            System.out.println("ERRO: Número da conta inválido");
                        }else{
                            System.out.println("ERRO: Não foi possivel logar!");
                        }
                    break;

                    case 3:
                        System.out.println("Obrigada por usar Tenacious Bank!");
                    break;

                    default:
                        System.out.println("Opcao Invalida!");
                    break;
                }
            };

        canalDeComunicacao.close();

    }

    public void getStateFromApplication(Address requester, java.io.OutputStream out, boolean use_separate_thread){
            try {
                // Exemplo: Obtenha o estado atual do aplicativo
                Object state = estado;
                // Serializa o estado e envia para o solicitante
                Util.objectToStream(state, new DataOutputStream(out));
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    public void salvar(){
        try{
            fileOut = new ObjectOutputStream(new FileOutputStream("StateBank.dat"));
            fileOut.writeObject(estado);
            fileOut.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            }
    }

    // extends ReceiverAdapter
    public void receive(Message msg) { //exibe mensagens recebidas

    }

    // extends ReceiverAdapter
    public void viewAccepted(View new_view) { //exibe alterações na composição do cluster

    }

    // implements RequestHandler
    public Resposta handle(Message msg) throws Exception{ // responde requisições recebidas
        //acertas as respostas
        Pedido serv = (Pedido) msg.getObject();

        if(serv.tipo == Servico.tipoServico.SALDO){
            return estado.saldo(serv.conta.conta);
        }
        else if(serv.tipo == Servico.tipoServico.EXTRATO){
            return estado.extrato(serv.conta.conta);
        }
        else if(serv.tipo == Servico.tipoServico.CRIACONTA) {
            return estado.criaConta(serv.conta);
        }
        else if(serv.tipo == Servico.tipoServico.TRANSFERENCIA) {
            return estado.transferencia(serv.valor,serv.contaDest,serv.contaOrig); 
        }
        else if(serv.tipo == Servico.tipoServico.LOGIN) {
            return estado.autenticacao(serv.conta); 
        }
        else if(serv.tipo == Servico.tipoServico.CONSULTA) {
            return estado.consulta(serv.conta.nome); 
        }else{
            System.out.println("Opcao Invalida");
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
            System.out.println("\n========= Bem Vindo, "+conta.conta+" ==========");
            System.out.println("1- Transferir dinheiro.");
            System.out.println("2- Conferir Saldo.");
            System.out.println("3- Conferir Extrato.");
            System.out.println("4- Conferir numero da conta.");
            System.out.println("5- Sair.");
            System.out.print("Digite a sua escolha:");
            menu = scan.nextInt();

            switch(menu){
                case 1:
                    scan.nextLine(); 
                    serv.contaOrig = conta.conta;
                    System.out.print("Digite a conta que você deseja transferir:");
                    serv.contaDest = scan.nextLine();
                    System.out.print("Digite o valor que deseja transferir:");
                    serv.valor = scan.nextInt();
                    serv.tipo = Servico.tipoServico.TRANSFERENCIA;

                    respostas = enviaMulticast(serv);

                    boolean flag=true;
                    for(Rsp<Resposta> r : respostas.values()){
                        if(r.getValue().tipo == Servico.tipoRetorno.TRANSFERENCIA_FALHA){
                            System.out.println("ERRO: Tranfencia nao realizada");
                            flag = false;
                            break;
                        }else if(r.getValue().tipo == Servico.tipoRetorno.DESTINO_INVALIDO){
                            System.out.print("ERRO: Destino invalido");
                            flag = false;
                            break;
                        }else if(r.getValue().tipo == Servico.tipoRetorno.SEM_SALDO){
                            System.out.print("ERRO: Conta sem saldo");
                            flag = false;
                            break;
                        }
                    }
                    if(flag){
                        salvar();
                        System.out.println("Tranfencia realizada com sucesso");
                        }
                break;

                case 2:
                    serv.tipo = Servico.tipoServico.SALDO;
                    serv.contaOrig = conta.conta;

                    num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                    membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                    resposta = enviaUnicast(membro, serv);

                    if(resposta.tipo==Servico.tipoRetorno.SALDO_FALHA){
                        System.out.println("ERRO: falha ao ver o Saldo!");

                    }else if(resposta.tipo==Servico.tipoRetorno.SALDO_OKAY){
                        System.out.println("Saldo: R$" + resposta.saldo);
                    }else{
                        System.out.println("ERRO: falha ao ver o Saldo!");
                    }
                break;

                case 3:
                    serv.tipo = Servico.tipoServico.EXTRATO;
                    serv.contaOrig = conta.conta;
                    
                    num = (int)Math.random() * canalDeComunicacao.getView().getMembers().size();
                    membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                    resposta = enviaUnicast(membro, serv);

                    if(resposta.tipo==Servico.tipoRetorno.EXTRATO_FALHO){
                        System.out.println("ERRO: falha ao ver o Saldo!");
                    }else if(resposta.tipo==Servico.tipoRetorno.EXTRATO_OKAY){
                        System.out.println("Extrato:");
                        for(String r : resposta.extrato){
                            System.out.println(r);
                        }

                    }else{
                        System.out.println("ERRO: falha ao ver o Saldo!");
                    }
                break;

                case 4:
                    System.out.println("Você saiu da sua conta.");
                break;

                case 5:
                    scan.nextLine(); 
                    System.out.print("Digite o nome que deseja procurar:");
                    serv.contaOrig = scan.nextLine();

                    num = (int)Math.random() * (canalDeComunicacao.getView().getMembers().size()-1);
                    membro = (Address)canalDeComunicacao.getView().getMembers().get(num);

                    resposta = enviaUnicast(membro, serv.contaOrig);

                    if(resposta.tipo==Servico.tipoRetorno.CONSULTA_FALHA){
                        System.out.println("ERRO: Este nome nao esta cadastrado!");

                    }else if(resposta.tipo==Servico.tipoRetorno.CONSULTA_OKAY){
                        System.out.println("Conta de "+ serv.contaOrig + ": " + resposta.nomeConta);
                    }
                break;

                default:
                    System.out.println("Opcao Invalida!");
                break;
            }

        }


    }//aplicacao

    private RspList <Resposta> enviaMulticast(Object conteudo) throws Exception{

        Address cluster = null; //OBS.: não definir um destinatário significa enviar a TODOS os membros do cluster
        Message mensagem=new Message(cluster, conteudo);

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta da MAIORIA dos membros (MAJORITY) // Outras opções: ALL, FIRST, NONE
        opcoes.setAnycasting(false);


        RspList <Resposta> respList = despachante.castMessage(null, mensagem, opcoes); //envia o MULTICAST

        return respList;
    }

    private Resposta enviaUnicast(Address destino, Object conteudo) throws Exception{

        Message mensagem=new Message(destino, conteudo);

        RequestOptions opcoes = new RequestOptions();
        opcoes.setMode(ResponseMode.GET_ALL); // ESPERA receber a resposta do destino // Outras opções: ALL, MAJORITY, FIRST, NONE
        // opcoes.setMode(ResponseMode.GET_NONE); // não ESPERA receber a resposta do destino // Outras opções: ALL, MAJORITY, FIRST

        Resposta resp = despachante.sendMessage(mensagem, opcoes); //envia o UNICAST

        return resp;
    }


    public static void main(String[] args) throws Exception {
        new Tbank().start();
    }

}//class