package banco;

import banco.model.Cliente;
import banco.model.ContaBancaria;
import banco.model.NotificadorSMS;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


@DisplayName("Testes do Padrão Observer - Sistema Bancário")
class BancoObserverTest {

    private ContaBancaria conta;
    private Cliente cliente;
    private NotificadorSMS notificadorSMS;

    @BeforeEach
    void setUp() {
        conta = new ContaBancaria("12345-6", "0001", 1000.00);
        cliente = new Cliente("123.456.789-00", "Maria Oliveira", "maria@email.com");
        notificadorSMS = new NotificadorSMS("(32) 99999-8888");
    }

    // =========================================================================
    // Testes de registro de observers
    // =========================================================================

    @Test
    @DisplayName("Deve registrar um observer corretamente")
    void deveRegistrarObserver() {
        conta.adicionarObserver(cliente);
        assertEquals(1, conta.getQuantidadeObservers());
    }

    @Test
    @DisplayName("Deve registrar múltiplos observers")
    void deveRegistrarMultiplosObservers() {
        conta.adicionarObserver(cliente);
        conta.adicionarObserver(notificadorSMS);
        assertEquals(2, conta.getQuantidadeObservers());
    }

    @Test
    @DisplayName("Não deve registrar o mesmo observer duas vezes")
    void naoDeveRegistrarObserverDuplicado() {
        conta.adicionarObserver(cliente);
        conta.adicionarObserver(cliente);
        assertEquals(1, conta.getQuantidadeObservers());
    }

    @Test
    @DisplayName("Deve remover um observer corretamente")
    void deveRemoverObserver() {
        conta.adicionarObserver(cliente);
        conta.adicionarObserver(notificadorSMS);
        conta.removerObserver(cliente);
        assertEquals(1, conta.getQuantidadeObservers());
    }

    @Test
    @DisplayName("Deve iniciar sem observers")
    void deveIniciarSemObservers() {
        assertEquals(0, conta.getQuantidadeObservers());
    }

    // =========================================================================
    // Testes de depósito
    // =========================================================================

    @Test
    @DisplayName("Depósito deve atualizar o saldo corretamente")
    void depositoDeveAtualizarSaldo() {
        conta.depositar(500.00);
        assertEquals(1500.00, conta.getSaldo(), 0.01);
    }

    @Test
    @DisplayName("Depósito deve notificar o cliente")
    void depositoDeveNotificarCliente() {
        conta.adicionarObserver(cliente);
        conta.depositar(500.00);

        assertEquals(1, cliente.getQuantidadeNotificacoes());
        assertTrue(cliente.getUltimaNotificacao().contains("Depósito"));
        assertTrue(cliente.getUltimaNotificacao().contains("500,00"));
    }

    @Test
    @DisplayName("Depósito deve notificar todos os observers")
    void depositoDeveNotificarTodosObservers() {
        conta.adicionarObserver(cliente);
        conta.adicionarObserver(notificadorSMS);

        conta.depositar(300.00);

        assertEquals(1, cliente.getQuantidadeNotificacoes());
        assertEquals(1, notificadorSMS.getQuantidadeMensagens());
    }

