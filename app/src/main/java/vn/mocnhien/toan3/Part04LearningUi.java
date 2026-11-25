package vn.mocnhien.toan3;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;
import org.json.JSONArray;

/** Native place-value board, quotient scaffold and unscored guides. Only known operands are shown before solving. */
final class Part04LearningUi {
    private Part04LearningUi(){}
    private static void cell(NativeUi ui,LinearLayout row,String value,int color,int size,float weight){
        TextView t=ui.text(value,size,true);t.setGravity(Gravity.CENTER);
        t.setMinHeight(ui.dp(38));t.setPadding(ui.dp(2),ui.dp(4),ui.dp(2),ui.dp(4));
        t.setBackground(ui.outlined(color,10,NativeUi.OUTLINE));ui.weight(row,t,weight);
    }
    private static void digitRow(NativeUi ui,LinearLayout parent,String label,String value,int color){
        LinearLayout r=ui.row();ui.add(parent,r);cell(ui,r,label,NativeUi.WHITE,15,1.6f);
        String s="    "+value;String last=s.substring(s.length()-4);
        for(int i=0;i<4;i++)cell(ui,r,last.substring(i,i+1),color,25,1);
    }
    private static void multiplyBoard(NativeUi ui,LinearLayout parent,int n,int b){
        LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);ui.label(c,"Bảng đặt tính theo hàng",20,true);
        LinearLayout labels=ui.row();ui.add(c,labels);cell(ui,labels,"Hàng",NativeUi.WHITE,15,1.6f);
        for(String x:new String[]{"Nghìn","Trăm","Chục","Đơn vị"})cell(ui,labels,x,NativeUi.WHITE,15,1);
        digitRow(ui,c,"Số bị nhân",Integer.toString(n),NativeUi.PALE);
        digitRow(ui,c,"×",Integer.toString(b),NativeUi.MINT);
        digitRow(ui,c,"Tích","□□□□",NativeUi.WHITE);
        ui.caption(c,"Bắt đầu ở hàng đơn vị (bên phải), rồi chuyển sang trái.");
    }
    private static void divideBoard(NativeUi ui,LinearLayout parent,int n,int b){
        LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);ui.label(c,"Đặt tính chia",20,true);
        LinearLayout labels=ui.row();ui.add(c,labels);cell(ui,labels,"Số bị chia",NativeUi.WHITE,16,2);cell(ui,labels,"Số chia",NativeUi.WHITE,16,1);
        LinearLayout values=ui.row();ui.add(c,values);cell(ui,values,Integer.toString(n),NativeUi.PALE,28,2);cell(ui,values,Integer.toString(b),NativeUi.MINT,28,1);
        LinearLayout output=ui.row();ui.add(c,output);cell(ui,output,"Thương: ?",NativeUi.WHITE,22,2);cell(ui,output,"Dư: ?",NativeUi.WHITE,20,1);
        ui.caption(c,"Từ trái sang phải: chia → nhân → trừ → hạ. Không bỏ chữ số 0 ở thương.");
    }
    static boolean setup(NativeUi ui,LinearLayout parent,JSONObject q){
        if(!Part04Tutor.applies(q))return false;
        JSONObject v=Json.obj(q,"visual");String type=v.optString("type");
        if("long_multiplication".equals(type)){
            int a=v.optInt("multiplicand",-1),b=v.optInt("multiplier",-1);
            if(a<0||a>999||b<1||b>9)return false;multiplyBoard(ui,parent,a,b);return true;
        }
        if("long_division".equals(type)){
            int a=v.optInt("dividend",-1),b=v.optInt("divisor",-1);
            if(a<0||a>999||b<1||b>9)return false;divideBoard(ui,parent,a,b);return true;
        }
        return false;
    }
    static boolean visual(NativeUi ui,LinearLayout parent,JSONObject q){
        if(!Part04Tutor.applies(q))return false;
        JSONObject v=Json.obj(q,"visual");String type=v.optString("type");
        if("ratio_table".equals(type)||"ratio_difference_table".equals(type)){
            JSONArray pairs=Json.arr(v,"pairs");if(pairs.length()==0)return false;
            LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);
            boolean difference="ratio_difference_table".equals(type);
            ui.label(c,difference?"So sánh theo đơn vị và theo số lần":"Bảng so sánh số lần",20,true);
            LinearLayout heads=ui.row();ui.add(c,heads);
            for(String title:difference?new String[]{"Số thứ nhất","Số thứ hai","Hiệu","Gấp mấy lần"}:new String[]{"Số thứ nhất","Số thứ hai","Gấp mấy lần"})cell(ui,heads,title,NativeUi.WHITE,16,1);
            for(int i=0;i<pairs.length();i++){
                JSONArray p=pairs.optJSONArray(i);if(p==null||p.length()<2)continue;
                LinearLayout row=ui.row();ui.add(c,row);
                int color=i==v.optInt("activeColumn",-1)?NativeUi.MINT:NativeUi.PALE;
                cell(ui,row,p.optString(0),color,22,1);cell(ui,row,p.optString(1),color,22,1);
                // Source example values already appear in the study solution; never copy hidden answers into the exercise.
                if(difference)cell(ui,row,"?",NativeUi.WHITE,20,1);
                cell(ui,row,"?",NativeUi.WHITE,20,1);
            }
            return true;
        }
        if("expression".equals(type)){
            ui.caption(parent,"Thứ tự: trong ngoặc → nhân, chia → cộng, trừ. Cùng mức: từ trái sang phải.");
            return true;
        }
        return false;
    }
    static void addGuide(NativeUi ui,LinearLayout parent,int lesson){
        if(!Part04Tutor.applies(lesson))return;
        LinearLayout shell=ui.card(NativeUi.LAV);ui.add(parent,shell);
        ui.label(shell,"Tính từng bước",22,true);
        ui.caption(shell,"15 ví dụ bổ sung cho Bài 36–40 • Không tính sao");
        LinearLayout body=ui.column();body.setVisibility(View.GONE);
        Button toggle=ui.button("Mở hướng dẫn Bài "+lesson,NativeUi.PINK,()->{});
        ui.add(shell,toggle);ui.add(shell,body);
        toggle.setOnClickListener(w->{boolean open=body.getVisibility()!=View.VISIBLE;
            body.setVisibility(open?View.VISIBLE:View.GONE);toggle.setText(open?"Đóng hướng dẫn":"Mở hướng dẫn Bài "+lesson);});
        for(Part04Tutor.Guide g:Part04Tutor.guides(lesson)){
            LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.label(c,g.name,23,true);
            ui.label(c,g.rule,19,false);ui.label(c,g.example,23,true);
            if("multiply".equals(g.model))multiplyBoard(ui,c,g.a,g.b);
            else if("divide".equals(g.model))divideBoard(ui,c,g.a,g.b);
            else if("ratio".equals(g.model)){
                JSONObject v=new JSONObject();Json.put(v,"type","bar_compare");
                Json.put(v,"labels",new JSONArray(java.util.Arrays.asList("Số lớn","Số bé")));
                Json.put(v,"lengths",new JSONArray(java.util.Arrays.asList(g.a,g.b)));
                c.addView(new MathDiagram(c.getContext(),v,new JSONObject()),new LinearLayout.LayoutParams(-1,ui.dp(160)));
            }else{
                LinearLayout r=ui.row();ui.add(c,r);
                cell(ui,r,"1. Ngoặc",NativeUi.PALE,17,1);cell(ui,r,"2. Nhân, chia",NativeUi.MINT,17,1);cell(ui,r,"3. Cộng, trừ",NativeUi.GOLD,17,1);
            }
            LinearLayout revealed=ui.column();ui.add(c,revealed);
            int[] n={0};Button next=ui.button("Xem bước 1",NativeUi.PINK,()->{});ui.add(c,next);
            next.setOnClickListener(w->{
                if(n[0]>=g.steps.length){revealed.removeAllViews();n[0]=0;next.setText("Xem bước 1");return;}
                LinearLayout step=ui.card(NativeUi.MINT);ui.add(revealed,step);
                ui.caption(step,"Bước "+(n[0]+1));ui.label(step,g.steps[n[0]],20,true);
                n[0]++;next.setText(n[0]==g.steps.length?"Xem lại từ đầu":"Xem bước "+(n[0]+1));
            });
        }
    }
}
