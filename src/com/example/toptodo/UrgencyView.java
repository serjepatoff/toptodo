package com.example.toptodo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class UrgencyView extends View {
  private Paint paint = new Paint();
  private int intensity = 0;

  public UrgencyView(Context context, AttributeSet attrs) {
    super(context, attrs);

    paint.setAntiAlias(true);
    paint.setStrokeWidth(6f);
    paint.setColor(Color.BLACK);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeJoin(Paint.Join.ROUND);
  }

  @Override
  protected void onDraw(Canvas canvas) {
	  int r = intensity<128 ? 2*intensity : 255;
	  int g = intensity<128 ? 255 : 2*(255 - intensity);
	  canvas.drawARGB(255, r, g, 0	);
  }
  
  public void setUrgency( int urgency ) {
	  this.intensity = urgency*255/100;
	  if (intensity<0) intensity=0;
	  if (intensity>255) intensity=255;
	  postInvalidate();
  }
} 