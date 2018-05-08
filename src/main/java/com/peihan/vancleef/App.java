package com.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;

public class App {

    public static void main(String[] args) {
        Cli cli = new Cli(args);
        cli.start();
    }

}
