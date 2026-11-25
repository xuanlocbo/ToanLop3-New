package vn.mocnhien.toan3;
import android.view.View;
import android.widget.*;
import org.json.JSONObject;

/** Optional review examples; use exact approved geometry model, no score/state writes. */
final class Part05LearningUi {
    private Part05LearningUi(){}
    static boolean geometry(NativeUi ui,LinearLayout parent,JSONObject q){
        if(!Part05Tutor.applies(q))return false;
        JSONObject v=Json.obj(q,"visual");if(!Part05GeometryView.supports(v))return false;
        View diagram=new Part05GeometryView(parent.getContext(),v);
        parent.addView(diagram,new LinearLayout.LayoutParams(-1,ui.dp(230)));
        return true;
    }
    static void addGuide(NativeUi ui,LinearLayout parent,int lesson){
        if(!Part05Tutor.applies(lesson))return;
        LinearLayout shell=ui.card(NativeUi.LAV);ui.add(parent,shell);ui.label(shell,"Ôn tập cuối học kỳ",22,true);
        ui.caption(shell,"Ví dụ bổ sung • Không tính sao");
        LinearLayout body=ui.column();body.setVisibility(View.GONE);
        Button toggle=ui.button("Mở hướng dẫn Bài "+lesson,NativeUi.PINK,()->{});
        ui.add(shell,toggle);ui.add(shell,body);
        toggle.setOnClickListener(w->{boolean open=body.getVisibility()!=View.VISIBLE;body.setVisibility(open?View.VISIBLE:View.GONE);toggle.setText(open?"Đóng hướng dẫn":"Mở hướng dẫn Bài "+lesson);});
        for(Part05Tutor.Guide g:Part05Tutor.guides(lesson)){
            LinearLayout c=ui.card(NativeUi.WHITE);ui.add(body,c);ui.label(c,g.name,23,true);ui.label(c,g.rule,19,false);ui.label(c,g.example,22,true);
            if(!g.visual.isEmpty())try{
                JSONObject v=new JSONObject(g.visual);
                View model=Part05GeometryView.supports(v)?new Part05GeometryView(c.getContext(),v):new MathDiagram(c.getContext(),v,new JSONObject());
                c.addView(model,new LinearLayout.LayoutParams(-1,ui.dp(205)));
            }catch(org.json.JSONException ex){throw new IllegalStateException("Invalid supplementary review model",ex);}
            LinearLayout revealed=ui.column();ui.add(c,revealed);int[] n={0};
            Button next=ui.button("Xem bước 1",NativeUi.PINK,()->{});ui.add(c,next);
            next.setOnClickListener(w->{if(n[0]>=g.steps.length){revealed.removeAllViews();n[0]=0;next.setText("Xem bước 1");return;}
                LinearLayout step=ui.card(NativeUi.MINT);ui.add(revealed,step);ui.caption(step,"Bước "+(n[0]+1));ui.label(step,g.steps[n[0]],20,true);
                n[0]++;next.setText(n[0]==g.steps.length?"Xem lại từ đầu":"Xem bước "+(n[0]+1));});
        }
    }
}
