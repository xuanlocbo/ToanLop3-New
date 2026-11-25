package vn.mocnhien.toan3;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;

/** Optional measurement examples; no progress or reward writes. */
final class Part03LearningUi {
    private Part03LearningUi() {}
    static void addGuide(NativeUi ui,LinearLayout parent,int lesson){
        if(!Part03Tutor.applies(lesson))return;
        LinearLayout shell=ui.card(NativeUi.LAV);ui.add(parent,shell);
        ui.label(shell,"Tập đo và đọc số đo",22,true);
        ui.caption(shell,"Ví dụ bổ sung • Không tính sao • Hình minh họa không thay cho dụng cụ đo thật.");
        LinearLayout content=ui.column();content.setVisibility(View.GONE);
        Button toggle=ui.button("Mở hướng dẫn Bài "+lesson,NativeUi.PINK,()->{});
        ui.add(shell,toggle);ui.add(shell,content);
        toggle.setOnClickListener(w->{boolean open=content.getVisibility()!=View.VISIBLE;
            content.setVisibility(open?View.VISIBLE:View.GONE);
            toggle.setText(open?"Đóng hướng dẫn":"Mở hướng dẫn Bài "+lesson);});
        for(Part03Tutor.Guide g:Part03Tutor.guides(lesson)){
            LinearLayout c=ui.card(NativeUi.WHITE);ui.add(content,c);
            ui.label(c,g.name,23,true);ui.label(c,g.rule,19,false);ui.label(c,g.example,22,true);
            if(!g.visual.isEmpty())try{
                JSONObject v=new JSONObject(g.visual);
                if(MathDiagram.supports(v.optString("type"))){
                    View diagram=new MathDiagram(c.getContext(),v,new JSONObject());
                    c.addView(diagram,new LinearLayout.LayoutParams(-1,ui.dp(188)));
                }
            }catch(org.json.JSONException e){throw new IllegalStateException("Invalid supplementary illustration",e);}
            LinearLayout revealed=ui.column();ui.add(c,revealed);
            int[] n={0};Button next=ui.button("Xem bước 1",NativeUi.PINK,()->{});ui.add(c,next);
            next.setOnClickListener(w->{
                if(n[0]>=g.steps.length){revealed.removeAllViews();n[0]=0;next.setText("Xem bước 1");return;}
                ui.label(revealed,g.steps[n[0]],20,true);n[0]++;
                next.setText(n[0]==g.steps.length?"Xem lại từ đầu":"Xem bước "+(n[0]+1));
            });
        }
    }
}