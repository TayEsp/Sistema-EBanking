package dados;


public class Conta {
    String conta;
    int senha;
    float saldo = 10000;
    String extrato;

    public Conta(String conta,int senha){
        this.conta = conta;
        this.senha = senha;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }
    public void setSenha(int senha) {
        this.senha = senha;
    }
    public void setSaldo(Long saldo) {
        this.saldo = saldo;
    }

    public String getConta() {
        return conta;
    }
    public int getSenha() {
        return senha;
    }
    public Long getSaldo() {
        return saldo;
    }
}
