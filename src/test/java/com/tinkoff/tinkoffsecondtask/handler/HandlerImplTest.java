package com.tinkoff.tinkoffsecondtask.handler;

import static org.mockito.Mockito.*;

import com.tinkoff.tinkoffsecondtask.client.Client;
import com.tinkoff.tinkoffsecondtask.handler.impl.HandlerImpl;
import com.tinkoff.tinkoffsecondtask.model.Address;
import com.tinkoff.tinkoffsecondtask.model.Event;
import com.tinkoff.tinkoffsecondtask.model.Payload;
import com.tinkoff.tinkoffsecondtask.model.Result;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.concurrent.Executor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HandlerImplTest {

    @Mock
    private Client client;

    @Mock
    private Executor taskExecutor;

    @InjectMocks
    private HandlerImpl handler;

    private Payload payload;
    private Address address;

    @BeforeEach
    void setUp() {
        address = new Address("datacenter1", "node1");
        payload = new Payload("source", "data".getBytes());
        Event event = new Event(List.of(address), payload);

        when(client.readData()).thenReturn(event);
        doAnswer(invocation -> {
            ((Runnable) invocation.getArgument(0)).run();
            return null;
        }).when(taskExecutor).execute(any(Runnable.class));
    }

    @Test
    void performOperationShouldSendDataSuccessfully() {
        when(client.sendData(any(Address.class), any(Payload.class))).thenReturn(Result.ACCEPTED);

        handler.performOperation();

        verify(client, times(1)).sendData(eq(address), eq(payload));
    }

    @Test
    void performOperationShouldRetrySendingDataWhenRejected() {
        when(client.sendData(any(Address.class), any(Payload.class)))
                .thenReturn(Result.REJECTED)
                .thenReturn(Result.ACCEPTED);

        handler.performOperation();

        verify(client, times(2)).sendData(eq(address), eq(payload));
    }
}
