package com.peihan.vancleef.p2p;

import lombok.Data;

@Data
public class RequestMessage {
    private Action action;

    public RequestMessage(Action action) {
        this.action = action;
    }
}
