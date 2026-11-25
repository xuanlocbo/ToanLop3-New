package vn.mocnhien.toan3;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.*;
import android.text.*;
import org.json.*;
import java.util.*;

/** One native question screen; every edit and manipulation commits its draft by stable ID. */
final class QuestionPanel extends LinearLayout {
    interface Host { void changed(); void next(); void feedback(boolean correct); }
    final NativeUi ui;final JSONObject q,v;final String id,kind;final ProgressStore store;final Host host;
    final int adventureLesson,adventureGate;
    JSONObject draft;
    boolean rebuilding,celebrateNext;
    QuestionPanel(Context c,JSONObject question,ProgressStore store,Host host){
        this(c,question,store,host,0,-1);
    }
    QuestionPanel(Context c,JSONObject question,ProgressStore store,Host host,int adventureLesson,int adventureGate){
        super(c);setOrientation(VERTICAL);ui=new NativeUi(c);this.q=question;v=Json.obj(q,"visual");
        id=q.optString("id");kind=q.optString("kind");this.store=store;this.host=host;
        this.adventureLesson=adventureLesson;this.adventureGate=adventureGate;
        if(inAdventure()&&!id.equals(AdventurePlan.questionId(adventureLesson,adventureGate)))throw new IllegalArgumentException("Wrong gate question");
        draft=readDraft();render();
    }
    boolean inAdventure(){return adventureLesson!=0;}
    JSONObject readDraft(){return inAdventure()?store.adventureDraft(adventureLesson,adventureGate):store.draft(id);}
    boolean writeDraft(JSONObject d){return inAdventure()?store.saveAdventureDraft(adventureLesson,adventureGate,d):store.saveDraft(id,d);}
    boolean resetAnswer(){return inAdventure()?writeDraft(new JSONObject()):store.resetDraft(id);}
    void warn(){Toast.makeText(getContext(),"Chưa lưu được. Hãy thử lại.",Toast.LENGTH_LONG).show();}
    boolean persist(){
        if(writeDraft(draft)){host.changed();return true;}
        draft=readDraft();warn();return false;
    }
    void set(String k,Object value,boolean redraw){
        Json.put(draft,k,value);if("retry".equals(draft.optString("status")))draft.remove("status");
        if(persist()&&redraw)render();
    }
    void clear(){if(resetAnswer()){draft=readDraft();host.changed();render();}else warn();}
    void submit(){
        ProgressStore.Result r=inAdventure()?store.submitAdventure(adventureLesson,adventureGate,draft):store.submit(id,draft);
        if(!r.saved){draft=readDraft();warn();return;}
        draft=readDraft();host.changed();host.feedback(r.correct);celebrateNext=r.correct;render();
    }
    void field(LinearLayout p,String label,String value,boolean number,java.util.function.Consumer<String> action){
        ui.label(p,label,17,true);EditText e=ui.input(label,value,number);ui.add(p,e);
        e.addTextChangedListener(new TextWatcher(){public void beforeTextChanged(CharSequence s,int start,int count,int after){}
            public void onTextChanged(CharSequence s,int start,int before,int count){if(!rebuilding)action.accept(s.toString());}
            public void afterTextChanged(Editable e){}});
    }
    void field(LinearLayout p,String label,String key,boolean numeric){field(p,label,draft.optString(key,""),numeric,x->set(key,x,false));}
    void arrayField(LinearLayout p,String label,String key,int index,boolean numeric){
        field(p,label,Json.str(Json.arr(draft,key).opt(index)),numeric,x->arraySet(key,index,x,false));
    }
    void arraySet(String key,int index,Object value,boolean redraw){
        List<Object> a=Json.list(Json.arr(draft,key));while(a.size()<=index)a.add("");
        a.set(index,value);set(key,Json.array(a),redraw);
    }
    void pick(String title,JSONArray choices,java.util.function.Consumer<Object> use){
        List<Object> a=Json.list(choices);String[] labels=a.stream().map(Json::str).toArray(String[]::new);
        new AlertDialog.Builder(getContext()).setTitle(title).setItems(labels,(dialog,which)->use.accept(a.get(which))).setNegativeButton("Đóng",null).show();
    }
    void button(LinearLayout p,String s,int color,Runnable a){ui.action(p,s,color,a);}
    void selectRow(LinearLayout p,String name,JSONArray options,java.util.function.Consumer<Object> set,Object current){
        ui.label(p,name,18,true);LinearLayout line=ui.row();ui.add(p,line);
        int n=options.length();for(int i=0;i<n;i++){Object value=options.opt(i);ui.smallAction(line,Json.str(value),Json.same(current,value)?NativeUi.MINT:NativeUi.PALE,()->set.accept(value));}
    }
    void toggle(String key,Object value){List<Object> a=Json.list(Json.arr(draft,key));int found=-1;for(int i=0;i<a.size();i++)if(Json.same(a.get(i),value)){found=i;break;}if(found>=0)a.remove(found);else a.add(value);set(key,Json.array(a),true);}
    void append(String key,Object value){List<Object>a=Json.list(Json.arr(draft,key));a.add(value);set(key,Json.array(a),true);}
    void removeLast(String key){List<Object>a=Json.list(Json.arr(draft,key));if(!a.isEmpty())a.remove(a.size()-1);set(key,Json.array(a),true);}
    void drawing(LinearLayout p,View diagram,int height){LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(-1,ui.dp(height));lp.setMargins(0,ui.dp(5),0,ui.dp(5));p.addView(diagram,lp);}
    String source(){String book=q.optString("book");return book.equals("BONUS")?"Thử thách kho báu • Câu "+q.optInt("exercise"):book+" • Trang "+q.optInt("page")+" • "+(book.equals("VBT")?"Tiết "+q.optInt("period")+" • ":"")+"Bài tập "+q.optInt("exercise")+" • "+q.optString("part");}
    void render(){
        rebuilding=true;removeAllViews();ui.caption(this,source());ui.label(this,q.optString("prompt"),25,true);
        boolean correct="correct".equals(draft.optString("status"));
        if(correct){
            LinearLayout win=ui.card(NativeUi.MINT);ui.add(this,win);
            if(!ui.compact()&&!kind.equals("study")&&!kind.startsWith("practice"))ui.banner(win,AdventurePlan.theme(q.optInt("lesson",26)),true,82);
            ui.label(win,kind.equals("study")?"Đã học xong!":kind.startsWith("practice")?"Đã ghi nhận thực hành!":"Giỏi lắm! Em làm đúng rồi!",26,true);
            int earned=draft.optInt("earned");
            TextView award=ui.badge(earned>0?"★ +"+earned+" sao hồng":"✓ Đã lưu kết quả",earned>0?NativeUi.GOLD:NativeUi.WHITE);ui.add(win,award);
            ui.label(win,Part04Tutor.solution(q),20,false);LinearLayout row=ui.row();ui.add(win,row);
            if(!inAdventure())ui.smallAction(row,"Làm lại",NativeUi.PALE,this::clear);ui.smallAction(row,inAdventure()?"Qua cổng →":"Tiếp tục →",NativeUi.GREEN,host::next);
            if(celebrateNext){win.setAlpha(.65f);win.setTranslationY(ui.dp(6));win.animate().alpha(1f).translationY(0).setDuration(230).start();win.post(()->row.requestRectangleOnScreen(new android.graphics.Rect(0,0,row.getWidth(),row.getHeight()),false));}
            celebrateNext=false;rebuilding=false;return;
        }
        if(kind.equals("board_game")){
            BoardState b=BoardState.replay(q,draft);
            Json.put(draft,"boardPosition",b.landing>=0?b.landing:b.position);
        }
        String type=v.optString("type");
        if(MathDiagram.supports(type)&&!(kind.startsWith("practice")&&type.equals("ruler_object")))
            drawing(this,new MathDiagram(getContext(),v,draft),type.equals("dice_board")?300:206);
        if(!Part02LearningUi.visual(ui,this,q)&&!Part04LearningUi.visual(ui,this,q))visualText();
        if(kind.equals("study")){
            LinearLayout c=ui.card(NativeUi.LAV);ui.add(this,c);ui.label(c,"Cùng tìm hiểu",21,true);Part04LearningUi.setup(ui,c,q);ui.label(c,Part04Tutor.solution(q),21,false);
            button(this,"Em đã hiểu",NativeUi.PINK,()->{set("read",true,false);submit();});rebuilding=false;return;
        }
        if(!store.unlocked(q)){ui.label(this,"Hãy hoàn thành bước trước để mở nhiệm vụ này.",20,true);rebuilding=false;return;}
        LinearLayout content=ui.card(NativeUi.WHITE);ui.add(this,content);
        switch(kind){
            case "number":case "ruler":case "balance":case "scale":case "cup":case "thermometer":
                if(type.equals("matching_lines"))matchingEndpoint(content);
                else field(content,"Đáp án", "value",true);break;
            case "multiplication":written(content,false);field(content,"Tích","value",true);break;
            case "division":case "division_story":
                if(type.equals("long_division"))written(content,true);
                field(content,kind.equals("division_story")?"Số bộ quần áo":"Thương","quotient",true);
                field(content,kind.equals("division_story")?"Số mét vải thừa":"Số dư (ghi 0 nếu chia hết)","remainder",true);break;
            case "steps":case "compose_story":steps(content);break;
            case "practice":case "practice_measure":case "practice_compose_story":practice(content);break;
            case "choice":case "match_value":options(content,false);if(Json.obj(q,"grading").optBoolean("requiresReason"))field(content,"Vì sao?","reason",false);break;
            case "multi":case "segment_multi":options(content,true);break;
            case "point_select":pointSelect(content);break;
            case "route_match":route(content);break;
            case "match":match(content);break;
            case "order":order(content);break;
            case "digit_fill":digits(content);break;
            case "number_fill":fill(content);break;
            case "operator_fill":operators(content);break;
            case "pour":water(content);break;
            case "balance_select":weights(content);break;
            case "draw":draw(content);break;
            case "angle_multi":angle(content);break;
            case "board_game":board(content);break;
            default:throw new IllegalStateException("Unimplemented "+kind);
        }
        if("retry".equals(draft.optString("status"))){LinearLayout tryAgain=ui.card(0xffffedda);ui.add(this,tryAgain);ui.label(tryAgain,"Chưa đúng, mình thử thêm một lần nhé!",21,true);tryAgain.post(()->tryAgain.requestRectangleOnScreen(new android.graphics.Rect(0,0,tryAgain.getWidth(),tryAgain.getHeight()),false));}
        if(draft.optBoolean("showHint"))ui.label(this,"Gợi ý: "+Part04Tutor.hint(q),19,false);
        if(draft.optBoolean("showSolution"))ui.label(this,"Cách giải: "+Part04Tutor.solution(q),19,false);
        LinearLayout controls=ui.row();ui.add(this,controls);
        ui.smallAction(controls,"Gợi ý",NativeUi.LAV,()->set("showHint",!draft.optBoolean("showHint"),true));
        ui.smallAction(controls,"Làm lại",NativeUi.PALE,this::clear);
        if(!kind.equals("board_game"))ui.smallAction(controls,"Kiểm tra",NativeUi.PINK,this::submit);
        if(draft.optBoolean("submitted")&&!kind.equals("board_game"))button(this,"Xem lời giải",NativeUi.LAV,()->set("showSolution",!draft.optBoolean("showSolution"),true));
        rebuilding=false;
    }
    void visualText(){
        switch(v.optString("type")){
            case "table":table(Json.arr(v,"headers"),Json.arr(v,"rows"));break;
            case "expression_compare":for(Object o:Json.list(v.optJSONArray("expressions"))){JSONObject t=(JSONObject)o;ui.label(this,t.optString("id")+"   "+t.optString("expression"),20,true);}break;
            case "worked_expression":ui.label(this,v.optString("expression")+" = "+v.optString("intermediate")+" = "+v.optString("shown"),21,true);break;
            case "worked_division":{
                JSONObject t=Json.obj(v,"shown");if(v.has("a")||v.has("dividend"))ui.label(this,v.optInt("a",v.optInt("dividend"))+" : "+v.optInt("b",v.optInt("divisor"))+" = "+t.optInt("quotient")+(t.optInt("remainder")>0?" dư "+t.optInt("remainder"):""),23,true);
                JSONArray rr=Json.arr(v,"writtenRows");if(rr.length()>0)ui.label(this,"Các dòng tính trong mẫu: "+android.text.TextUtils.join("  •  ",Json.list(rr)),18,false);break;}
            case "worked_multiplication":ui.label(this,v.optInt("a")+" × "+v.optInt("b")+" = "+v.optInt("shown"),23,true);break;
            case "number_machine":ui.label(this,Json.str(v.opt("start"))+" → "+v.optString("op","?")+" "+v.optInt("factor")+" → "+(v.has("end")?Json.str(v.opt("end")):"□"),22,true);break;
            case "operation_chain":{
                if(v.has("chain")){ui.label(this,android.text.TextUtils.join("  →  ",Json.list(v.optJSONArray("chain"))),22,true);break;}
                StringBuilder s=new StringBuilder(v.isNull("start")?"□":v.optString("start","□"));int i=0;
                for(Object o:Json.list(v.optJSONArray("operations"))){JSONObject t=(JSONObject)o;s.append("  → ").append(t.optString("op")).append(" ").append(t.isNull("value")?"□":t.optString("value")).append(" → ").append(i==Json.arr(v,"operations").length()-1&&v.has("end")?v.optString("end"):"□");i++;}
                ui.label(this,s.toString(),20,true);break;}
            case "ratio_table":case "ratio_difference_table":{
                JSONArray p=Json.arr(v,"pairs");StringBuilder s=new StringBuilder();for(Object o:Json.list(p)){JSONArray x=(JSONArray)o;s.append(x.optInt(0)).append(" và ").append(x.optInt(1)).append("     ");}ui.label(this,s.toString().trim(),19,false);break;}
            case "number_machine_table":ui.label(this,"Số ban đầu: "+android.text.TextUtils.join("    ",Json.list(Json.arr(v,"inputs"))),19,true);break;
            case "temperature_reading_table":{JSONObject ex=Json.obj(v,"example");ui.label(this,"Mẫu: "+ex.optInt("valueC")+" °C — "+ex.optString("reading"),19,false);break;}
            case "ordered_expressions":for(Object e:Json.list(v.optJSONArray("expressions")))ui.label(this,Json.str(e),21,true);break;
            case "matching_lines":{for(Object o:Json.list(v.optJSONArray("lines"))){JSONObject a=(JSONObject)o;ui.label(this,a.optInt("start")+" "+a.optString("unit")+"  →  "+a.optString("op")+" "+a.optInt("factor")+"  →  "+(a.optBoolean("example")?a.optInt("end"):"□"),18,false);}break;}
            case "vertical_digit_fill":ui.label(this,v.optString("multiplicand").replace("?","□")+" × "+v.optString("multiplier").replace("?","□")+" = "+v.optString("product").replace("?","□"),28,true);break;
            case "number_fill":case "operator_fill":ui.label(this,v.optString("template").replace("?","□"),26,true);break;
            default:break; // Other contexts are already given in the source-derived prompt.
        }
    }
    void table(JSONArray headers,JSONArray rows){
        LinearLayout card=ui.card(NativeUi.LAV);ui.add(this,card);tableRow(card,headers,true);
        for(Object o:Json.list(rows))if(o instanceof JSONArray)tableRow(card,(JSONArray)o,false);
    }
    void tableRow(LinearLayout p,JSONArray cells,boolean bold){LinearLayout row=ui.row();ui.add(p,row);for(Object x:Json.list(cells)){TextView t=ui.text(x==JSONObject.NULL?"□":Json.str(x),18,bold);t.setGravity(android.view.Gravity.CENTER);ui.weight(row,t,1);}}
    void written(LinearLayout p,boolean div){
        JSONArray ss=Json.arr(v,"steps");if(ss.length()==0)return;
        ui.label(p,div?"Đặt tính và chia lần lượt từ trái sang phải":"Đặt tính và nhân từ hàng đơn vị",20,true);
        if(!Part04LearningUi.setup(ui,p,q))ui.label(p,div?v.optInt("dividend")+"  │ "+v.optInt("divisor"):v.optInt("multiplicand")+"\n×  "+v.optInt("multiplier")+"\n────────",26,true);
        for(int i=0;i<ss.length();i++){ui.label(p,"Lượt "+(i+1),18,true);LinearLayout line=ui.row();ui.add(p,line);
            String[] keys=div?new String[]{"partialDividend","quotientDigit","product","remainder"}:new String[]{"product","resultDigit","carryOut"};
            String[] labels=div?new String[]{"Số đang chia","Chữ số thương","Tích","Còn dư"}:new String[]{"Kết quả nhân","Viết","Nhớ (0 nếu không)"};
            for(int j=0;j<keys.length;j++){LinearLayout box=ui.column();ui.weight(line,box,1);field(box,labels[j],"work"+i+"_"+keys[j],true);}
        }
    }
    void steps(LinearLayout p){
        if(kind.equals("compose_story")){field(p,"Bài toán em tự nêu","story",false);adult(p);}
        JSONArray work=Json.arr(q,"workedSteps");
        for(int i=0;i<work.length();i++){
            LinearLayout row=ui.row();ui.add(p,row);LinearLayout a=ui.column(),b=ui.column();ui.weight(row,a,1);ui.weight(row,b,1);
            field(a,"Số thứ nhất","s"+i+"a",true);field(b,"Số thứ hai","s"+i+"b",true);final String key="s"+i+"op";
            selectRow(p,"Dấu phép tính bước "+(i+1),new JSONArray(Arrays.asList("+","−","×",":")),x->set(key,x,true),draft.opt(key));
        }
        field(p,"Kết quả bước 1","intermediate",true);field(p,"Kết quả cuối cùng","final",true);
    }
    void adult(LinearLayout p){CheckBox cb=new CheckBox(getContext());cb.setText("Người lớn đã xem và xác nhận");cb.setTextSize(18);cb.setChecked(draft.optBoolean("adult"));ui.add(p,cb);cb.setOnCheckedChangeListener((b,on)->set("adult",on,false));}
    void practice(LinearLayout p){
        ui.label(p,"Thực hành cùng người lớn. Không có đáp án cố định và không tự chấm sao.",18,false);
        if(kind.equals("practice_measure"))ui.label(p,"Dùng sách in thật và thước; kích thước trên màn hình không phải số đo thực.",18,false);
        JSONArray fields=v.optJSONArray("fields");if(fields==null)fields=v.optJSONArray("columns");
        if(fields==null)field(p,"Kết quả hoặc ghi chú","note",false);else for(Object o:Json.list(fields)){
            String key=Json.str(o);field(p,key,Json.obj(draft,"notes").optString(key),false,x->{JSONObject notes=Json.obj(draft,"notes");Json.put(notes,key,x);set("notes",notes,false);});
        }
        adult(p);
    }
    void options(LinearLayout p,boolean multi){
        JSONArray choices=Json.arr(v,"options");
        ui.label(p,multi?"Chạm chọn tất cả đáp án đúng":"Chọn một đáp án",18,true);
        for(Object x:Json.list(choices)){
            boolean checked=multi?Json.contains(Json.arr(draft,"values"),x):Json.same(draft.opt("value"),x);
            button(p,(checked?"✓  ":"")+Json.str(x),checked?NativeUi.MINT:NativeUi.PALE,()->{if(multi)toggle("values",x);else set("value",x,true);});
        }
    }
    void pointSelect(LinearLayout p){JSONArray opts=Json.array(Json.keys(Json.obj(v,"points")));for(Object x:Json.list(opts))button(p,(Json.same(draft.opt("value"),x)?"✓  ":"")+"Điểm "+Json.str(x),Json.same(draft.opt("value"),x)?NativeUi.MINT:NativeUi.PALE,()->set("value",x,true));}
    void route(LinearLayout p){
        ui.label(p,"Thẻ "+v.optInt("card")+" • Chọn cầu",20,true);
        for(Object o:Json.list(v.optJSONArray("bridges"))){JSONObject t=(JSONObject)o;String k=t.optString("id");button(p,"Cầu "+k+"  "+t.optString("op")+" "+t.optInt("value"),Json.same(draft.opt("bridge"),k)?NativeUi.MINT:NativeUi.PALE,()->set("bridge",k,true));}
        selectRow(p,"Chọn giỏ",Json.arr(v,"baskets"),x->set("basket",x,true),draft.opt("basket"));
    }
    void match(LinearLayout p){
        JSONArray opts=Json.arr(v,"options");LinkedHashMap<String,String> items=new LinkedHashMap<>();
        if(v.has("expressions"))for(Object o:Json.list(v.optJSONArray("expressions"))){JSONObject t=(JSONObject)o;items.put(t.optString("id"),t.optString("expression"));}
        else if(v.has("scenes")){for(Object o:Json.list(v.optJSONArray("scenes")))items.put(Json.str(o),Json.str(o));List<Object> ids=new ArrayList<>();for(Object o:Json.list(v.optJSONArray("thermometers")))ids.add(((JSONObject)o).optString("id"));opts=Json.array(ids);}
        else for(Object o:Json.list(v.optJSONArray("objects")))items.put(Json.str(o),Json.str(o));
        final JSONArray choices=opts;
        for(Map.Entry<String,String> item:items.entrySet()){
            String k=item.getKey();Object selected=Json.obj(draft,"mapping").opt(k);
            button(p,item.getValue()+"  →  "+(selected==null?"Chọn":Json.str(selected)),selected==null?NativeUi.PALE:NativeUi.MINT,()->pick(item.getValue(),choices,x->{JSONObject m=Json.obj(draft,"mapping");Json.put(m,k,x);set("mapping",m,true);}));
        }
    }
    void matchingEndpoint(LinearLayout p){
        List<Object> opts=new ArrayList<>();for(Object o:Json.list(v.optJSONArray("lines"))){JSONObject t=(JSONObject)o;int b=t.optInt("factor",1);int e=t.has("end")?t.optInt("end"):t.optString("op").equals("×")?t.optInt("start")*b:t.optInt("start")/Math.max(1,b);if(!opts.contains(e))opts.add(e);}
        Collections.shuffle(opts,new Random(id.hashCode()));selectRow(p,"Chọn kết quả để nối",Json.array(opts),x->set("value",x,true),draft.opt("value"));
    }
    void order(LinearLayout p){
        JSONArray items=Json.arr(v,"items");
        if(items.length()>0){
            ui.label(p,"Chạm các số theo thứ tự đề yêu cầu.",18,true);ui.label(p,"Đã chọn: "+android.text.TextUtils.join(" → ",Json.list(Json.arr(draft,"values"))),21,true);
            for(Object o:Json.list(items)){JSONObject t=(JSONObject)o;int x=t.optInt("value");button(p,t.optString("name")+"  "+x+" "+v.optString("unit"),Json.contains(Json.arr(draft,"values"),x)?NativeUi.MINT:NativeUi.PALE,()->{if(!Json.contains(Json.arr(draft,"values"),x))append("values",x);});}
            button(p,"Bỏ số cuối",NativeUi.LAV,()->removeLast("values"));
        }else for(int i=0;i<Json.arr(v,"expressions").length();i++)arrayField(p,"Vị trí "+(i+1),"values",i,true);
    }
    void digits(LinearLayout p){
        String t=v.optString("multiplicand")+v.optString("multiplier")+v.optString("product");int count=0;for(char ch:t.toCharArray())if(ch=='?')count++;
        JSONArray options=Json.arr(v,"digitOptions");for(int i=0;i<count;i++){final int k=i;Object x=Json.arr(draft,"values").opt(i);button(p,"Ô "+(i+1)+"   "+(Json.str(x).isEmpty()?"□":Json.str(x)),Json.str(x).isEmpty()?NativeUi.PALE:NativeUi.MINT,()->pick("Chọn chữ số cho ô "+(k+1),options,y->arraySet("values",k,y,true)));}
    }
    void fill(LinearLayout p){int n=0;for(char ch:v.optString("template").toCharArray())if(ch=='?')n++;for(int i=0;i<n;i++)arrayField(p,"Ô số "+(i+1),"values",i,true);}
    void operators(LinearLayout p){
        int n=0;for(char ch:v.optString("template").toCharArray())if(ch=='?')n++;
        int rows=v.optBoolean("requireAll")?2:1;JSONArray opts=Json.arr(v,"operators");
        for(int r=0;r<rows;r++){ui.label(p,"Cách "+(r+1),18,true);for(int i=0;i<n;i++){final int row=r,col=i;JSONArray seq=rows==1?Json.arr(draft,"values"):Json.arr(draft,"sequences").optJSONArray(r);Object curr=seq==null?null:seq.opt(i);
            selectRow(p,"Dấu ở ô "+(i+1),opts,x->{if(rows==1)arraySet("values",col,x,true);else{List<Object> seqs=Json.list(Json.arr(draft,"sequences"));while(seqs.size()<=row)seqs.add(new JSONArray());JSONArray s=(JSONArray)seqs.get(row);List<Object> vals=Json.list(s);while(vals.size()<=col)vals.add("");vals.set(col,x);seqs.set(row,Json.array(vals));set("sequences",Json.array(seqs),true);}},curr);}
        }
    }
    void water(LinearLayout p){
        int[] s=NativeGrader.water(q,draft);if(s==null){ui.label(p,"Trạng thái không hợp lệ, hãy làm lại.",18,true);return;}
        ui.label(p,"A: "+s[0]+" ml     B: "+s[1]+" ml",24,true);
        for(int i=0;i<2;i++){final int cup=i;LinearLayout row=ui.row();ui.add(p,row);String name=i==0?"A":"B";
            ui.smallAction(row,"Đầy "+name,NativeUi.PALE,()->waterAction("fill",cup,-1));
            ui.smallAction(row,"Đổ hết "+name,NativeUi.PALE,()->waterAction("empty",cup,-1));
            ui.smallAction(row,"Rót "+name+" → "+(i==0?"B":"A"),NativeUi.MINT,()->waterAction("pour",cup,1-cup));}
        button(p,"Quay lại thao tác trước",NativeUi.LAV,()->removeLast("actions"));
    }
    void waterAction(String type,int cup,int to){JSONObject a=new JSONObject();Json.put(a,"type",type);if(type.equals("pour")){Json.put(a,"from",cup);Json.put(a,"to",to);}else Json.put(a,"cup",cup);if(Json.arr(draft,"actions").length()<1000)append("actions",a);}
    void weights(LinearLayout p){
        if(v.has("availableWeightsKg")){
            JSONArray avail=Json.arr(v,"availableWeightsKg");ui.label(p,"Đặt từng quả cân lên đĩa trái hoặc phải.",19,true);
            for(int i=0;i<avail.length();i++){final int k=i;String w=avail.optInt(i)+" kg";LinearLayout row=ui.row();ui.add(p,row);ui.smallAction(row,w+" • Trái",Json.contains(Json.arr(draft,"left"),i)?NativeUi.MINT:NativeUi.PALE,()->placeWeight(k,"left"));ui.smallAction(row,w+" • Phải",Json.contains(Json.arr(draft,"right"),i)?NativeUi.MINT:NativeUi.PALE,()->placeWeight(k,"right"));}
            selectRow(p,"Đĩa đặt gạo",new JSONArray(Arrays.asList("Trái","Phải")),x->set("riceSide",x.equals("Trái")?"left":"right",true),draft.optString("riceSide").equals("left")?"Trái":draft.optString("riceSide").equals("right")?"Phải":"");return;
        }
        JSONArray a=Json.arr(v,"availableWeightsG");ui.label(p,"Chọn từng quả cân. Các quả cùng khối lượng là những quả riêng.",19,true);
        for(int i=0;i<a.length();i++){final int k=i;boolean selected=Json.contains(Json.arr(draft,"selected"),i);button(p,(selected?"✓  ":"")+a.optInt(i)+" g",selected?NativeUi.MINT:NativeUi.PALE,()->{draft.remove("weighings");toggle("selected",k);});}
        if(v.has("weighingsAllowed")){
            button(p,draft.optInt("weighings")==1?"Đã cân một lần ✓":"Thực hiện một lần cân",NativeUi.LAV,()->set("weighings",1,true));
            selectRow(p,"Lấy phần gạo nào?",new JSONArray(Arrays.asList("Đã cân","Còn lại")),x->set("keep",x.equals("Đã cân")?"weighed":"remaining",true),draft.optString("keep").equals("weighed")?"Đã cân":draft.optString("keep").equals("remaining")?"Còn lại":"");
        }
    }
    void placeWeight(int i,String side){List<Object> left=Json.list(Json.arr(draft,"left")),right=Json.list(Json.arr(draft,"right"));left.removeIf(x->Json.same(x,i));right.removeIf(x->Json.same(x,i));(side.equals("left")?left:right).add(i);Json.put(draft,"left",Json.array(left));set("right",Json.array(right),true);}
    void draw(LinearLayout p){
        ui.label(p,"Hình mẫu",19,true);drawing(p,new GridCanvas(getContext(),Json.arr(v,"templateVertices"),true,null),190);
        ui.label(p,"Em chạm lần lượt các giao điểm. Chạm lại điểm đầu để khép hình.",19,true);
        drawing(p,new GridCanvas(getContext(),Json.arr(draft,"vertices"),draft.optBoolean("closed"),(x,y)->{
            if(draft.optBoolean("closed"))return;
            JSONArray a=Json.arr(draft,"vertices");JSONArray vertex=new JSONArray(Arrays.asList(x,y));
            if(a.length()>=3&&Json.same(a.opt(0),vertex)){set("closed",true,true);return;}
            if(!Json.contains(a,vertex)&&a.length()<12)append("vertices",vertex);
        }),210);
        LinearLayout row=ui.row();ui.add(p,row);ui.smallAction(row,"Bỏ điểm cuối",NativeUi.LAV,()->{draft.remove("closed");removeLast("vertices");});ui.smallAction(row,"Xóa hình",NativeUi.PALE,()->{draft.remove("closed");set("vertices",new JSONArray(),true);});
    }
    void angle(LinearLayout p){
        ui.label(p,"Chọn đỉnh trước, rồi chọn hai điểm trên hai cạnh của góc.",18,true);
        JSONArray pending=Json.arr(draft,"pendingAngle");ui.label(p,"Đang chọn: "+android.text.TextUtils.join(" • ",Json.list(pending)),20,true);
        JSONArray angles=Json.arr(draft,"angles");for(Object o:Json.list(angles)){JSONObject a=(JSONObject)o;JSONArray arms=Json.arr(a,"arms");ui.label(p,"✓ Góc "+arms.optString(0)+a.optString("vertex")+arms.optString(1),19,true);}
        LinearLayout row=ui.row();ui.add(p,row);int i=0;for(String k:Json.keys(Json.obj(v,"points"))){
            if(i>0&&i%5==0){row=ui.row();ui.add(p,row);}i++;ui.smallAction(row,k,Json.contains(pending,k)?NativeUi.MINT:NativeUi.PALE,()->anglePoint(k));}
        button(p,"Bỏ góc cuối",NativeUi.LAV,()->removeLast("angles"));button(p,"Xóa điểm đang chọn",NativeUi.PALE,()->set("pendingAngle",new JSONArray(),true));
    }
    void anglePoint(String point){List<Object>a=Json.list(Json.arr(draft,"pendingAngle"));if(a.contains(point))return;a.add(point);if(a.size()==3){JSONObject t=new JSONObject();Json.put(t,"vertex",a.get(0));Json.put(t,"arms",Json.array(a.subList(1,3)));Json.put(draft,"pendingAngle",new JSONArray());append("angles",t);}else set("pendingAngle",Json.array(a),true);}
    void board(LinearLayout p){
        BoardState b=BoardState.replay(q,draft);if(!b.valid){ui.label(p,"Lượt chơi không hợp lệ. Chọn Làm lại.",20,true);return;}
        if(b.finished){button(p,"Nhận kết quả",NativeUi.PINK,this::submit);return;}
        ui.label(p,"Ô "+b.position+" • Lượt "+b.turns+" • Xúc xắc "+(b.die==0?"—":b.die),21,true);
        if(b.landing<0){
            if(draft.has("boardMessage"))ui.label(p,draft.optString("boardMessage"),19,false);
            button(p,"Gieo xúc xắc",NativeUi.PINK,()->{JSONObject e=new JSONObject();Json.put(e,"die",1+new Random().nextInt(6));draft.remove("boardMessage");append("turns",e);if(BoardState.finished(q,draft))submit();});
        }else{
            JSONObject t=b.tile;ui.label(p,"Ô "+b.landing+" : "+t.optInt("a")+" "+t.optString("o")+" "+t.optInt("b")+" = ? "+t.optString("u"),24,true);
            field(p,"Kết quả","boardEntry",true);
            button(p,"Trả lời lượt này",NativeUi.PINK,()->{
                if(Json.integer(draft.opt("boardEntry"),Integer.MIN_VALUE)==Integer.MIN_VALUE)return;
                JSONArray events=Json.arr(draft,"turns");JSONObject last=events.optJSONObject(events.length()-1);Json.put(last,"value",draft.opt("boardEntry"));
                boolean correct=Json.same(t.opt("z"),draft.opt("boardEntry"));draft.remove("boardEntry");Json.put(draft,"boardMessage",correct?"Đúng rồi, tiến đến ô "+b.landing+"!":"Chưa đúng, trở về ô trước lượt gieo.");
                if(persist()){host.feedback(correct);render();}
            });
        }
    }
}
