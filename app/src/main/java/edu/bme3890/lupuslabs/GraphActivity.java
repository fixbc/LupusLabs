package edu.bme3890.lupuslabs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
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
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class GraphActivity extends AppCompatActivity {
    //view-related variables
    ImageView imageView, cropImageView, cropImageView2;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    //file-related variables
    Bitmap imageBitmap;
    File imageFile;

    //graph-related variables
    GraphView graph;

    Bitmap lineBitmap;
    Bitmap barBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        Intent nameIntent = getIntent();
        String FILENAME = nameIntent.getStringExtra("imageName");

        imageView = findViewById(R.id.imageView);
        cropImageView = findViewById(R.id.cropAreaImageView);
        cropImageView2 = findViewById(R.id.cropAreaImageView2);
        TextView imageSize = findViewById(R.id.imageSizeTV);

        Button lineSeriesBtn = findViewById(R.id.lineSeriesButton);
        Button barGraphBtn = findViewById(R.id.barButton);
        graph = findViewById(R.id.graph);

        //assume image already exists in device
        //load image file and create bitmap
        File mediaStorageDir = new File(getFilesDir(),"TCAImages");
        imageFile = new File(mediaStorageDir, FILENAME);
        imageView.setImageURI(Uri.fromFile(imageFile));
        imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        //indicate size of image
        imageSize.setText(imageBitmap.getWidth() + " x " +
                imageBitmap.getHeight());

    }

    //function activated on button press
    public void createGraph(View v){
        //determine which button was selected
        switch (v.getId()){
            case R.id.barButton://barButton Graph
                //do something
                runBarSeries(extractCropArea());
                imageView.setVisibility(View.GONE);
                break;
            case R.id.lineSeriesButton:
                //do something
                runLineSeries(extractCropArea());
                imageView.setVisibility(View.GONE);
                break;
            default:
                //do nothing

        }
    }

    public Bitmap extractCropArea(){
        int x1, y1, x2, y2, x1_2, x2_2, y1_2, y2_2;
        x1 = 0;
        x2 = 3264;
        y1 = 1150;
        y2 = 1350;

        //show region of interest in crop window
        Bitmap croppedImageBitmap = Bitmap.createBitmap(imageBitmap,x1, y1, x2-x1, y2-y1);
        cropImageView.setImageBitmap(croppedImageBitmap);

        // define region of interest for albumin pad
        x1_2 = 990;
        x2_2 = 1190;
        y1_2 = 1150;
        y2_2 = 1350;
        Bitmap croppedImageBitmap2 = Bitmap.createBitmap(imageBitmap,x1_2, y1_2, x2_2-x1_2, y2_2-y1_2);
        cropImageView2.setImageBitmap(croppedImageBitmap2);

        return croppedImageBitmap2;
    }


    public Bitmap runLineSeries (Bitmap croppedIB){
        //for each pixel in the image, extract the red, green and blue values
        int i = 0, h = croppedIB.getHeight(), w = croppedIB.getWidth();
        int numPixels = h * w;

        DataPoint[] redPixels = new DataPoint[numPixels], greenPixels = new DataPoint[numPixels],
                bluePixels = new DataPoint[numPixels];

        //extract color values into appropriate datapoint arrays
        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int pixel = croppedIB.getPixel(x,y);
                redPixels[i] = new DataPoint(i,Color.red(pixel));
                greenPixels[i] = new DataPoint(i,Color.green(pixel));
                bluePixels[i++] = new DataPoint(i,Color.blue(pixel));
            }
        }

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

        // get the bitmap
        Bitmap lineBitmap = graph.takeSnapshot();

        return lineBitmap;
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
        intent.putExtra(Activity.ACTIVITY_SERVICE, this.getClass().getSimpleName());
        try {
            //Write file
            String filename = "bitmap.png";
            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            barBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            barBitmap.recycle();

            intent.putExtra("barGraph", filename);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void openOldTestsActivity(View v) {
        Intent intent = new Intent(this, OldTestsActivity.class);
        intent.putExtra("barBitmap", getOutputMediaFile(MEDIA_TYPE_IMAGE).getName());
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
