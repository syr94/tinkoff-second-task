package com.tinkoff.tinkoffsecondtask.handler.impl;

import com.tinkoff.tinkoffsecondtask.client.Client;
import com.tinkoff.tinkoffsecondtask.handler.Handler;
import com.tinkoff.tinkoffsecondtask.model.Address;
import com.tinkoff.tinkoffsecondtask.model.Event;
import com.tinkoff.tinkoffsecondtask.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Component
public class HandlerImpl implements Handler {

    private final Client client;
    private final Executor taskExecutor;

    @Autowired
    public HandlerImpl(Client client, @Qualifier("taskExecutor") Executor taskExecutor) {
        this.client = client;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Асинхронно отправляет данные всем указанным получателям. Данные читаются один раз и отправляются каждому адресату.
     * В случае отклонения отправки (Result.REJECTED), метод повторяет попытку после задержки.
     * Процесс повторяется, пока данные не будут успешно отправлены или не произойдет прерывание потока.
     * Ошибки в процессе отправки логируются.
     *
     * @throws RuntimeException если поток был прерван во время ожидания перед повторной отправкой.
     */
    @Override
    public void performOperation() {
        Event event = client.readData();

        for (Address address : event.recipients()) {
            CompletableFuture.supplyAsync(() -> {
                Result result;
                do {
                    result = client.sendData(address, event.payload());
                    if (result == Result.REJECTED) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(timeout().toMillis());
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Interrupted while waiting to retry", e);
                        }
                    }
                } while (result == Result.REJECTED);
                return result;
            }, taskExecutor).exceptionally(ex -> {
                System.err.println("Failed to send data to " + address + ": " + ex.getMessage());
                return null;
            });
        }
    }

    @Override
    public Duration timeout() {
        return Duration.ofSeconds(1);
    }
}