    @Test
    @DisplayName("Depósito com valor zero deve lançar exceção")
    void depositoComValorZeroDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(0));
    }

    @Test
    @DisplayName("Depósito com valor negativo deve lançar exceção")
    void depositoComValorNegativoDeveLancarExcecao() {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(-100));
    }

    // =========================================================================
    // Testes de saque
    // =========================================================================

    @Test
    @DisplayName("Saque deve atualizar o saldo corretamente")
    void saqueDeveAtualizarSaldo() {
        conta.sacar(400.00);
        assertEquals(600.00, conta.getSaldo(), 0.01);
    }

    @Test
    @DisplayName("Saque deve notificar o cliente")
    void saqueDeveNotificarCliente() {
        conta.adicionarObserver(cliente);
        conta.sacar(200.00);

        assertEquals(1, cliente.getQuantidadeNotificacoes());
        assertTrue(cliente.getUltimaNotificacao().contains("Saque"));
        assertTrue(cliente.getUltimaNotificacao().contains("200,00"));
    }

    @Test
    @DisplayName("Saque com saldo insuficiente deve lançar exceção e não notificar")
    void saqueComSaldoInsuficienteDeveLancarExcecao() {
        conta.adicionarObserver(cliente);

        assertThrows(IllegalArgumentException.class, () -> conta.sacar(9999.00));
        assertEquals(0, cliente.getQuantidadeNotificacoes());
    }

    @Test
    @DisplayName("Saque com valor exato do saldo deve zerar a conta")
    void saqueComValorExatoDeveZerarConta() {
        conta.sacar(1000.00);
        assertEquals(0.00, conta.getSaldo(), 0.01);
    }

    // =========================================================================
    // Testes de transferência
    // =========================================================================

    @Test
    @DisplayName("Transferência deve debitar da origem e creditar no destino")
    void transferenciaDeveDebitarOrigemECreditarDestino() {
        ContaBancaria destino = new ContaBancaria("98765-4", "0001", 0.00);
        conta.transferir(destino, 300.00);

        assertEquals(700.00, conta.getSaldo(), 0.01);
        assertEquals(300.00, destino.getSaldo(), 0.01);
    }

    @Test
    @DisplayName("Transferência deve notificar cliente da conta de origem")
    void transferenciaDeveNotificarClienteOrigem() {
        conta.adicionarObserver(cliente);
        ContaBancaria destino = new ContaBancaria("98765-4", "0001", 0.00);

        conta.transferir(destino, 250.00);

        assertEquals(1, cliente.getQuantidadeNotificacoes());
        assertTrue(cliente.getUltimaNotificacao().contains("Transferência Enviada"));
    }

    @Test
    @DisplayName("Transferência deve notificar cliente da conta de destino")
    void transferenciaDeveNotificarClienteDestino() {
        ContaBancaria destino = new ContaBancaria("98765-4", "0001", 0.00);
        Cliente clienteDestino = new Cliente("987.654.321-00", "João Silva", "joao@email.com");
        destino.adicionarObserver(clienteDestino);

        conta.transferir(destino, 250.00);

        assertEquals(1, clienteDestino.getQuantidadeNotificacoes());
        assertTrue(clienteDestino.getUltimaNotificacao().contains("Transferência Recebida"));
    }

    @Test
    @DisplayName("Transferência com saldo insuficiente deve lançar exceção")
    void transferenciaComSaldoInsuficienteDeveLancarExcecao() {
        ContaBancaria destino = new ContaBancaria("98765-4", "0001", 0.00);
        assertThrows(IllegalArgumentException.class, () -> conta.transferir(destino, 5000.00));
    }

    // =========================================================================
    // Testes de pagamento
    // =========================================================================

    @Test
    @DisplayName("Pagamento deve atualizar o saldo corretamente")
    void pagamentoDeveAtualizarSaldo() {
        conta.pagar("Conta de luz", 150.00);
        assertEquals(850.00, conta.getSaldo(), 0.01);
    }

    @Test
    @DisplayName("Pagamento deve notificar o cliente com a descrição")
    void pagamentoDeveNotificarClienteComDescricao() {
        conta.adicionarObserver(cliente);
        conta.pagar("Conta de água", 80.00);

        assertEquals(1, cliente.getQuantidadeNotificacoes());
        assertTrue(cliente.getUltimaNotificacao().contains("Pagamento"));
        assertTrue(cliente.getUltimaNotificacao().contains("Conta de água"));
    }

    @Test
    @DisplayName("Pagamento com saldo insuficiente deve lançar exceção e não notificar")
    void pagamentoComSaldoInsuficienteDeveLancarExcecao() {
        conta.adicionarObserver(cliente);
        assertThrows(IllegalArgumentException.class, () -> conta.pagar("Boleto", 9999.00));
        assertEquals(0, cliente.getQuantidadeNotificacoes());
    }

    // =========================================================================
    // Testes de múltiplas movimentações / histórico
    // =========================================================================

    @Test
    @DisplayName("Observer removido não deve mais receber notificações")
    void observerRemovidoNaoDeveReceberNotificacoes() {
        conta.adicionarObserver(cliente);
        conta.depositar(100.00); // cliente recebe esta

        conta.removerObserver(cliente);
        conta.depositar(200.00); // cliente NÃO deve receber esta

        assertEquals(1, cliente.getQuantidadeNotificacoes());
    }

    @Test
    @DisplayName("Múltiplas movimentações devem gerar múltiplas notificações")
    void multiplosEventosDevemGerarMultiplasNotificacoes() {
        conta.adicionarObserver(cliente);

        conta.depositar(500.00);
        conta.sacar(100.00);
        conta.pagar("Internet", 99.90);

        assertEquals(3, cliente.getQuantidadeNotificacoes());
    }

    @Test
    @DisplayName("Histórico deve registrar todas as movimentações")
    void historicoDeveRegistrarTodasMovimentacoes() {
        conta.depositar(200.00);
        conta.sacar(50.00);
        conta.pagar("Boleto", 100.00);

        assertEquals(3, conta.getHistoricoMovimentacoes().size());
    }

    @Test
    @DisplayName("Saldo inicial deve ser preservado sem movimentações")
    void saldoInicialDeveSerPreservado() {
        assertEquals(1000.00, conta.getSaldo(), 0.01);
    }

    @Test
    @DisplayName("Conta sem observers não deve lançar exceção ao notificar")
    void contaSemObserversNaoDeveLancarExcecao() {
        assertDoesNotThrow(() -> conta.depositar(100.00));
    }
}
