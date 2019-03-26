package king.toby.partybooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PhotoCanvas extends View {

    private final String TAG = this.getClass().getSimpleName();
    private final Paint paint;
    private final Bitmap[] parts;
    private static String internalDir;
    private final List<String> poses;

    static Bitmap imageToSave;

    int bitmapWidth;
    int bitmapHeight;
    int leftPos;
    int topPos;

    public PhotoCanvas(Context context, String internalDir, List<String> poses) {
        super(context);

        this.internalDir = internalDir;

        Pair<Integer, Integer> sizes = this.findXY();
        this.imageToSave = Bitmap.createBitmap(sizes.first, sizes.second, Bitmap.Config.ARGB_8888);

        this.poses = new ArrayList<>(poses);
        this.paint = new Paint();
        this.parts = new Bitmap[4];

        ColorMatrix bWMatrix = new ColorMatrix();
        bWMatrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(bWMatrix);
        this.paint.setColorFilter(filter);

        createQuad();
    }

    public void createQuad(){
        //loop through 4 saved images
        for (int i = 0; i <= 3; i++) {
            //Get image path
            String path = String.format("%s/%s", internalDir, "image" + i + ".jpg");
            //Decode a file path into a bitmap
            Bitmap loadedImage = BitmapFactory.decodeFile(path);
            //Creates a new bitmap, scaled from an existing bitmap
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, loadedImage.getWidth() / 2, loadedImage.getHeight() / 2, false);
            //Convert bitmap to black and white
            Bitmap blackAndWhiteBitmap = applyFilter(scaledBitmap);
            //Flip bitmap horizontally
            Matrix flipMatrix = new Matrix();
            flipMatrix.preScale(-1.0f, 1.0f);
            Bitmap flippedBitmap = Bitmap.createBitmap(blackAndWhiteBitmap, 0, 0, blackAndWhiteBitmap.getWidth(), blackAndWhiteBitmap.getHeight(), flipMatrix, true);
            //Add to bitmap array
            this.parts[i] = flippedBitmap;
        }
    }

    public int createXPosition(int i){
        bitmapWidth = parts[0].getWidth();
        final int paddingLeft = i % 2 == 0 ? 0 : 10;
        leftPos = bitmapWidth * (i % 2) + paddingLeft;
        return leftPos;
    }

    public int createYPosition(int i){
        bitmapHeight = parts[0].getHeight();
        final int paddingTop = i > 1 ? 10 : 0;
        topPos = bitmapHeight * (i / 2) + paddingTop;
        return topPos;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.setBitmap(imageToSave);
        canvas.drawColor(Color.BLACK);

        bitmapWidth = parts[0].getWidth();
        bitmapHeight = parts[0].getHeight();

        for (int i = 0; i <= 3; i++) {
            //Get current pose
            final String poseText = poses.get(i);

            //Create bounding box
            Rect textBounds = new Rect();

            //Setup text
            paint.getTextBounds(poseText, 0, poseText.length(), textBounds);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            final int textWidth = textBounds.width();

            canvas.drawBitmap(parts[i], createXPosition(i), createYPosition(i), paint);
            canvas.drawText(poseText, createXPosition(i) + bitmapWidth / 2 - textWidth / 2, createYPosition(i) + bitmapHeight - 20, paint);
        }

        File savedPhoto = new File(internalDir + "/finalimage.png");

        try (FileOutputStream out = new FileOutputStream(savedPhoto)) {
            imageToSave.compress(Bitmap.CompressFormat.PNG, 50, out); // bmp is your Bitmap instance
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

//    public static AsyncTask<Void, Void, String> saveFile() {
//        /// SAVE IMAGE HERE
//        File savedPhoto = new File(internalDir + "/finalimage.png");
//
//        try (FileOutputStream out = new FileOutputStream(savedPhoto)) {
//            return new AsyncTask<Void, Void, String>() {
//                @Override
//                protected String doInBackground (final Void... voids) {
//                    imageToSave.compress(Bitmap.CompressFormat.PNG, 50, out); // bmp is your Bitmap instance
//                    return "Done";
//                }
//            }.execute();
//
//        } catch (IOException e) {
//            Log.e("SAVE FAILED", "Failed to save image");
//            return null;
//        }
//    }

    public Bitmap applyFilter(Bitmap colourBitmap) {
        final Bitmap blackWhiteBitmap = Bitmap.createBitmap(colourBitmap.getWidth(), colourBitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(blackWhiteBitmap);
        c.drawBitmap(colourBitmap, 0, 0, paint);
        return blackWhiteBitmap;
    }

    private Pair<Integer, Integer> findXY () {
        String path = String.format("%s/%s", internalDir, "image" + 0 + ".jpg");
        //Decode a file path into a bitmap
        Bitmap loadedImage = BitmapFactory.decodeFile(path);
        int x = loadedImage.getWidth();
        int y = loadedImage.getHeight();

        return Pair.create(x, y);
    }

    public static Bitmap getImageToSave() {
        return imageToSave;
    }

}