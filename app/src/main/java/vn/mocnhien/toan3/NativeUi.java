package vn.mocnhien.toan3;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.*;
import android.text.InputType;
import android.view.View;

final class NativeUi {
    static final int INK=0xff643550,PINK=0xffc94a83,WHITE=0xffffffff,PALE=0xffffe3f0,BG=0xfffff0f6,GREEN=0xff248450,MINT=0xffd2f1db,GOLD=0xffffd774,LAV=0xffeee3ff;
    final Context c;
    NativeUi(Context c){this.c=c;}
    int dp(float n){return (int)(c.getResources().getDisplayMetrics().density*n+.5f);}
    GradientDrawable bg(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
    LinearLayout column(){LinearLayout x=new LinearLayout(c);x.setOrientation(LinearLayout.VERTICAL);return x;}
    LinearLayout row(){LinearLayout x=new LinearLayout(c);x.setOrientation(LinearLayout.HORIZONTAL);x.setGravity(Gravity.CENTER_VERTICAL);return x;}
    TextView text(String s,int size,boolean bold){TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(INK);t.setTypeface(Typeface.create("sans-serif",bold?Typeface.BOLD:Typeface.NORMAL));t.setLineSpacing(dp(3),1f);t.setPadding(dp(5),dp(3),dp(5),dp(3));return t;}
    void label(LinearLayout p,String s,int size,boolean bold){p.addView(text(s,size,bold),new LinearLayout.LayoutParams(-1,-2));}
    Button button(String label,int color,Runnable click){Button b=new Button(c);b.setAllCaps(false);b.setText(label);b.setTextSize(19);b.setTextColor(color==PINK||color==GREEN?WHITE:INK);b.setTypeface(Typeface.DEFAULT,Typeface.BOLD);b.setGravity(Gravity.CENTER);b.setBackgroundTintList(null);b.setBackground(bg(color,14));b.setMinHeight(dp(48));b.setPadding(dp(12),dp(8),dp(12),dp(8));b.setOnClickListener(v->click.run());return b;}
    void add(LinearLayout p,View v){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(4),0,dp(4));p.addView(v,lp);}
    void weight(LinearLayout p,View v,float w){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,w);lp.setMargins(dp(3),dp(3),dp(3),dp(3));p.addView(v,lp);}
    void action(LinearLayout p,String text,int color,Runnable action){add(p,button(text,color,action));}
    void smallAction(LinearLayout p,String text,int color,Runnable action){weight(p,button(text,color,action),1);}
    LinearLayout card(int color){LinearLayout c=column();c.setBackground(bg(color,18));c.setPadding(dp(12),dp(9),dp(12),dp(9));return c;}
    void pad(View v,int n){v.setPadding(dp(n),dp(n),dp(n),dp(n));}
    EditText input(String label,String value,boolean numeric){EditText e=new EditText(c);e.setTextSize(21);e.setTextColor(INK);e.setHintTextColor(0xff927687);e.setHint(label);e.setSingleLine(numeric);e.setInputType(numeric?InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED:InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);e.setText(value);e.setContentDescription(label);e.setPadding(dp(12),dp(9),dp(12),dp(9));e.setMinHeight(dp(51));e.setBackground(bg(PALE,12));return e;}
}
