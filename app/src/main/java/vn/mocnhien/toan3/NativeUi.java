package vn.mocnhien.toan3;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;

/** Shared visual system; all callbacks and learning state remain in their original owners. */
final class NativeUi {
    static final int INK=0xff57364d,PINK=0xffb93e7a,WHITE=0xffffffff,PALE=0xffffe4ef,BG=0xfffff4f8,
        GREEN=0xff267650,MINT=0xffdbf3e5,GOLD=0xffffe4a6,LAV=0xffeee6fc;
    static final int OUTLINE=0xffefd5e3, MUTED=0xff795c70;
    final Context c;
    final Typeface normal=Typeface.create("sans-serif",Typeface.NORMAL);
    final Typeface bold=Typeface.create("sans-serif-rounded",Typeface.BOLD);
    NativeUi(Context c){this.c=c;}
    int dp(float n){return (int)(c.getResources().getDisplayMetrics().density*n+.5f);}
    int widthDp(){return c.getResources().getConfiguration().screenWidthDp;}
    GradientDrawable bg(int color,int radius){
        GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;
    }
    GradientDrawable outlined(int color,int radius,int edge){
        GradientDrawable d=bg(color,radius);d.setStroke(dp(1),edge);return d;
    }
    GradientDrawable wash(int start,int end,int radius){
        GradientDrawable d=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{start,end});d.setCornerRadius(dp(radius));return d;
    }
    LinearLayout column(){LinearLayout x=new LinearLayout(c);x.setOrientation(LinearLayout.VERTICAL);return x;}
    LinearLayout row(){LinearLayout x=new LinearLayout(c);x.setOrientation(LinearLayout.HORIZONTAL);x.setGravity(Gravity.CENTER_VERTICAL);return x;}
    TextView text(String s,int size,boolean heavy){
        TextView t=new TextView(c);t.setText(s);t.setTextSize(size);t.setTextColor(INK);
        t.setTypeface(heavy?bold:normal);t.setLineSpacing(dp(3),1f);t.setPadding(dp(5),dp(3),dp(5),dp(3));
        return t;
    }
    void label(LinearLayout p,String s,int size,boolean heavy){p.addView(text(s,size,heavy),new LinearLayout.LayoutParams(-1,-2));}
    void caption(LinearLayout p,String s){TextView t=text(s,15,false);t.setTextColor(MUTED);p.addView(t,new LinearLayout.LayoutParams(-1,-2));}
    TextView badge(String s,int color){TextView t=text(s,17,true);t.setGravity(Gravity.CENTER);t.setBackground(outlined(color,15,OUTLINE));t.setPadding(dp(12),dp(6),dp(12),dp(6));return t;}
    Button button(String label,int color,Runnable click){
        Button b=new Button(c);b.setAllCaps(false);b.setText(label);b.setTextSize(19);
        boolean strong=color==PINK||color==GREEN;
        b.setTextColor(strong?WHITE:INK);b.setTypeface(bold);b.setGravity(Gravity.CENTER);b.setBackgroundTintList(null);
        GradientDrawable base=strong?wash(color,color==PINK?0xffa72e67:0xff226745,16):outlined(color,16,OUTLINE);
        b.setBackground(new RippleDrawable(ColorStateList.valueOf(strong?0x30ffffff:0x22b93e7a),base,bg(0xffffffff,16)));
        b.setStateListAnimator(null);b.setMinWidth(0);b.setMinimumWidth(0);b.setMinHeight(dp(50));
        b.setPadding(dp(10),dp(8),dp(10),dp(8));b.setSoundEffectsEnabled(false);
        b.setOnClickListener(v->click.run());return b;
    }
    void add(LinearLayout p,View v){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(4),0,dp(4));p.addView(v,lp);}
    void weight(LinearLayout p,View v,float w){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,w);lp.setMargins(dp(4),dp(3),dp(4),dp(3));p.addView(v,lp);}
    void action(LinearLayout p,String text,int color,Runnable action){add(p,button(text,color,action));}
    void smallAction(LinearLayout p,String text,int color,Runnable action){weight(p,button(text,color,action),1);}
    LinearLayout card(int color){
        LinearLayout card=column();card.setBackground(outlined(color,20,color==WHITE?OUTLINE:0x16b93e7a));
        card.setPadding(dp(12),dp(10),dp(12),dp(10));return card;
    }
    void banner(LinearLayout parent,int theme,boolean hero,int height){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(height));lp.setMargins(0,dp(4),0,dp(6));
        parent.addView(new StoryArt.Banner(c,theme,hero),lp);
    }
    void pad(View v,int n){v.setPadding(dp(n),dp(n),dp(n),dp(n));}
    EditText input(String label,String value,boolean numeric){
        EditText e=new EditText(c);e.setTextSize(22);e.setTextColor(INK);e.setHintTextColor(MUTED);e.setHint(label);
        e.setSingleLine(numeric);e.setInputType(numeric?InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_SIGNED:InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_MULTI_LINE|InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        e.setImeOptions(android.view.inputmethod.EditorInfo.IME_FLAG_NO_EXTRACT_UI|(numeric?android.view.inputmethod.EditorInfo.IME_ACTION_DONE:android.view.inputmethod.EditorInfo.IME_ACTION_NONE));
        e.setText(value);e.setContentDescription(label);e.setPadding(dp(12),dp(9),dp(12),dp(9));e.setMinHeight(dp(52));
        e.setBackground(outlined(0xfffff7fa,14,0xffdeb0c8));return e;
    }
}
