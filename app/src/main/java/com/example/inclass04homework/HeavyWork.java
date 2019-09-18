package com.example.inclass04homework;
import java.util.ArrayList;
import java.util.Random;


public class HeavyWork {
    static final int COUNT = 900000;
    static ArrayList<Double> getArrayNumbers(int n){
        ArrayList<Double> returnArray = new ArrayList<>();

        for (int i=0; i<n; i++){
            returnArray.add(getNumber());
        }

        return returnArray;
    }

    static double getNumber(){
        double num = 0;
        Random rand = new Random();
        for(int i=0;i<COUNT; i++){
            num = num + rand.nextDouble();
        }
        return num / ((double) COUNT);
    }
}