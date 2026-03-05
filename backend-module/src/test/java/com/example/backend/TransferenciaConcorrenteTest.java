package com.example.backend;

import com.example.ejb.service.BeneficioEjbService;

import com.example.ejb.model.Beneficio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class TransferenciaConcorrenteTest {

    @Autowired
    private BeneficioEjbService ejbService;

    private Long idA;
    private Long idB;

    @BeforeEach
    void setup() {
        // Usa o service que já tem @Transactional
        Beneficio a = ejbService.criar("Conta A", "teste", new BigDecimal("1000.00"));
        Beneficio b = ejbService.criar("Conta B", "teste", new BigDecimal("1000.00"));
        idA = a.getId();
        idB = b.getId();
    }

    @Test
    void deveManterConsistenciaSobConcorrencia() throws InterruptedException {
        int numeroDeThreads = 10;
        BigDecimal valorTransferencia = new BigDecimal("100.00");

        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch latch = new CountDownLatch(numeroDeThreads); // sincroniza início
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(() -> {
                latch.countDown();
                try {
                    latch.await(); // todas as threads esperam aqui antes de iniciar
                    ejbService.transfer(idA, idB, valorTransferencia);
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    falhas.incrementAndGet();
                    System.out.println("Falha esperada: " + e.getMessage());
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // Busca saldos finais
        BigDecimal saldoA = ejbService.buscarPorId(idA).get().getValor();
        BigDecimal saldoB = ejbService.buscarPorId(idB).get().getValor();

        System.out.println("Sucessos: " + sucessos.get());
        System.out.println("Falhas:   " + falhas.get());
        System.out.println("Saldo A:  " + saldoA);
        System.out.println("Saldo B:  " + saldoB);

        // A soma total NUNCA pode mudar — invariante principal
        BigDecimal somaTotal = saldoA.add(saldoB);
        assertThat(somaTotal).isEqualByComparingTo("2000.00");

        // Cada transferência bem-sucedida moveu 100 reais
        BigDecimal transferido = new BigDecimal("100.00").multiply(new BigDecimal(sucessos.get()));
        assertThat(saldoA).isEqualByComparingTo(new BigDecimal("1000.00").subtract(transferido));
        assertThat(saldoB).isEqualByComparingTo(new BigDecimal("1000.00").add(transferido));
    }

    @Test
    void deveFalharQuandoSaldoInsuficienteComConcorrencia() throws InterruptedException {
        // Atualiza saldo da Conta A para 150 via service (já tem @Transactional)
        ejbService.atualizar(idA, "Conta A", "teste", new BigDecimal("150.00"));

        int numeroDeThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch latch = new CountDownLatch(numeroDeThreads);
        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        for (int i = 0; i < numeroDeThreads; i++) {
            executor.submit(() -> {
                latch.countDown();
                try {
                    latch.await();
                    ejbService.transfer(idA, idB, new BigDecimal("100.00"));
                    sucessos.incrementAndGet();
                } catch (Exception e) {
                    falhas.incrementAndGet();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        BigDecimal saldoA = ejbService.buscarPorId(idA).get().getValor();
        BigDecimal saldoB = ejbService.buscarPorId(idB).get().getValor();

        System.out.println("Sucessos: " + sucessos.get());
        System.out.println("Saldo A:  " + saldoA);
        System.out.println("Saldo B:  " + saldoB);

        assertThat(saldoA.add(saldoB)).isEqualByComparingTo("1150.00");
        assertThat(saldoA).isGreaterThanOrEqualTo(BigDecimal.ZERO);
        assertThat(sucessos.get()).isLessThanOrEqualTo(1);
    }
}