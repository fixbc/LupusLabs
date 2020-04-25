package edu.bme3890.lupuslabs;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    //view-related variables
    ImageView imageView, cropImageView, cropImageView2, cropImageView3, cropImageView4, imageView5, imageView6;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //file-related variables
    Bitmap imageBitmap1;
    Bitmap imageBitmap2;
    Bitmap imageBitmap3;
    File imageFile1;
    File imageFile2;
    File imageFile3;

    //graph-related variables
    GraphView graph;

    ImageView negativeImageView;
    Bitmap negativeBitmap;
    double[] negativeRGBVector = new double[3];

    ImageView mgdL15ImageView;
    Bitmap mgdL15Bitmap;
    double[] mgdL15RGBVector = new double[3];

    ImageView mgdL30ImageView;
    Bitmap mgdL30Bitmap;
    double[] mgdL30RGBVector = new double[3];

    ImageView mgdL100ImageView;
    Bitmap mgdL100Bitmap;
    double[] mgdL100RGBVector = new double[3];

    ImageView mgdL300ImageView;
    Bitmap mgdL300Bitmap;
    double[] mgdL300RGBVector = new double[3];

    ImageView mgdL2000ImageView;
    Bitmap mgdL2000Bitmap;
    double[] mgdL2000RGBVector = new double[3];

    double[] sampleRGBVector1 = new double[3];
    double[] sampleRGBVector2 = new double[3];
    double[] sampleRGBVector3 = new double[3];

    TextView resultTextView;
    Button barGraphBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent nameIntent = getIntent();
        String FILENAME1 = nameIntent.getStringExtra("image1");
        String FILENAME2 = nameIntent.getStringExtra("image2");
        String FILENAME3 = nameIntent.getStringExtra("image3");

        imageView = findViewById(R.id.imageView);
        imageView5 = findViewById(R.id.imageView5);
        imageView6 = findViewById(R.id.imageView6);
        cropImageView = findViewById(R.id.cropAreaImageView);
        cropImageView2 = findViewById(R.id.cropAreaImageView2);
        cropImageView3 = findViewById(R.id.cropAreaImageView3);
        cropImageView4 = findViewById(R.id.cropAreaImageView4);
        TextView imageSize = findViewById(R.id.imageSizeTV);

        //Button lineSeriesBtn = findViewById(R.id.lineSeriesButton);
        barGraphBtn = findViewById(R.id.runTestButton);
        graph = findViewById(R.id.graph);

        //assume image already exists in device
        //load image file and create bitmap
        File mediaStorageDir = new File(getFilesDir(),"TCAImages");
        imageFile1 = new File(mediaStorageDir, FILENAME1);
        imageView.setImageURI(Uri.fromFile(imageFile1));
        imageBitmap1 = BitmapFactory.decodeFile(imageFile1.getAbsolutePath());

        imageFile2 = new File(mediaStorageDir, FILENAME2);
        imageView5.setImageURI(Uri.fromFile(imageFile2));
        imageBitmap2 = BitmapFactory.decodeFile(imageFile2.getAbsolutePath());

        imageFile3 = new File(mediaStorageDir, FILENAME3);
        imageView6.setImageURI(Uri.fromFile(imageFile3));
        imageBitmap3 = BitmapFactory.decodeFile(imageFile3.getAbsolutePath());

        //indicate size of image
        imageSize.setText(imageBitmap1.getWidth() + " x " +
                imageBitmap1.getHeight());

        negativeImageView = findViewById(R.id.negativeImageView);
        mgdL15ImageView = findViewById(R.id.mgdL15ImageView);
        mgdL30ImageView = findViewById(R.id.mgdL30ImageView);
        mgdL100ImageView = findViewById(R.id.mgdL100ImageView);
        mgdL300ImageView = findViewById(R.id.mgdL300ImageView);
        mgdL2000ImageView = findViewById(R.id.mgdL2000ImageView);

        negativeBitmap = ((BitmapDrawable) negativeImageView.getDrawable()).getBitmap();
        mgdL15Bitmap = ((BitmapDrawable) mgdL15ImageView.getDrawable()).getBitmap();
        mgdL30Bitmap = ((BitmapDrawable) mgdL30ImageView.getDrawable()).getBitmap();
        mgdL100Bitmap = ((BitmapDrawable) mgdL100ImageView.getDrawable()).getBitmap();
        mgdL300Bitmap = ((BitmapDrawable) mgdL300ImageView.getDrawable()).getBitmap();
        mgdL2000Bitmap = ((BitmapDrawable) mgdL2000ImageView.getDrawable()).getBitmap();

        negativeRGBVector = getavgRGBVector(negativeBitmap);
        mgdL15RGBVector = getavgRGBVector(mgdL15Bitmap);
        mgdL30RGBVector = getavgRGBVector(mgdL30Bitmap);
        mgdL100RGBVector = getavgRGBVector(mgdL100Bitmap);
        mgdL300RGBVector = getavgRGBVector(mgdL300Bitmap);
        mgdL2000RGBVector = getavgRGBVector(mgdL2000Bitmap);

        negativeImageView.setVisibility(View.GONE);
        mgdL15ImageView.setVisibility(View.GONE);
        mgdL30ImageView.setVisibility(View.GONE);
        mgdL100ImageView.setVisibility(View.GONE);
        mgdL300ImageView.setVisibility(View.GONE);
        mgdL2000ImageView.setVisibility(View.GONE);

        resultTextView = findViewById(R.id.resultTextView);
    }

    //function activated on button press
    public void createGraph(View v){
        //determine which button was selected
        switch (v.getId()){
            case R.id.runTestButton://barButton Graph
                //do something
                sampleRGBVector1 = getavgRGBVector(extractCropArea(imageBitmap1));
                sampleRGBVector2 = getavgRGBVector(extractCropArea(imageBitmap2));
                sampleRGBVector3 = getavgRGBVector(extractCropArea(imageBitmap3));
                compareRGBVectors(sampleRGBVector3);
                imageView.setVisibility(View.GONE);
                imageView5.setVisibility(View.GONE);
                imageView6.setVisibility(View.GONE);
                barGraphBtn.setVisibility(View.GONE);
                runLineSeries(sampleRGBVector1, sampleRGBVector2, sampleRGBVector3);
                break;
            //case R.id.lineSeriesButton:
                //do something
                //runLineSeries(extractCropArea());
                //imageView.setVisibility(View.GONE);
                //break;
            default:
                //do nothing

        }
    }

    public Bitmap extractCropArea(Bitmap imageBitmap){
        int x1, y1, x2, y2, x1_2, x2_2, y1_2, y2_2;
        x1 = 0;
        x2 = 3264;
        y1 = 1150;
        y2 = 1350;

        //show region of interest in crop window
        Bitmap croppedImageBitmap1 = Bitmap.createBitmap(imageBitmap1,x1, y1, x2-x1, y2-y1);
        cropImageView.setImageBitmap(croppedImageBitmap1);

        // define region of interest for albumin pad
        x1_2 = 990;
        x2_2 = 1190;
        y1_2 = 1150;
        y2_2 = 1350;
        Bitmap croppedImageBitmap2 = Bitmap.createBitmap(imageBitmap1,x1_2, y1_2, x2_2-x1_2, y2_2-y1_2);
        cropImageView2.setImageBitmap(croppedImageBitmap2);

        Bitmap croppedImageBitmap3 = Bitmap.createBitmap(imageBitmap2,x1_2, y1_2, x2_2-x1_2, y2_2-y1_2);
        cropImageView3.setImageBitmap(croppedImageBitmap3);

        Bitmap croppedImageBitmap4 = Bitmap.createBitmap(imageBitmap3,x1_2, y1_2, x2_2-x1_2, y2_2-y1_2);
        cropImageView4.setImageBitmap(croppedImageBitmap4);

        croppedImageBitmap2 = Bitmap.createBitmap(imageBitmap,x1_2, y1_2, x2_2-x1_2, y2_2-y1_2);

        return croppedImageBitmap2;
    }

    public double[] getavgRGBVector(Bitmap croppedIB) {
        int i = 0, h = croppedIB.getHeight()/2, w = croppedIB.getWidth()/2;
        int numPixels = h * w;

        int[] redPixels = new int[numPixels], greenPixels = new int[numPixels],
                bluePixels = new int[numPixels];

        //extract color values into appropriate int arrays
        for (int x = w/2; x < w + (w/2); x++){
            for (int y = h/2; y < h + (h/2); y++){
                int pixel = croppedIB.getPixel(x,y);
                redPixels[i] = Color.red(pixel);
                greenPixels[i] = Color.green(pixel);
                bluePixels[i++] = Color.blue(pixel);
            }
        }

        // find average red value
        int redSum = 0;
        for (int value : redPixels) {
            redSum += value;
        }

        double redAvg = (double) redSum / redPixels.length;

        // find average green value
        int greenSum = 0;
        for (int value : greenPixels) {
            greenSum += value;
        }

        double greenAvg = (double) greenSum / greenPixels.length;

        // find average blue value
        int blueSum = 0;
        for (int value : bluePixels) {
            blueSum += value;
        }

        double blueAvg = (double) blueSum / bluePixels.length;

        double[] avgRGBVector = {redAvg, greenAvg, blueAvg};

        return avgRGBVector;
    }

    public void compareRGBVectors(double[] sampleRGBVector) {

        double[] angles = new double[6];

        angles[0] = Math.sqrt(Math.pow(sampleRGBVector[0] - negativeRGBVector[0], 2) + Math.pow(sampleRGBVector[1] - negativeRGBVector[1], 2) +
                        Math.pow(sampleRGBVector[2] - negativeRGBVector[2], 2));

        angles[1] = Math.sqrt(Math.pow(sampleRGBVector[0] - mgdL15RGBVector[0], 2) + Math.pow(sampleRGBVector[1] - mgdL15RGBVector[1], 2) +
                Math.pow(sampleRGBVector[2] - mgdL15RGBVector[2], 2));

        angles[2] = Math.sqrt(Math.pow(sampleRGBVector[0] - mgdL30RGBVector[0], 2) + Math.pow(sampleRGBVector[1] - mgdL30RGBVector[1], 2) +
                Math.pow(sampleRGBVector[2] - mgdL30RGBVector[2], 2));

        angles[3] = Math.sqrt(Math.pow(sampleRGBVector[0] - mgdL100RGBVector[0], 2) + Math.pow(sampleRGBVector[1] - mgdL100RGBVector[1], 2) +
                Math.pow(sampleRGBVector[2] - mgdL100RGBVector[2], 2));

        angles[4] = Math.sqrt(Math.pow(sampleRGBVector[0] - mgdL300RGBVector[0], 2) + Math.pow(sampleRGBVector[1] - mgdL300RGBVector[1], 2) +
                Math.pow(sampleRGBVector[2] - mgdL300RGBVector[2], 2));

        angles[5] = Math.sqrt(Math.pow(sampleRGBVector[0] - mgdL2000RGBVector[0], 2) + Math.pow(sampleRGBVector[1] - mgdL2000RGBVector[1], 2) +
                Math.pow(sampleRGBVector[2] - mgdL2000RGBVector[2], 2));

        int index = 0;
        double min = angles[index];

        for (int i=1; i < angles.length; i++) {

            if (angles[i] < min) {
                min = angles[i];
                index = i;
            }
        }

        if (index == 0) {
            negativeImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: negative");
        } else if (index == 1) {
            mgdL15ImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: 0-15 mg/dL");
        } else if (index == 2) {
            mgdL30ImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: 15-30 mg/dL");
        } else if (index == 3) {
            mgdL100ImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: 30-100 mg/dL");
        } else if (index == 4) {
            mgdL300ImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: 100-300 mg/dL");
        } else if (index == 5) {
            mgdL2000ImageView.setVisibility(View.VISIBLE);
            resultTextView.setText("Result: 300-2000 mg/dL");
        }

    }

    public void runLineSeries (double[] RGBVector1, double[] RGBVector2, double[] RGBVector3) {
        //for each pixel in the image, extract the red, green and blue values

        DataPoint[] redPixels = new DataPoint[3], greenPixels = new DataPoint[3],
                bluePixels = new DataPoint[3];

        redPixels[0] = new DataPoint(0, RGBVector1[0]);
        redPixels[1] = new DataPoint(1, RGBVector2[0]);
        redPixels[2] = new DataPoint(2, RGBVector3[0]);

        greenPixels[0] = new DataPoint(0, RGBVector1[1]);
        greenPixels[1] = new DataPoint(1, RGBVector2[1]);
        greenPixels[2] = new DataPoint(2, RGBVector3[1]);

        bluePixels[0] = new DataPoint(0, RGBVector1[2]);
        bluePixels[1] = new DataPoint(1, RGBVector2[2]);
        bluePixels[2] = new DataPoint(2, RGBVector3[2]);

        //create line series for each color
        LineGraphSeries<DataPoint> redSeries = new LineGraphSeries<>(redPixels);
        LineGraphSeries<DataPoint> greenSeries = new LineGraphSeries<>(greenPixels);
        LineGraphSeries<DataPoint> blueSeries = new LineGraphSeries<>(bluePixels);

        //add line series to the graph
        graph.removeAllSeries(); //clears the graph of previous lines
        graph.addSeries(redSeries);
        redSeries.setColor(Color.RED);
        redSeries.setThickness(1);
        graph.addSeries(greenSeries);
        greenSeries.setColor(Color.GREEN);
        greenSeries.setThickness(1);
        graph.addSeries(blueSeries);
        blueSeries.setColor(Color.BLUE);
        blueSeries.setThickness(1);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"0 sec", "30 sec", "60 sec"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

    }

    public Bitmap runBarSeries (Bitmap croppedIB){
        //for each pixel in the image, extract the red, green and blue values
        int i = 0, h = croppedIB.getHeight(), w = croppedIB.getWidth();

        int redTotal = 0, blueTotal = 0, greenTotal = 0;

        //extract color values into appropriate datapoint arrays
        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int pixel = croppedIB.getPixel(x,y);
                redTotal += Color.red(pixel);
                greenTotal += Color.green(pixel);
                blueTotal += Color.blue(pixel);
            }
        }

        DataPoint[] redDataPoints = new DataPoint[1];
        redDataPoints[0] = new DataPoint(1,redTotal);

        DataPoint[] greenDataPoints = new DataPoint[1];
        greenDataPoints[0] = new DataPoint(1.5,greenTotal);

        DataPoint[] blueDataPoints = new DataPoint[1];
        blueDataPoints[0] = new DataPoint(2,blueTotal);

        //create line series for color
        BarGraphSeries<DataPoint> reds = new BarGraphSeries<>(redDataPoints);
        BarGraphSeries<DataPoint> greens = new BarGraphSeries<>(greenDataPoints);
        BarGraphSeries<DataPoint> blues = new BarGraphSeries<>(blueDataPoints);

        //add bars to the graph
        graph.removeAllSeries();
        graph.addSeries(reds);
        graph.addSeries(greens);
        graph.addSeries(blues);
        reds.setSpacing(0);
        reds.setColor(Color.RED);
        greens.setColor(Color.GREEN);
        greens.setSpacing(0);
        blues.setColor(Color.BLUE);
        blues.setSpacing(0);

        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"red", "green", "blue"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);

        // get the bitmap
        Bitmap barBitmap = graph.takeSnapshot();

        // graph.takeSnapshotAndShare(this, "barGraph", "barGraphResults");

        return barBitmap;
    }

    public void openEmailActivity(View v) {
        Intent intent = new Intent(this, EmailActivity.class);
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z").format(new Date());

        intent.putExtra("results", "Your results from " + timeStamp + " are below.\n" + resultTextView.getText().toString());
        intent.putExtra(Activity.ACTIVITY_SERVICE, this.getClass().getSimpleName());
        startActivity(intent);
    }

    public void openOldTestsActivity(View v) {
        Intent intent = new Intent(this, OldTestsActivity.class);
        startActivity(intent);
    }

    private File getOutputMediaFile(int type){

        //create an internal directory to store photos
        File mediaStorageDir = new File(getFilesDir(),"TCAImages");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("TCAImages", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "bitmap" + ".png");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;

    }

}
