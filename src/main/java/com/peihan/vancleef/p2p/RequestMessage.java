package com.peihan.vancleef.p2p;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RequestMessage<T> {
    private Action action;

    private T data;

    public RequestMessage(Action action,T data) {
        this.action = action;
        this.data = data;
    }
}
