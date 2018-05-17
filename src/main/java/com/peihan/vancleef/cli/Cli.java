package com.peihan.vancleef.cli;

import com.peihan.vancleef.exception.base.ServiceException;
import io.netty.util.internal.SocketUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.cli.*;

import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;


/**
 * cli命令格式为 [order] [arg] [value] [arg] [value]......
 * 例如 send -from peihan -to lushu -amount 5
 */
public class Cli {

    private CliProxy cliProxy;
    private Options options;

    public Cli() {
        options = new Options();
        cliProxy = new CliProxy();
    }


    @AllArgsConstructor
    private enum Order {
        INIT(0, "init"),
        LIST(1, "list"),
        BALANCE(3, "balance"),
        SEND(4, "send"),
        WALLET(5, "wallet"),
        PULL(6, "pull"),
        PUSH(7, "push"),
        EXIT(8, "exit");

        @Getter
        private int index;
        @Getter
        private String option;
    }

    @AllArgsConstructor
    private enum Arg {
        ADDRESS(0, "address"),
        FROM(1, "from"),
        TO(2, "to"),
        AMOUNT(3, "amount");

        @Getter
        private int index;
        @Getter
        private String option;
    }


    public void parse(String[] args) throws ServiceException {

        checkArgs();
        //初始化命令选项
        initOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = commandLineParser.parse(options, args);
        } catch (ParseException e) {
            help();
            System.exit(0);
        }
        String order = args[0];

        if (Objects.equals(order, Order.LIST.getOption())) {
            cliProxy.showAllBlocks();
        } else if (Objects.equals(order, Order.INIT.getOption())) {
            String address = commandLine.getOptionValue(Arg.ADDRESS.getOption());
            cliProxy.initBlockChain(address);
        } else if (Objects.equals(order, Order.BALANCE.getOption())) {
            String address = commandLine.getOptionValue(Arg.ADDRESS.getOption());
            cliProxy.showBalance(address);
        } else if (Objects.equals(order, Order.SEND.getOption())) {
            String from = commandLine.getOptionValue(Arg.FROM.getOption());
            String to = commandLine.getOptionValue(Arg.TO.getOption());
            int amount = Integer.valueOf(commandLine.getOptionValue(Arg.AMOUNT.getOption()));
            cliProxy.transfer(from, to, amount);
        } else if (Objects.equals(order, Order.WALLET.getOption())) {
            cliProxy.createWallet();
        } else if (Objects.equals(order, Order.PULL.getOption())) {
            cliProxy.pull();
        } else if(Objects.equals(order,Order.PUSH.getOption())){
            cliProxy.push();
        } else if (Objects.equals(order, Order.EXIT.getOption())) {
            System.exit(0);
        } else {
            help();
        }

    }


    public void ui() throws ServiceException {
        help();
        while (true) {
            System.out.println(">>>");
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            String[] args = input.split("\\s+");
            parse(args);
        }
    }

    private void checkArgs() {
    }

    private void initOptions() {
        Option address = Option.builder(Arg.ADDRESS.getOption()).hasArg(true).build();
        Option from = Option.builder(Arg.FROM.getOption()).hasArg(true).build();
        Option to = Option.builder(Arg.TO.getOption()).hasArg(true).build();
        Option amount = Option.builder(Arg.AMOUNT.getOption()).hasArg(true).build();
        options.addOption(address);
        options.addOption(from);
        options.addOption(to);
        options.addOption(amount);
    }


    private void help() {
        System.out.println("Usage:");
        System.out.println("  balance -address [ADDRESS] (Get balance of ADDRESS)");
        System.out.println("  init -address [ADDRESS] (init blockchain)");
        System.out.println("  list (list all the blocks of the blockchain)");
        System.out.println("  send -from [FROM] -to [TO] -amount [AMOUNT] (Send AMOUNT of coins from FROM address to TO)");
        System.out.println("  wallet (create a wallet and return a address)");
        System.out.println("  pull (pull from other node to execute consensus algorithm)");
        System.out.println("  push (push to other node to broadcast all blocks)");
    }


}
