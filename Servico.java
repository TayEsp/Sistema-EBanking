import dados.conta;
import dados.extrato;
import java.util.*;

public class Servico {

    public static enum tipoServico {
        TRANSFERECIA,
        SALDO,
        EXTRATO,
        CRIACONTA;

    }

    Map <String,Conta> contas = new HashMap();
    String contaDest;
    String contaOrig;
    float valor;
    tipoServico tipo;

    public Servico(){}

    public boolean tranferencia(float valor, String dest, String orig){
        Conta contaDest = contas.get(dest);
        Conta contaOrig = contas.get(orig);

        //verificacao

        //debita das duas contas
    }

    
}
