package vn.mocnhien.toan3;

import android.graphics.Typeface;
import android.view.Gravity;
import android.widget.*;
import org.json.JSONObject;
import org.json.JSONArray;

/** Small, accessible native illustrations and optional unscored mini-lessons. */
final class Part02LearningUi {
    private Part02LearningUi() {}
    private static void tile(NativeUi ui, LinearLayout row, String text, int color, float weight) {
        TextView v=ui.text(text,19,true);
        v.setGravity(Gravity.CENTER);
        v.setMinHeight(ui.dp(48));
        v.setBackground(ui.outlined(color,12,NativeUi.OUTLINE));
        ui.weight(row,v,weight);
    }
    private static void flow(NativeUi ui,LinearLayout parent,String a,String op,String b) {
        LinearLayout row=ui.row();ui.add(parent,row);
        tile(ui,row,a,NativeUi.MINT,1);
        tile(ui,row,op,NativeUi.LAV,1.3f);
        tile(ui,row,b,NativeUi.GOLD,1);
    }
    private static void segments(NativeUi ui,LinearLayout parent,String label,int count,String text,int color) {
        LinearLayout row=ui.row();ui.add(parent,row);tile(ui,row,label,NativeUi.WHITE,1.5f);
        for(int i=0;i<count;i++)tile(ui,row,text,color,1);
    }
    private static void guideDiagram(NativeUi ui,LinearLayout p,String kind) {
        switch(kind){
            case "division":flow(ui,p,"Số bị chia","Chia → nhân\ntrừ → hạ","Thương");break;
            case "groups":segments(ui,p,"Mỗi bàn",4,"1",NativeUi.PALE);ui.caption(p,"Mỗi ô là một chỗ. Còn bạn chưa có chỗ thì cần thêm bàn.");break;
            case "reduce":segments(ui,p,"24",3,"?",NativeUi.PALE);ui.caption(p,"Ba phần bằng nhau; tìm một phần.");break;
            case "compare":flow(ui,p,"18","Giảm 3 lần\n: 3","?");flow(ui,p,"18","Bớt 3 đơn vị\n− 3","?");break;
            case "chain":flow(ui,p,"32",": 4","?");flow(ui,p,"?","× 3","?");break;
            case "more":segments(ui,p,"An",1,"6",NativeUi.MINT);segments(ui,p,"Bình",2,"?",NativeUi.PALE);ui.caption(p,"Bình nhiều hơn An 3 nhãn. Hỏi tổng.");break;
            case "multiple":segments(ui,p,"Giỏ A",1,"4",NativeUi.MINT);segments(ui,p,"Giỏ B",3,"4",NativeUi.PALE);ui.caption(p,"Ba phần bằng nhau, mỗi phần 4 quả.");break;
            case "less":flow(ui,p,"Lan: 12","Ít hơn 5","Mai: ?");break;
            case "tens":flow(ui,p,"70","Tách 50 + 20","?");break;
            case "chain29":flow(ui,p,"12","× 4","?");flow(ui,p,"?"," : 6","?");break;
            case "cut":segments(ui,p,"Mỗi đoạn",1,"4 m",NativeUi.MINT);ui.caption(p,"Dây dài 26 m; chỉ đếm những đoạn đủ 4 m.");break;
            default:break;
        }
    }
    static void addGuide(NativeUi ui, LinearLayout parent,int lesson) {
        if(!Part02Tutor.applies(lesson))return;
        LinearLayout guide=ui.card(NativeUi.LAV);ui.add(parent,guide);
        ui.label(guide,"Hiểu bài trước khi luyện",22,true);
        ui.caption(guide,"Ví dụ bổ sung • Không tính sao, không thay đổi tiến độ SGK/VBT.");
        LinearLayout content=ui.column();content.setVisibility(android.view.View.GONE);
        Button show=ui.button("Mở hướng dẫn Bài "+lesson,NativeUi.PINK,()->{});
        ui.add(guide,show);ui.add(guide,content);
        show.setOnClickListener(w->{
            boolean open=content.getVisibility()!=android.view.View.VISIBLE;
            content.setVisibility(open?android.view.View.VISIBLE:android.view.View.GONE);
            show.setText(open?"Đóng hướng dẫn":"Mở hướng dẫn Bài "+lesson);
        });
        for(Part02Tutor.Guide g:Part02Tutor.guides(lesson)){
            LinearLayout card=ui.card(NativeUi.WHITE);ui.add(content,card);
            ui.label(card,g.name,23,true);ui.label(card,g.rule,19,false);
            ui.label(card,g.example,23,true);guideDiagram(ui,card,g.diagram);
            LinearLayout revealed=ui.column();ui.add(card,revealed);
            final int[] i={0};
            Button next=ui.button("Xem bước 1",NativeUi.PINK,()->{});ui.add(card,next);
            next.setOnClickListener(w->{
                if(i[0]>=g.steps.length){revealed.removeAllViews();i[0]=0;next.setText("Xem bước 1");return;}
                ui.label(revealed,g.steps[i[0]],20,true);i[0]++;
                next.setText(i[0]==g.steps.length?"Xem lại từ đầu":"Xem bước "+(i[0]+1));
            });
        }
    }
    /** Render known givens only. Do not read answer, solution, workedSteps or hidden results. */
    static boolean visual(NativeUi ui, LinearLayout parent,JSONObject q) {
        if(!Part02Tutor.applies(q))return false;
        JSONObject v=q.optJSONObject("visual");if(v==null)return false;
        String t=v.optString("type");
        switch(t){
            case "number_machine":{
                LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);
                flow(ui,c,v.optString("start"),"→ "+v.optString("op","?")+" "+v.optString("factor","?")+" →",v.has("end")?v.optString("end"):"?");
                return true;
            }
            case "operation_chain":{
                if(v.has("chain"))return false; // existing text preserves given endpoints and options.
                JSONArray ops=v.optJSONArray("operations");if(ops==null)return false;
                LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);
                for(int i=0;i<ops.length();i++){
                    JSONObject op=ops.optJSONObject(i);if(op==null)continue;
                    boolean active=i==v.optInt("activeStep",-1);
                    if(active)ui.caption(c,"Bước "+(i+1)+" đang làm");
                    flow(ui,c,i==0?v.optString("start","?"):"?","→ "+op.optString("op","?")+" "+(op.isNull("value")?"?":op.optString("value"))+" →",i==ops.length()-1&&v.has("end")?v.optString("end"):"?");
                }
                return true;
            }
            case "equal_groups": {
                int count=v.optInt("groups",0);if(count<2||count>8||!v.has("total"))return false;
                LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);
                ui.label(c,"Tổng: "+v.optString("total")+"  •  "+count+" nhóm bằng nhau",20,true);
                LinearLayout row=ui.row();ui.add(c,row);
                for(int i=0;i<count;i++)tile(ui,row,"?",NativeUi.MINT,1);
                return true;
            }
            case "long_division":{
                // Only for examples and exercises; actual calculation rows are still entered below.
                LinearLayout c=ui.card(NativeUi.LAV);ui.add(parent,c);
                flow(ui,c,v.optString("dividend"),": "+v.optString("divisor"),"?");
                ui.caption(c,"Chia → nhân → trừ → hạ");return true;
            }
            default:return false;
        }
    }
}