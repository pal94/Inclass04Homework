package com.example.inclass04homework;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.HeaderViewListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {
    SeekBar sb;
    TextView sb_text;
    TextView tv_avg;
    TextView tv_max;
    TextView tv_min;
    int PROG;
    ExecutorService threadpool;
    ArrayList obj_arr;
    HeavyWork obj;
    ProgressDialog pgbar;
    Handler handler;
    DecimalFormat df;

    public Double getAverage(ArrayList arrayList){
        Double sum = 0.0;

        for(int i = 0; i < PROG; i++){
            Double val = Double.parseDouble(arrayList.get(i).toString());
            sum = val + sum;
            Log.d("TAG", "doInBackground: in loop Average"+ arrayList.get(i));
        }
        Double avg = sum/PROG;
        return avg;
    }

    public Double getMax(ArrayList arrayList){

        Double max = Double.parseDouble(arrayList.get(0).toString());

        for (int i = 1; i < PROG; i++) {
            if (Double.parseDouble(arrayList.get(i).toString()) > max) {
                max = Double.parseDouble(arrayList.get(i).toString());
                Log.d("TAG", "doInBackground: in loop Max"+ arrayList.get(i));
            }
        }
        return max;
    }

    public Double getMin(ArrayList arrayList){

        Double min = Double.parseDouble(arrayList.get(0).toString());

        for (int i = 1; i < PROG; i++) {
            if (Double.parseDouble(arrayList.get(i).toString()) < min) {
                min = Double.parseDouble(arrayList.get(i).toString());
                Log.d("TAG", "doInBackground: in loop Min"+ arrayList.get(i));
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
        tv_avg = findViewById(R.id.tvAvg);
        tv_max=findViewById(R.id.tvMax);
        tv_min=findViewById(R.id.tvMin);


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
                Double[] result = new Double[3];
                pgbar = new ProgressDialog(MainActivity.this);
                pgbar.setMessage("Calculating");
                pgbar.setMax(100);
                pgbar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                pgbar.setCancelable(false);


                handler = new Handler(new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message msg) {
                        switch (msg.what)
                        {
                            case DoWork.STATUS_START:
                                pgbar.show();
                                Log.d("DEMO", "Starting...");
                                break;
                            case DoWork.STATUS_PROGRESS:
                                //pgbar.setProgress(msg.getData().getInt(DoWork.PROGRESS));
                                Log.d("DEMO", "Working.....");
                                break;
                            case DoWork.STATUS_STOP:
                                pgbar.dismiss();
                                Log.d("DEMO", "Stopping....");
                        }
                        return false;
                    }
                });




                Future<Double[]> future1 = threadpool.submit(new DoWork(PROG));
                try {
                    result = future1.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

                //threadpool.submit(results);

                    df = new DecimalFormat("0.00000000");
                    String avg = df.format(result[0]);
                    String max = df.format(result[1]);
                    String min = df.format(result[2]);

                    tv_avg.setText(avg);
                    tv_avg.setVisibility(View.VISIBLE);
                    tv_max.setText(max);
                    tv_max.setVisibility(View.VISIBLE);
                    tv_min.setText(min);
                    tv_min.setVisibility(View.VISIBLE);

                    threadpool.shutdown();


            }
        });

    }
    class DoWork implements Callable<Double[]> {
        static final int STATUS_START =0x00;
        static final int STATUS_GETAVG =0x01;
        static final int STATUS_MAX = 0x02;
        static final int STATUS_MIN=0x03;
        static final int STATUS_PROGRESS=0x05;
        static  final int STATUS_STOP=0x04;
        static final String PROGRESS = "Progress";
        private int seekprogress;

        public DoWork(int seekprogress) {
            this.seekprogress = seekprogress;
        }


        @Override
        public Double[] call() {

            Message startMessage= new Message();
            startMessage.what=STATUS_START;
            handler.sendMessage(startMessage);

            obj_arr=obj.getArrayNumbers(seekprogress);


            Double[] results = new Double[3];
            Double avg = getAverage(obj_arr);
            results[2] = avg;
            Double max = getMax(obj_arr);
            results[1] = max;
            Double min = getMin(obj_arr);
            results[0] =  min;

            Message stopMessage = new Message();
            stopMessage.what=STATUS_STOP;
            handler.sendMessage((stopMessage));

            Log.d("TAG", "doInBackground: Average "+avg);
            Log.d("TAG", "doInBackground: Max "+max);
            Log.d("TAG", "doInBackground: Min "+min);


            return results;

        }
    }
}
