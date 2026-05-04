package banco.model;

import banco.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class Cliente implements Observer {

    private final String cpf;
    private final String nome;
    private final String email;
    private final List<String> notificacoes;

    public Cliente(String cpf, String nome, String email) {
        this.cpf = cpf;
        this.nome = nome;
        this.email = email;
        this.notificacoes = new ArrayList<>();
    }


    @Override
    public void atualizar(String evento, double valor) {
        String mensagem = String.format(
                "Notificação para %s (%s): %s no valor de R$ %.2f",
                nome, email, evento, valor
        );
        notificacoes.add(mensagem);
        System.out.println(mensagem);
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }


    public List<String> getNotificacoes() {
        return Collections.unmodifiableList(notificacoes);
    }


    public String getUltimaNotificacao() {
        if (notificacoes.isEmpty()) {
            return null;
        }
        return notificacoes.get(notificacoes.size() - 1);
    }


    public int getQuantidadeNotificacoes() {
        return notificacoes.size();
    }

    @Override
    public String toString() {
        return String.format("Cliente{cpf='%s', nome='%s', email='%s'}", cpf, nome, email);
    }
}
