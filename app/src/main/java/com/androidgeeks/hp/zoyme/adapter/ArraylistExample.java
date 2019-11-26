package com.androidgeeks.hp.zoyme.adapter;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by hp on 1/15/2018.
 */

public class ArraylistExample {

    ArrayList al = new ArrayList();          //Size:  0, Capacity:  0


    public static void main(String[] args)
            throws Exception {
        ArrayList al = new ArrayList();
        getCapacity(al);
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");
        al.add("shailesh");al.add("shailesh");
        al.add("shailesh");


        getCapacity(al);
    }

    static void getCapacity(ArrayList<?> l)
            throws Exception {
        Field dataField = ArrayList.class.getDeclaredField("elementData");
        dataField.setAccessible(true);
        System.out.format("Size: %2d, Capacity: %2d%n", l.size(), ((Object[]) dataField.get(l)).length);
    }
}