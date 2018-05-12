package com.peihan.vancleef;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class TestAll {


    @Test
    public void testTime() throws Exception {
        System.out.println("1525358555");
        System.out.println(System.currentTimeMillis() / 1000);
    }

    @Test
    public void testSize() throws Exception {
        //String ss = "3386d37d7b8e5279d9ee20d3a9607635e67fcd8edd58ca84c864464d49fcd94a";
        //System.out.println(ss.length());

        int a = 1;
        int b = 2;
        String aa = "1";

        String ss = a + b + aa;
        System.out.println(ss);
    }

    @Test
    public void name() throws Exception {


        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            list.add(i);
        }


        ListIterator listIterator = list.listIterator();

        while (listIterator.hasNext()) {
            System.out.print("index:" + listIterator.nextIndex() + "    ");
            System.out.println(listIterator.next());
            System.out.print("index:" + listIterator.nextIndex() + "    ");
        }


    }
}
