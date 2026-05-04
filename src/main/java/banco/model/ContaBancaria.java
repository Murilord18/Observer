package banco.model;

import banco.observer.Observable;
import banco.observer.Observer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ContaBancaria implements Observable {

    private final String numeroConta;
    private final String agencia;
    private double saldo;
    private final List<Observer> observers;
    private final List<String> historicoMovimentacoes;

    public ContaBancaria(String numeroConta, String agencia, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.saldo = saldoInicial;
        this.observers = new ArrayList<>();
        this.historicoMovimentacoes = new ArrayList<>();
    }

    // -------------------------------------------------------------------------
    // Implementação de Observable
    // -------------------------------------------------------------------------

    @Override
    public void adicionarObserver(Observer observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removerObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notificarObservers(String evento, double valor) {
        for (Observer observer : observers) {
            observer.atualizar(evento, valor);
        }
    }

    // -------------------------------------------------------------------------
    // Operações bancárias (disparam notificações)
    // -------------------------------------------------------------------------

    /**
     * Realiza um depósito na conta e notifica os observadores.
     *
     * @param valor valor a ser depositado (deve ser positivo)
     * @throws IllegalArgumentException se o valor for inválido
     */
    public void depositar(double valor) {
        validarValor(valor);
        saldo += valor;
        String evento = TipoMovimentacao.DEPOSITO.getDescricao();
        registrarHistorico(evento, valor);
        notificarObservers(evento, valor);
    }

    /**
     * Realiza um saque na conta e notifica os observadores.
     *
     * @param valor valor a ser sacado
     * @throws IllegalArgumentException se o valor for inválido ou saldo insuficiente
     */
    public void sacar(double valor) {
        validarValor(valor);
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente. Saldo atual: R$ " + saldo);
        }
        saldo -= valor;
        String evento = TipoMovimentacao.SAQUE.getDescricao();
        registrarHistorico(evento, valor);
        notificarObservers(evento, valor);
    }

    /**
     * Realiza uma transferência para outra conta e notifica os observadores de ambas.
     *
     * @param destino conta de destino
     * @param valor   valor a ser transferido
     */
    public void transferir(ContaBancaria destino, double valor) {
        validarValor(valor);
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para transferência. Saldo atual: R$ " + saldo);
        }
        saldo -= valor;
        String eventoEnviada = TipoMovimentacao.TRANSFERENCIA_ENVIADA.getDescricao()
                + " para conta " + destino.getNumeroConta();
        registrarHistorico(eventoEnviada, valor);
        notificarObservers(eventoEnviada, valor);

        destino.receberTransferencia(valor, this.numeroConta);
    }

    /**
     * Recebe uma transferência de outra conta (uso interno).
     */
    void receberTransferencia(double valor, String contaOrigem) {
        saldo += valor;
        String eventoRecebida = TipoMovimentacao.TRANSFERENCIA_RECEBIDA.getDescricao()
                + " da conta " + contaOrigem;
        registrarHistorico(eventoRecebida, valor);
        notificarObservers(eventoRecebida, valor);
    }

    /**
     * Realiza o pagamento de uma conta/boleto e notifica os observadores.
     *
     * @param descricao descrição do pagamento
     * @param valor     valor a ser pago
     */
    public void pagar(String descricao, double valor) {
        validarValor(valor);
        if (valor > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para pagamento. Saldo atual: R$ " + saldo);
        }
        saldo -= valor;
        String evento = TipoMovimentacao.PAGAMENTO.getDescricao() + ": " + descricao;
        registrarHistorico(evento, valor);
        notificarObservers(evento, valor);
    }

    // -------------------------------------------------------------------------
    // Métodos auxiliares
    // -------------------------------------------------------------------------

    private void validarValor(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("O valor da operação deve ser positivo. Valor informado: " + valor);
        }
    }

    private void registrarHistorico(String evento, double valor) {
        historicoMovimentacoes.add(String.format("[%s] R$ %.2f | Saldo: R$ %.2f", evento, valor, saldo));
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public String getNumeroConta() {
        return numeroConta;
    }

    public String getAgencia() {
        return agencia;
    }

    public double getSaldo() {
        return saldo;
    }

    public List<String> getHistoricoMovimentacoes() {
        return Collections.unmodifiableList(historicoMovimentacoes);
    }

    public int getQuantidadeObservers() {
        return observers.size();
    }

    @Override
    public String toString() {
        return String.format("ContaBancaria{numero='%s', agencia='%s', saldo=R$ %.2f}",
                numeroConta, agencia, saldo);
    }
}
