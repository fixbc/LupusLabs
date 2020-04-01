package edu.bme3890.lupuslabs;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;

public class GraphActivity extends AppCompatActivity {
    //view-related variables
    EditText x1ET, y1ET, x2ET, y2ET;
    ImageView imageView, cropImageView;

    //file-related variables
    private String FILENAME = "example.png";
    Bitmap imageBitmap;
    File imageFile;

    //graph-related variables
    GraphView graph;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);


        //assign the views
        x1ET = (EditText) findViewById(R.id.x1ET);
        y1ET = (EditText) findViewById(R.id.y1ET);
        x2ET = (EditText) findViewById(R.id.x2ET);
        y2ET = (EditText) findViewById(R.id.y2ET);

        imageView = (ImageView) findViewById(R.id.imageView);
        cropImageView = (ImageView) findViewById(R.id.cropAreaImageView);
        TextView imageSize = (TextView) findViewById(R.id.imageSizeTV);

        Button lineSeriesBtn = (Button) findViewById(R.id.lineSeriesButton);
        Button barGraphBtn = (Button) findViewById(R.id.barButton);
        graph = (GraphView) findViewById(R.id.graph);

        //assume image already exists in device
        //load image file and create bitmap
        imageFile = new File(this.getFilesDir(), FILENAME);
        imageView.setImageURI(Uri.fromFile(imageFile));
        imageBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath());

        //indicate size of image
        imageSize.setText(String.valueOf(imageBitmap.getWidth()) + " x " +
                String.valueOf(imageBitmap.getHeight()));

        //sample line graph code from GraphView documentation
        /*
        GraphView graph = (GraphView) findViewById(R.id.graph);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        graph.addSeries(series);
        */

        //sample BarGraph code from GraphView documentation
        /*BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, 3000),
                new DataPoint(1, 4000),
                new DataPoint(2, 5000),
                new DataPoint(3, 6000),
                new DataPoint(4, 8000)
        });
        graph.addSeries(series);*/
    }

    //function activated on button press
    public void createGraph(View v){
        //determine which button was selected
        switch (v.getId()){
            case R.id.barButton://barButton Graph
                //do something
                runBarSeries(extractCropArea());
                break;
            case R.id.lineSeriesButton:
                //do something
                runLineSeries(extractCropArea());
                break;
            default:
                //do nothing

        }
    }

    public Bitmap extractCropArea(){
        int x1, y1, x2, y2;
        x1 = Integer.decode(x1ET.getText().toString());
        x2 = Integer.decode(x2ET.getText().toString());
        y1 = Integer.decode(y1ET.getText().toString());
        y2 = Integer.decode(y2ET.getText().toString());

        //show region of interest in crop window
        Bitmap croppedImageBitmap = Bitmap.createBitmap(imageBitmap,x1, y1, x2-x1, y2-y1);
        cropImageView.setImageBitmap(croppedImageBitmap);
        return croppedImageBitmap;
    }


    void runLineSeries (Bitmap croppedIB){
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
                bluePixels[i++] = new DataPoint(i,Color.blue(pixel));;
            }
        }

        //create line series for each color
        LineGraphSeries<DataPoint> redSeries = new LineGraphSeries<>(redPixels);
        LineGraphSeries<DataPoint> greenSeries = new LineGraphSeries<>(greenPixels);
        LineGraphSeries<DataPoint> blueSeries = new LineGraphSeries<>(bluePixels);

        //add line series to the graph
        graph.removeAllSeries(); //clears the graph of previous lines
        graph.addSeries(redSeries);
        graph.addSeries(greenSeries);
        graph.addSeries(blueSeries);
    }

    void runBarSeries (Bitmap croppedIB){
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

        DataPoint[] rgbDataPoints = new DataPoint[3];
        rgbDataPoints[0] = new DataPoint(1,redTotal);
        rgbDataPoints[1] = new DataPoint(2,greenTotal);
        rgbDataPoints[2] = new DataPoint(3,blueTotal);

        //create line series for color
        BarGraphSeries<DataPoint> colors = new BarGraphSeries<>(rgbDataPoints);

        //add bars to the graph
        graph.removeAllSeries();
        graph.addSeries(colors);
    }

}
