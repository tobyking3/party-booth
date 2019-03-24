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
import android.util.Log;
import android.view.View;
import java.util.ArrayList;
import java.util.Arrays;

public class PhotoCanvas extends View {

    private final Paint paint;
    private final Bitmap[] parts;
    private final String internalDir;
    int bitmapWidth, bitmapHeight, paddingTop, paddingLeft, leftPos, topPos, textWidth;
    String poseText;

    public static final ArrayList<String> poses = new ArrayList<String>();

    public PhotoCanvas(Context context, String internalDir, String p1, String p2, String p3, String p4) {
        super(context);

        poses.addAll(Arrays.asList(p1, p2, p3, p4));

        this.paint = new Paint();
        this.parts = new Bitmap[4];
        this.internalDir = internalDir;

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
        paddingLeft = i % 2 == 0 ? 0 : 10;
        leftPos = bitmapWidth * (i % 2) + paddingLeft;
        return leftPos;
    }

    public int createYPosition(int i){
        bitmapHeight = parts[0].getHeight();
        paddingTop = i > 1 ? 10 : 0;
        topPos = bitmapHeight * (i / 2) + paddingTop;
        return topPos;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLUE);

        for (int i = 0; i <= 3; i++) {
            bitmapWidth = parts[0].getWidth();
            bitmapHeight = parts[0].getHeight();
            //Get current pose
            poseText = poses.get(i);
            //Create bounding box
            Rect textBounds = new Rect();
            //Setup text
            paint.getTextBounds(poseText, 0, poseText.length(), textBounds);
            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            textWidth = textBounds.width();

            Log.i("last x pos", String.valueOf(leftPos));
            Log.i("last y pos", String.valueOf(topPos));

            Log.i("last height", String.valueOf(bitmapHeight));
            Log.i("last width", String.valueOf(bitmapWidth));

            canvas.drawBitmap(parts[i], createXPosition(i), createYPosition(i), paint);
            canvas.drawText(poseText, createXPosition(i) + bitmapWidth / 2 - textWidth / 2, createYPosition(i) + bitmapHeight - 20, paint);
        }
    }

    public Bitmap applyFilter(Bitmap colourBitmap) {
        final Bitmap blackWhiteBitmap = Bitmap.createBitmap(colourBitmap.getWidth(), colourBitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(blackWhiteBitmap);
        c.drawBitmap(colourBitmap, 0, 0, paint);
        return blackWhiteBitmap;
    }

}