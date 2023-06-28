/*
 SAIBA MAIS: http://www.jgroups.org/manual/html/user-building-blocks.html#MessageDispatcher
/**/

import java.util.Collection;

import javax.swing.text.View;

import org.jgroups.*;
import org.jgroups.blocks.*;
import org.jgroups.util.*;


public class Tbank extends ReceiverAdapter implements RequestHandler {

    JChannel canalDeComunicacao;
    MessageDispatcher  despachante;
    Servico estado;

    private void start() throws Exception {

        //Cria o canal de comunicação com uma configuração XML do JGroups
        canalDeComunicacao=new JChannel("cast.xml");        

        despachante=new MessageDispatcher(canalDeComunicacao, null, null, this);

        canalDeComunicacao.setReceiver(this);	//quem irá lidar com as mensagens recebidas

        estado = new Servico();

        canalDeComunicacao.connect("Tenacious Bank");

            aplicacao();
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

      if(serv.tipo == tipoServico.SALDO)
        return " SIM "; // resposta padrão desse helloworld à requisição contida na mensagem
      else if(serv.tipo == tipoServico.EXTRATO)
        return " SIM "; // resposta padrão desse helloworld à requisição contida na mensagem
      else if(serv.tipo == tipoServico.CRIACONTA) {
          return " SIM "; // resposta padrão desse helloworld à requisição contida na mensagem
      }
      else if(serv.tipo == tipoServico.TRANSFERECIA) {
          return estado.tranferecia(serv.valor,serv.dest,serv.orig); // resposta padrão desse helloworld à requisição contida na mensagem
      }
      else if(serv.tipo == tipoServico.DELETACONTA)
          return " SIM "; // resposta padrão desse helloworld à requisição contida na mensagem

    }

    // -=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Código-fonte da minha aplicação de exemplo

    final int TAMANHO_MINIMO_CLUSTER = 5;

    private void aplicacao() {

        int menu;

        switch(menu){
            case servico.tipoServico.TRANSFERENCIA:
                Servico serv = new Servico();
                serv.contaDest = contadestdig;
                serv.contaOrig = contaorgdig;
                serv.valor = valordig;
                serv.tipo = servico.tipoServico.TRANSFERENCIA;
                RspList respostas = enviaMulticast(serv);
                if(!respostas.values.contains(false)){
                    System.out.println("Tranfencia realizada com sucesso");
                }else{
                    System.out.println("ERRO: Tranfencia nao realizada");
                }
                break;
        }


    }//aplicacao  


    private void menu(){
        System.out.print("=========Bem Vindo ao Tenacious Bank==========");
    }

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
