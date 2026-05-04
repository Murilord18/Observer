package banco.model;

import banco.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class NotificadorSMS implements Observer {

    private final String telefone;
    private final List<String> mensagensEnviadas;

    public NotificadorSMS(String telefone) {
        this.telefone = telefone;
        this.mensagensEnviadas = new ArrayList<>();
    }

    @Override
    public void atualizar(String evento, double valor) {
        String sms = String.format(
                "SMS para %s: Movimentação detectada - %s | Valor: R$ %.2f",
                telefone, evento, valor
        );
        mensagensEnviadas.add(sms);
        System.out.println(sms);
    }

    public String getTelefone() {
        return telefone;
    }

    public List<String> getMensagensEnviadas() {
        return Collections.unmodifiableList(mensagensEnviadas);
    }

    public int getQuantidadeMensagens() {
        return mensagensEnviadas.size();
    }
}
