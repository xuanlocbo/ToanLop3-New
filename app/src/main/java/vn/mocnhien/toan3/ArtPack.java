package vn.mocnhien.toan3;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;

/** Native bitmaps compiled from the standalone step-1 archive. Visuals never own game or math state. */
final class ArtPack {
    private static JSONObject manifest;
    private static final LruCache<String, Bitmap> cache=new LruCache<String,Bitmap>(24*1024*1024){
        @Override protected int sizeOf(String key,Bitmap value){return value.getByteCount();}
    };
    private static final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.FILTER_BITMAP_FLAG);
    private ArtPack(){}
    private static synchronized JSONObject manifest(Context ctx){
        if(manifest==null){
            try(InputStream in=ctx.getAssets().open("visuals/manifest.json")){
                ByteArrayOutputStream bytes=new ByteArrayOutputStream();byte[] buf=new byte[8192];int n;
                while((n=in.read(buf))!=-1)bytes.write(buf,0,n);
                manifest=new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
                if(manifest.optInt("runtimeCount")!=206||manifest.optInt("questionCount")!=959)throw new IllegalStateException("Unexpected visual manifest");
            }catch(Exception e){android.util.Log.e("MocNhien","Cannot read art manifest",e);manifest=new JSONObject();}
        }
        return manifest;
    }
    static Bitmap bitmap(Context ctx,String id){
        if(id==null||id.isEmpty())return null;
        Bitmap b=cache.get(id);if(b!=null)return b;
        JSONObject entry=Json.obj(Json.obj(manifest(ctx),"images"),id);
        String path=entry.optString("path");
        if(path.isEmpty())return null;
        try(InputStream in=ctx.getAssets().open(path)){
            b=BitmapFactory.decodeStream(in);
            if(b!=null)cache.put(id,b);
            return b;
        }catch(Exception e){android.util.Log.w("MocNhien","Missing art "+id,e);return null;}
    }
    static boolean draw(Context ctx,Canvas c,String id,float x,float y,float w,float h){
        Bitmap b=bitmap(ctx,id);if(b==null||w<=0||h<=0)return false;
        c.drawBitmap(b,null,new RectF(x,y,x+w,y+h),paint);return true;
    }
    static boolean fit(Context ctx,Canvas c,String id,float x,float y,float w,float h){
        Bitmap b=bitmap(ctx,id);if(b==null||w<=0||h<=0)return false;
        float s=Math.min(w/b.getWidth(),h/b.getHeight()),ww=b.getWidth()*s,hh=b.getHeight()*s;
        c.drawBitmap(b,null,new RectF(x+(w-ww)/2,y+(h-hh)/2,x+(w+ww)/2,y+(h+hh)/2),paint);
        return true;
    }
    static ImageView image(Context ctx,String id,int heightPx){
        ImageView v=new ImageView(ctx);v.setImageBitmap(bitmap(ctx,id));
        v.setScaleType(ImageView.ScaleType.FIT_CENTER);
        v.setImportantForAccessibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO);
        v.setLayoutParams(new LinearLayout.LayoutParams(-1,heightPx));
        return v;
    }
    static Drawable icon(Context ctx,String id,int size){
        Bitmap b=bitmap(ctx,id);if(b==null)return null;
        BitmapDrawable d=new BitmapDrawable(ctx.getResources(),b);d.setFilterBitmap(true);
        d.setBounds(0,0,size,size);return d;
    }
    static String theme(int n){return new String[]{"bg_forest","bg_snow","bg_crystal","bg_pink"}[Math.max(0,Math.min(3,n))];}
    private static String normalize(String s){
        return Normalizer.normalize(s==null?"":s,Normalizer.Form.NFC).toLowerCase(Locale.ROOT).trim().replaceAll("\\s+"," ");
    }
    static String object(Context ctx,String label){
        String n=normalize(label);if(n.isEmpty())return "";
        if(n.matches("[0-9]+([.,][0-9]+)?\\s*(kg|g)"))return "weight";
        JSONObject a=Json.obj(manifest(ctx),"aliases");
        String id=a.optString(n);if(!id.isEmpty())return id;
        for(String prefix:new String[]{"quả ","con ","gói ","bao ","chiếc ","cái ","quyển ","cuốn "})
            if(n.startsWith(prefix)){id=a.optString(n.substring(prefix.length()));if(!id.isEmpty())return id;}
        // Explicit object names followed by a given mass. Never infer the mass.
        String stripped=n.replaceFirst("\\s+[0-9]+([.,][0-9]+)?\\s*(kg|g)$","");
        if(!stripped.equals(n))return a.optString(stripped);
        return "";
    }
    static void question(NativeUi ui,LinearLayout parent,JSONObject q){
        JSONArray a=Json.obj(manifest(ui.c),"decorations").optJSONArray(q.optString("id"));
        if(a==null||a.length()==0)return;
        LinearLayout card=ui.card(NativeUi.WHITE);ui.add(parent,card);
        ui.caption(card,"Minh họa đồ vật • không biểu thị số lượng");
        LinearLayout row=ui.row();ui.add(card,row);
        for(int i=0;i<a.length();i++){
            JSONObject item=a.optJSONObject(i);if(item==null)continue;
            String id=item.optString("id");if(bitmap(ui.c,id)==null)continue;
            LinearLayout cell=ui.column();ui.weight(row,cell,1);
            cell.addView(image(ui.c,id,ui.dp(ui.compact()?38:56)));
            android.widget.TextView caption=ui.text(item.optString("label"),14,false);
            caption.setGravity(android.view.Gravity.CENTER);
            cell.addView(caption,new LinearLayout.LayoutParams(-1,-2));
        }
    }
}
