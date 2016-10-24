package com.example.tiger.multi_threaded;

        import android.app.ProgressDialog;
        import android.content.Context;
        import android.os.Handler;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.Button;
        import android.widget.ListView;
        import android.widget.Toast;

        import java.io.BufferedReader;
        import java.io.FileInputStream;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStreamReader;
        import java.util.ArrayList;
        import java.util.List;


public class MainActivity extends AppCompatActivity {
    static final int READ_BLOCK_SIZE = 100;
    String MY_FILE_NAME;
    List<String> total;
    ListView totalView;
    ArrayAdapter<String> arrayAdapter;
    Button loadButton;
    Button createButton;
    Button clearButton;
    Thread thread1;
    Thread thread2;
    ///////////////////////////////
    Button btnStartProgress;
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarHandler = new Handler();

    public int fileSize = 0;

    //////////////////////////////////////////////////
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createButton = (Button) findViewById(R.id.createButton);
        loadButton = (Button) findViewById(R.id.loadButton);
        clearButton = (Button) findViewById(R.id.button3);
        //reference to total
        totalView = (ListView) findViewById(R.id.MyListView);
        total = new ArrayList<String>();
        arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                total);
        totalView.setAdapter(arrayAdapter);

        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(final View v) {
                ///////////////////////////////

                // prepare for a progress bar dialog
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("File downloading ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

                //reset progress bar status
                progressBarStatus = 0;

                //reset filesize
                fileSize = 0;
                ////////////////////////////////
                thread1 = new Thread(new Runnable() {
                    public void run() {
                        while (progressBarStatus < 100) {
                            // process some tasks
                            progressBarStatus = createFile(v);
                            // your computer is too fast, sleep 1 second
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Update the progress bar
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });
                        }

                        // ok, file is downloaded,
                        if (progressBarStatus >= 100) {

                            // sleep 2 seconds, so that you can see the 100%
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // close the progress bar dialog
                            progressBar.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                try {
                    thread1.start();
                }catch (Exception e) {
                    e.printStackTrace();
                }
                Toast.makeText(getBaseContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        loadButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(final View v) {
                ///////////////////////////////

                // prepare for a progress bar dialog
                progressBar = new ProgressDialog(v.getContext());
                progressBar.setCancelable(true);
                progressBar.setMessage("File downloading ...");
                progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressBar.setProgress(0);
                progressBar.setMax(100);
                progressBar.show();

                //reset progress bar status
                progressBarStatus = 0;

                //reset filesize
                fileSize = 0;
                ////////////////////////////////
                thread2 = new Thread(new Runnable() {
                    public void run() {
                        while (progressBarStatus < 100) {
                            // process some tasks
                            progressBarStatus = Load(v);
                            // your computer is too fast, sleep 1 second
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // Update the progress bar
                            progressBarHandler.post(new Runnable() {
                                public void run() {
                                    progressBar.setProgress(progressBarStatus);
                                }
                            });
                        }

                        // ok, file is downloaded,
                        if (progressBarStatus >= 100) {

                            // sleep 2 seconds, so that you can see the 100%
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            // close the progress bar dialog
                            progressBar.dismiss();
                        }
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                arrayAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                });
                thread2.start();
            }
        });


        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (arrayAdapter != null) {
                    arrayAdapter.clear();
                    arrayAdapter.notifyDataSetChanged();
                } // end if
            }// end onClick
        });



    }

    public int createFile(View v) {

        MY_FILE_NAME = "numbers.txt";
        String to;
        // Create a new output file stream
        try {
            FileOutputStream fileos = openFileOutput(MY_FILE_NAME, Context.MODE_PRIVATE);

            for (; fileSize <= 10; ) {

                to = Integer.toString(fileSize);
                fileos.write(to.getBytes());
                fileos.write(System.getProperty("line.separator").getBytes());
                System.out.println(fileSize);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(fileSize*10);
                fileSize++;
            }
            fileos.close();

            //display file saved message
            //Toast.makeText(getBaseContext(), "File saved successfully!", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return 100;
    }

    // Read text from file
    public int Load(View v) {
        String to;
        //reading text from file
        try {
            FileInputStream fileIn = openFileInput(MY_FILE_NAME);
            InputStreamReader InputRead = new InputStreamReader(fileIn);
            BufferedReader r = new BufferedReader(InputRead);

            String line;
            while ((line = r.readLine()) != null) {
                to = Integer.toString(fileSize);
                total.add(line);
                System.out.println(fileSize);
                try {
                    Thread.sleep(250);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progressBar.setProgress(fileSize*10);
                fileSize++;
            }
            InputRead.close();

        } catch (IOException e) {
        }
        return 100;
    }

}