package com.peihan.vancleef.cli;

import org.apache.commons.cli.*;

public class Cli {

    private Options options;
    private String[] args;

    public Cli(String[] args) {
        this.args = args;
        options = new Options();
    }


    public void start() {
    }
}
