package king.toby.partybooth;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.util.ArrayList;

public class PhotoCanvas extends View {

    private final Paint paint;
    private final Bitmap[] parts;
    private final String internalDir;

    public static final ArrayList<String> poses = new ArrayList<String>();

    public PhotoCanvas(Context context, String internalDir, String p1, String p2, String p3, String p4) {
        super(context);

        poses.add(p1);
        poses.add(p2);
        poses.add(p3);
        poses.add(p4);

        this.paint = new Paint();
        this.parts = new Bitmap[4];
        this.internalDir = internalDir;
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(cm);
        this.paint.setColorFilter(filter);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        for (int i = 0; i <= 3; i++) {
            String path = String.format("%s/%s", internalDir, "image" + i + ".jpg");

            Bitmap loadedImage = BitmapFactory.decodeFile(path);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(loadedImage, loadedImage.getWidth() / 3, loadedImage.getHeight() / 3, false);

            this.parts[i] = applyFilter(scaledBitmap);

            int width = parts[0].getWidth();
            int height = parts[0].getHeight();

            int paddingTop;
            int paddingLeft;

            if(i % 2 == 0){
                paddingLeft = 0;
            } else {
                paddingLeft = 10;
            }

            if(i > 1){
                paddingTop = 10;
            } else {
                paddingTop = 0;
            }

            int leftPos = width * (i % 2) + paddingLeft;
            int topPos = height * (i / 2) + paddingTop;

            String text = poses.get(i);

            paint.setColor(Color.WHITE);
            paint.setTextSize(40);

            Rect textBounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), textBounds);
            int textWidth = textBounds.width();

            canvas.drawBitmap(parts[i], leftPos, topPos, paint);
            canvas.drawText(text, leftPos + width / 2 - textWidth / 2, topPos + height - 20, paint);

            super.onDraw(canvas);
        }
    }

    public Bitmap applyFilter(Bitmap colourBitmap) {
        final Bitmap blackWhiteBitmap = Bitmap.createBitmap(colourBitmap.getWidth(), colourBitmap.getHeight(), Bitmap.Config.RGB_565);
        final Canvas c = new Canvas(blackWhiteBitmap);
        c.drawBitmap(colourBitmap, 0, 0, paint);
        return blackWhiteBitmap;
    }

}