package vn.mocnhien.toan3;

import android.content.Context;
import org.json.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/** The generated, versioned asset is the single runtime curriculum. */
public final class Curriculum {
    public final LinkedHashMap<String,JSONObject> questions=new LinkedHashMap<>();
    public final LinkedHashMap<Integer,JSONObject> lessons=new LinkedHashMap<>();
    public final Map<String,Integer> rewards=new LinkedHashMap<>();
    public Curriculum(Context context) throws Exception {
        this(readAsset(context));
    }
    static JSONObject readAsset(Context c) throws Exception {
        try(InputStream in=c.getAssets().open("runtime_v2.json");
            ByteArrayOutputStream bytes=new ByteArrayOutputStream()){
            byte[] buf=new byte[8192];int n;
            while((n=in.read(buf))!=-1)bytes.write(buf,0,n);
            return new JSONObject(new String(bytes.toByteArray(),StandardCharsets.UTF_8));
        }
    }
    public Curriculum(JSONObject root) {
        if(root.optInt("schemaVersion")!=2||root.optInt("recordCount")!=959)
            throw new IllegalStateException("Phiên bản dữ liệu không đúng.");
        int sgk=0,vbt=0,bonus=0;
        for(Object o:Json.list(root.optJSONArray("lessons"))){
            JSONObject l=(JSONObject)o;int id=l.optInt("lesson");
            if(id<26||id>44||lessons.put(id,l)!=null)throw new IllegalStateException("Bài học trùng");
            for(String book:new String[]{"sgk","vbt","bonus"}){
                JSONArray list=l.optJSONArray(book);
                if(list==null||list.length()==0||("bonus".equals(book)&&list.length()!=2))
                    throw new IllegalStateException("Thiếu nội dung "+id);
                for(Object item:Json.list(list)){
                    JSONObject q=(JSONObject)item;String key=q.optString("id");
                    if(key.isEmpty()||q.optInt("lesson")!=id||!book.equalsIgnoreCase(q.optString("book"))
                        ||questions.put(key,q)!=null||!NativeGrader.supports(q.optString("kind")))
                        throw new IllegalStateException("Mục không hợp lệ: "+key);
                    rewards.put(key,Json.obj(q,"reward").optInt("coins"));
                    if(book.equals("sgk"))sgk++;else if(book.equals("vbt"))vbt++;else bonus++;
                }
            }
        }
        if(questions.size()!=959||lessons.size()!=19||sgk!=437||vbt!=484||bonus!=38)
            throw new IllegalStateException("Ngân hàng không đầy đủ.");
        for(JSONObject q:questions.values())for(Object k:Json.list(q.optJSONArray("prerequisites")))
            if(!questions.containsKey(String.valueOf(k)))throw new IllegalStateException("Thiếu bước trước");
    }
    public List<JSONObject> section(int lesson,String book){
        List<JSONObject> out=new ArrayList<>();
        JSONObject l=lessons.get(lesson);
        if(l!=null)for(Object o:Json.list(l.optJSONArray(book.toLowerCase(Locale.ROOT))))out.add((JSONObject)o);
        return out;
    }
    public List<JSONObject> lesson(int lesson){
        List<JSONObject> out=new ArrayList<>();
        for(String book:new String[]{"sgk","vbt","bonus"})out.addAll(section(lesson,book));
        return out;
    }
}
