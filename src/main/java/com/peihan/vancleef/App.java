package com.peihan.vancleef;

import com.peihan.vancleef.cli.Cli;
import com.peihan.vancleef.exception.base.ServiceException;

public class App {

    public static void main(String[] args) throws ServiceException {
        Cli cli = new Cli(args);
        cli.start();
    }

}
