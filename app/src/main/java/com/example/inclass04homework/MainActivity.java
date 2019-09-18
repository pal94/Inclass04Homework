package com.example.inclass04homework;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {
    SeekBar sb;
    TextView sb_text;
    int PROG;
    ExecutorService threadpool;
    ArrayList obj_arr;
    HeavyWork obj;

    public Double getAverage(ArrayList arrayList){
        Double sum = 0.0;

        for(int i = 0; i < PROG; i++){
            Double val = Double.parseDouble(arrayList.get(i).toString());
            sum = val + sum;
            Log.d("TAG", "doInBackground: in loop "+ arrayList.get(i));
        }
        Double avg = sum/PROG;
        return avg;
    }

    public Double getMax(ArrayList arrayList){

        Double max = Double.parseDouble(arrayList.get(0).toString());

        for (int i = 1; i < PROG; i++) {
            if (Double.parseDouble(arrayList.get(i).toString()) > max) {
                max = Double.parseDouble(arrayList.get(i).toString());
                Log.d("TAG", "doInBackground: in loop "+ arrayList.get(i));
            }
        }
        return max;
    }

    public Double getMin(ArrayList arrayList){

        Double min = Double.parseDouble(arrayList.get(0).toString());

        for (int i = 1; i < PROG; i++) {
            if (Double.parseDouble(arrayList.get(i).toString()) < min) {
                min = Double.parseDouble(arrayList.get(i).toString());
                Log.d("TAG", "doInBackground: in loop "+ arrayList.get(i));
            }
        }
        return min;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sb=findViewById(R.id.seekBar);
        sb.setMax(10);
        sb_text=findViewById(R.id.numComp);

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                //int val = (progress * (seekBar.getWidth()-2 * seekBar.getThumbOffset()) / seekBar.getMax());
                sb_text.setText("" + progress);
                PROG = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });
        threadpool= Executors.newFixedThreadPool(2);

        obj = new HeavyWork();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FutureTask<Double[]> results = new FutureTask(new DoWork());
                Log.d("TAG", results.toString());
                threadpool.submit(results);
            }
        });

    }
    class DoWork implements Callable<Double[]> {

        @Override
        public Double[] call() {
            obj_arr=obj.getArrayNumbers(PROG);
            Double[] results = new Double[3];

            Double avg = getAverage(obj_arr);
            results[2] = avg;
            Double max = getMax(obj_arr);
            results[1] = max;
            Double min = getMin(obj_arr);
            results[0] =  min;

            Log.d("TAG", "doInBackground: Average "+avg);
            Log.d("TAG", "doInBackground: Max "+max);
            Log.d("TAG", "doInBackground: Min "+min);

            return results;

        }
    }
}
