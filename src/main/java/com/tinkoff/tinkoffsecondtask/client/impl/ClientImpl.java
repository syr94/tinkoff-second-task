package com.tinkoff.tinkoffsecondtask.client.impl;

import com.tinkoff.tinkoffsecondtask.client.Client;
import com.tinkoff.tinkoffsecondtask.model.Address;
import com.tinkoff.tinkoffsecondtask.model.Event;
import com.tinkoff.tinkoffsecondtask.model.Payload;
import com.tinkoff.tinkoffsecondtask.model.Result;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientImpl implements Client {

    @Override
    public Event readData() {
        return new Event(List.of(
                new Address("datacenter", "node")),
                new Payload("source", "data".getBytes()));
    }

    @Override
    public Result sendData(Address dest, Payload payload) {
        return Result.ACCEPTED;
    }
}
