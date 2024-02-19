package com.tinkoff.tinkoffsecondtask.model;

import java.util.List;

public record Event(List<Address> recipients, Payload payload) {

}
