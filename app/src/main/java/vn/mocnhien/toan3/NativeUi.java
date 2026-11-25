package vn.mocnhien.toan3;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import java.util.HashMap;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import android.text.InputType;

/** Shared visual system; all callbacks and learning state remain in their original owners. */
final class NativeUi {
    static final int INK=0xff632784,PINK=0xffd51970,WHITE=0xffffffff,PALE=0xffffe1f0,BG=0xffffe9f3,
        GREEN=0xff267650,MINT=0xffe1f6da,GOLD=0xfffff1c9,LAV=0xfff0dffa,
        BLUE=0xff146fb5,ORANGE=0xffb85513,VIOLET=0xff7735b3,SKY=0xffddf3ff,ROSE=0xffffb9d7;
    static final int OUTLINE=0xfff0c6dc, MUTED=0xff725781;
    final Context c;
    final Typeface normal=Typeface.create("sans-serif",Typeface.NORMAL);
    final Typeface bold=Typeface.create("sans-serif",Typeface.BOLD);
    private static final HashMap<String,Bitmap> ART=new HashMap<>();
    NativeUi(Context c){this.c=c;}
    int dp(float n){return (int)(c.getResources().getDisplayMetrics().density*n+.5f);}
    int widthDp(){return c.getResources().getConfiguration().screenWidthDp;}
    int heightDp(){return c.getResources().getConfiguration().screenHeightDp;}
    boolean compact(){return heightDp()<450;}
    boolean sideBySide(){return widthDp()>=520&&widthDp()>heightDp();}
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
        boolean strong=color==PINK||color==GREEN||color==BLUE||color==ORANGE||color==VIOLET;
        b.setTextColor(strong?WHITE:INK);b.setTypeface(bold);b.setGravity(Gravity.CENTER);b.setBackgroundTintList(null);
        GradientDrawable base=strong?wash(color,color==PINK?0xfff13687:color,19):outlined(color,19,OUTLINE);
        b.setBackground(new RippleDrawable(ColorStateList.valueOf(strong?0x30ffffff:0x22b93e7a),base,bg(0xffffffff,16)));
        b.setStateListAnimator(null);b.setMinWidth(0);b.setMinimumWidth(0);b.setMinHeight(dp(compact()?46:50));
        b.setPadding(dp(10),dp(8),dp(10),dp(8));b.setSoundEffectsEnabled(false);
        b.setOnClickListener(v->click.run());return b;
    }
    void add(LinearLayout p,View v){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,-2);lp.setMargins(0,dp(4),0,dp(4));p.addView(v,lp);}
    void weight(LinearLayout p,View v,float w){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(0,-2,w);lp.setMargins(dp(4),dp(3),dp(4),dp(3));p.addView(v,lp);}
    void action(LinearLayout p,String text,int color,Runnable action){add(p,button(text,color,action));}
    void smallAction(LinearLayout p,String text,int color,Runnable action){weight(p,button(text,color,action),1);}
    LinearLayout card(int color){
        LinearLayout card=column();card.setBackground(outlined(color,23,color==WHITE?OUTLINE:0xfff8d7e7));
        card.setElevation(dp(compact()?1:2));card.setPadding(dp(compact()?8:12),dp(compact()?6:10),dp(compact()?8:12),dp(compact()?6:10));return card;
    }
    // Original artwork is used only as decoration; all controls and values are native views.
    ImageView art(String name,int height){
        ImageView image=new ImageView(c);
        Bitmap bitmap=ART.get(name);
        if(bitmap==null){
            try(java.io.InputStream in=c.getAssets().open("ui/"+name+".webp")){
                bitmap=BitmapFactory.decodeStream(in);
                if(bitmap!=null)ART.put(name,bitmap);
            }catch(java.io.IOException e){android.util.Log.w("MocNhien","Missing UI art: "+name,e);}
        }
        image.setImageBitmap(bitmap);
        if(image.getDrawable() instanceof android.graphics.drawable.BitmapDrawable)
            ((android.graphics.drawable.BitmapDrawable)image.getDrawable()).setFilterBitmap(true);
        image.setScaleType(ImageView.ScaleType.FIT_CENTER);
        image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        image.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(height)));
        return image;
    }
    ImageView packArt(String id,int height){return ArtPack.image(c,id,dp(height));}
    void stageBanner(LinearLayout parent,int lesson,int height){
        ImageView pic=packArt("stage_"+lesson,height);
        if(pic.getDrawable()==null){banner(parent,AdventurePlan.theme(lesson),false,height);return;}
        pic.setScaleType(ImageView.ScaleType.FIT_CENTER);
        pic.setBackground(bg(PALE,18));pic.setClipToOutline(true);
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(height));lp.setMargins(0,dp(4),0,dp(6));
        parent.addView(pic,lp);
    }
    void hero(LinearLayout p){
        int h=compact()?70:Math.min(250,Math.min(Math.round((widthDp()-20)*.56f),Math.max(110,(heightDp()-370)/2)));
        ImageView image=art("hero",h);
        image.setBackground(wash(0xfffff5fa,0xffffc9e3,24));
        image.setClipToOutline(true);
        image.setContentDescription("Mộc Nhiên Toán 3 — Học vui mỗi ngày");
        image.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_YES);
        p.addView(image);
    }
    ProgressBar meter(int current,int total,int color){
        ProgressBar bar=new ProgressBar(c,null,android.R.attr.progressBarStyleHorizontal);
        bar.setMax(Math.max(1,total));
        android.graphics.drawable.ClipDrawable fill=new android.graphics.drawable.ClipDrawable(bg(color,8),Gravity.LEFT,android.graphics.drawable.ClipDrawable.HORIZONTAL);
        android.graphics.drawable.LayerDrawable layers=new android.graphics.drawable.LayerDrawable(new Drawable[]{bg(0xfff6d5e6,8),fill});
        layers.setId(0,android.R.id.background);layers.setId(1,android.R.id.progress);
        bar.setProgressDrawable(layers);
        bar.setProgress(Math.max(0,Math.min(current,total)));
        return bar;
    }
    void meter(LinearLayout p,int current,int total,int color){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(12));lp.setMargins(dp(3),dp(5),dp(3),dp(4));p.addView(meter(current,total,color),lp);
    }
    void tile(LinearLayout row,String artName,String label,int color,int tint,Runnable click){
        LinearLayout box=card(tint);box.setGravity(Gravity.CENTER_HORIZONTAL);weight(row,box,1);
        box.setPadding(dp(7),dp(5),dp(7),dp(5));
        box.addView(art(artName,compact()?36:Math.min(100,Math.max(46,(heightDp()-510)/2))));
        box.setBackground(wash(tint,artName.equals("book")?0xffffacd5:artName.equals("practice")?0xffd8b3f5:artName.equals("game")?0xffb9e99c:0xffffdc82,23));
        box.setOnClickListener(v->click.run());
        if(android.os.Build.VERSION.SDK_INT>=28){box.setOutlineAmbientShadowColor(0xffe3a7c7);box.setOutlineSpotShadowColor(0xffe3a7c7);}
        Button b=button(label,color,click);b.setTextSize(compact()?16:18);
        b.setTextColor(color);b.setBackground(new RippleDrawable(ColorStateList.valueOf(0x22ffffff),bg(0x00ffffff,16),bg(WHITE,16)));
        b.setMinHeight(dp(compact()?42:46));b.setMinimumHeight(0);b.setPadding(dp(3),dp(5),dp(3),dp(5));
        box.addView(b,new LinearLayout.LayoutParams(-1,-2));
    }
    void navigation(LinearLayout row,String label,int kind,boolean selected,Runnable click){
        Button b=button(label,selected?PALE:WHITE,click);b.setTextColor(selected?PINK:MUTED);
        b.setContentDescription(label);b.setTextSize(12);b.setMinHeight(dp(54));b.setMinimumHeight(0);
        b.setPadding(dp(1),dp(4),dp(1),dp(4));b.setBackground(bg(selected?PALE:WHITE,16));
        String id=new String[]{"icon_home","icon_progress","icon_trophy","icon_settings"}[Math.max(0,Math.min(3,kind))];
        Drawable icon=ArtPack.icon(c,id,dp(23));
        if(icon==null){icon=new NavGlyph(kind,selected?PINK:MUTED);icon.setBounds(0,0,dp(23),dp(23));}
        b.setCompoundDrawables(null,icon,null,null);
        b.setCompoundDrawablePadding(dp(2));weight(row,b,1);
    }
    private static final class NavGlyph extends Drawable{
        final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);final int kind,color;
        NavGlyph(int k,int c){kind=k;color=c;}
        @Override public void draw(Canvas c){
            c.save();c.translate(getBounds().left,getBounds().top);c.scale(getBounds().width()/24f,getBounds().height()/24f);
            p.setColor(color);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(1.8f);p.setStrokeJoin(Paint.Join.ROUND);p.setStrokeCap(Paint.Cap.ROUND);
            Path path=new Path();
            switch(kind){
                case 0:
                    path.moveTo(2,11);path.lineTo(12,2.5f);path.lineTo(22,11);path.moveTo(5,9);path.lineTo(5,21);path.lineTo(10,21);path.lineTo(10,15);path.lineTo(14,15);path.lineTo(14,21);path.lineTo(19,21);path.lineTo(19,9);c.drawPath(path,p);break;
                case 1:
                    c.drawRoundRect(3,13,7,21,1,1,p);c.drawRoundRect(10,8,14,21,1,1,p);c.drawRoundRect(17,3,21,21,1,1,p);break;
                case 2:
                    c.drawRoundRect(3,8,21,21,2,2,p);c.drawLine(3,12,21,12,p);c.drawRoundRect(3,3,21,12,4,4,p);c.drawRoundRect(10,10,14,15,1,1,p);break;
                default:
                    for(int i=0;i<8;i++){double a=i*Math.PI/4;float x=(float)Math.cos(a),y=(float)Math.sin(a);c.drawLine(12+x*8,12+y*8,12+x*10.5f,12+y*10.5f,p);}
                    c.drawCircle(12,12,8,p);c.drawCircle(12,12,3,p);break;
            }c.restore();
        }
        @Override public void setAlpha(int a){p.setAlpha(a);}
        @Override public void setColorFilter(ColorFilter f){p.setColorFilter(f);}
        @Override public int getOpacity(){return PixelFormat.TRANSLUCENT;}
    }
    void banner(LinearLayout parent,int theme,boolean hero,int height){
        LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,dp(height));lp.setMargins(0,dp(4),0,dp(6));
        if(!hero){
            ImageView pic=packArt(ArtPack.theme(theme),height);
            if(pic.getDrawable()!=null){pic.setScaleType(ImageView.ScaleType.CENTER_CROP);pic.setBackground(bg(PALE,18));pic.setClipToOutline(true);parent.addView(pic,lp);return;}
        }
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
