package com.peihan.vancleef.cli;

import com.peihan.vancleef.exception.base.ServiceException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.*;

public class Cli {

    private CliFacade cliFacade;
    private Options options;
    private String[] args;

    public Cli(String[] args) {
        this.args = args;
        options = new Options();
        cliFacade = new CliFacadeImpl();
    }


    @AllArgsConstructor
    private enum Order {

        INIT(0, "init", "init the blockchains"),
        LIST(1, "list", "show all blocks"),
        ADD(2, "add", "add a block"),
        BALANCE(3, "balance", "query balance of the address");

        @Getter
        private int index;
        @Getter
        private String option;
        @Getter
        private String desc;
    }


    public void start() throws ServiceException {

        checkArgs();
        //初始化命令选项
        initOptions();

        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {

        }
        if (commandLine.hasOption(Order.LIST.getOption())) {
            cliFacade.showAllBlocks();
        } else if (commandLine.hasOption(Order.INIT.getOption())) {
            String address = commandLine.getOptionValue(Order.INIT.getOption());
            cliFacade.initBlockChain(address);
        } else if (commandLine.hasOption(Order.ADD.getOption())) {
            String data = commandLine.getOptionValue(Order.ADD.getOption());
            cliFacade.addBlock(data);
        } else if (commandLine.hasOption(Order.BALANCE.getOption())) {
            String data = commandLine.getOptionValue(Order.BALANCE.getOption());
            System.out.println(cliFacade.getBalance(data));
        } else {
            help();
        }

    }

    private void help() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("vcf", options);
    }

    private void checkArgs() {
    }

    private void initOptions() {
        Option init = Option.builder(Order.INIT.getOption()).hasArg(true).desc(Order.INIT.getDesc()).build();
        Option start = Option.builder(Order.LIST.getOption()).desc(Order.LIST.getDesc()).build();
        Option add = Option.builder(Order.ADD.getOption()).hasArg(true).desc(Order.ADD.getDesc()).build();
        Option balance = Option.builder(Order.BALANCE.getOption()).hasArg(true).desc(Order.BALANCE.getDesc()).build();
        options.addOption(init);
        options.addOption(start);
        options.addOption(add);
        options.addOption(balance);
    }
}
