package com.tinkoff.tinkoffsecondtask.client;

import com.tinkoff.tinkoffsecondtask.model.Address;
import com.tinkoff.tinkoffsecondtask.model.Event;
import com.tinkoff.tinkoffsecondtask.model.Payload;
import com.tinkoff.tinkoffsecondtask.model.Result;

public interface Client {
    //блокирующий метод для чтения данных
    Event readData();

    //блокирующий метод отправки данных
    Result sendData(Address dest, Payload payload);
}
