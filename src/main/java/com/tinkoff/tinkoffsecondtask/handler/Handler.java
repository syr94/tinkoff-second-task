package com.tinkoff.tinkoffsecondtask.handler;

import java.time.Duration;

public interface Handler {
    Duration timeout();

    void performOperation();
}
