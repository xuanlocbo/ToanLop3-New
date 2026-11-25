package vn.mocnhien.toan3;

import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/** Supplementary arithmetic explanations; the reviewed question bank and grader remain authoritative. */
final class Part04Tutor {
    private Part04Tutor(){}
    static boolean applies(int l){return l>=36&&l<=40;}
    static boolean applies(JSONObject q){return applies(q.optInt("lesson"))&&("SGK".equals(q.optString("book"))||"VBT".equals(q.optString("book")));}
    static final class Mul {
        final int digit,carryIn,total,written,carryOut;
        Mul(int d,int c,int n){digit=d;carryIn=c;total=n;written=n%10;carryOut=n/10;}
    }
    static final class Div {
        final int partial,quotient,product,remainder;
        Div(int n,int d){partial=n;quotient=n/d;product=quotient*d;remainder=n-product;}
    }
    static List<Mul> multiply(int n,int b){
        if(n<0||n>999||b<1||b>9)throw new IllegalArgumentException("outside lesson scope");
        List<Mul> out=new ArrayList<>();int carry=0;
        String digits=Integer.toString(n);
        for(int i=digits.length()-1;i>=0;i--){
            int d=digits.charAt(i)-'0';Mul row=new Mul(d,carry,d*b+carry);out.add(row);carry=row.carryOut;
        }
        return out;
    }
    static List<Div> divide(int n,int b){
        if(n<0||n>999||b<1||b>9)throw new IllegalArgumentException("outside lesson scope");
        List<Div> out=new ArrayList<>();int r=0;boolean started=false;String digits=Integer.toString(n);
        for(int i=0;i<digits.length();i++){
            int partial=r*10+digits.charAt(i)-'0';
            if(!started&&partial<b&&i<digits.length()-1){r=partial;continue;}
            Div row=new Div(partial,b);out.add(row);r=row.remainder;started=true;
        }
        return out;
    }
    static String mulLine(Mul row,int b,int i,int size){
        String[] names={"Đơn vị","Chục","Trăm"};
        String label=i<names.length?names[i]:"Lượt "+(i+1);
        String equation=b+" × "+row.digit+(row.carryIn>0?" + "+row.carryIn:"")+" = "+row.total;
        if(i==size-1)return label+": "+equation+"; viết "+row.total+".";
        return label+": "+equation+"; viết "+row.written+(row.carryOut>0?", nhớ "+row.carryOut:", không nhớ")+".";
    }
    static String divLine(Div row,int b,int i){
        return (i==0?"Lấy ":"Hạ chữ số tiếp theo, được ")+row.partial+": chia "+b+" được "+row.quotient+"; viết "+row.quotient+". "+row.quotient+" × "+b+" = "+row.product+"; "+row.partial+" − "+row.product+" = "+row.remainder+".";
    }
    private static String pretty(String s){return s.replace("*"," × ").replace("/"," : ").replace("+"," + ").replace("-"," − ");}
    static final class Expression {
        final int value;final List<String> lines;
        Expression(int value,List<String> lines){this.value=value;this.lines=lines;}
    }
    // Restricted elementary integer expressions only: no rounding, implicit multiplication,
    // unary operators, negative intermediate values or division by zero. Reject unsupported input.
    private static final Pattern ATOM=Pattern.compile("[0-9]+|[()+*/-]");
    private static final Pattern HIGH=Pattern.compile("([0-9]+)([*/])([0-9]+)");
    private static final Pattern LOW=Pattern.compile("([0-9]+)([+-])([0-9]+)");
    static Expression expression(String source){
        if(source==null||source.length()>150)return null;
        String cur=source.replace("−","-").replace("×","*").replace(":","/").replace("÷","/").replaceAll("\\s+","");
        boolean expectNumber=true;int depth=0,pos=0;Matcher lex=ATOM.matcher(cur);
        while(lex.find()){
            if(lex.start()!=pos)return null;pos=lex.end();String token=lex.group();
            if(Character.isDigit(token.charAt(0))){if(!expectNumber)return null;expectNumber=false;}
            else if(token.equals("(")){if(!expectNumber)return null;depth++;}
            else if(token.equals(")")){if(expectNumber||--depth<0)return null;}
            else{if(expectNumber)return null;expectNumber=true;}
        }
        if(pos!=cur.length()||expectNumber||depth!=0)return null;
        List<String> lines=new ArrayList<>();lines.add(pretty(cur));
        try{
            for(int loop=0;loop<100;loop++){
                if(cur.matches("[0-9]+"))return new Expression(Integer.parseInt(cur),lines);
                int open=cur.lastIndexOf('('),close=open<0?cur.length():cur.indexOf(')',open);
                if(close<0)return null;
                int start=open+1;String sub=cur.substring(start,close);
                if(open>=0&&sub.matches("[0-9]+")){
                    cur=cur.substring(0,open)+sub+cur.substring(close+1);
                }else{
                    Matcher m=HIGH.matcher(sub);
                    if(!m.find()){m=LOW.matcher(sub);if(!m.find())return null;}
                    long a=Long.parseLong(m.group(1)),b=Long.parseLong(m.group(3)),n;
                    if(a>1000000000L||b>1000000000L)return null;
                    switch(m.group(2)){
                        case "*":n=a*b;break;
                        case "/":if(b==0||a%b!=0)return null;n=a/b;break;
                        case "+":n=a+b;break;
                        default:n=a-b;break;
                    }
                    if(n<0||n>1000000000L)return null;
                    cur=cur.substring(0,start+m.start())+n+cur.substring(start+m.end());
                }
                lines.add(pretty(cur));
            }
        }catch(NumberFormatException|IndexOutOfBoundsException e){return null;}
        return null;
    }
    static String hint(JSONObject q){
        String old=Part03Tutor.hint(q);
        if(!applies(q))return old;
        String type=Json.obj(q,"visual").optString("type"),extra="";
        switch(type){
            case "long_multiplication":extra="Xếp số theo đúng hàng; nhân từ đơn vị sang chục, trăm. Cộng số nhớ của lượt trước vào tích của lượt đang làm.";break;
            case "long_division":extra="Chia từ trái sang phải: chia, nhân, trừ, hạ. Khi đã bắt đầu viết thương, không bỏ chữ số 0; số dư cuối phải nhỏ hơn số chia.";break;
            case "worked_division":extra="Thử lại: thương × số chia + số dư = số bị chia; số dư nhỏ hơn số chia.";break;
            case "expression":case "expression_match":case "expression_compare":case "worked_expression":case "ordered_expressions":
                extra="Trong ngoặc trước; nhân và chia trước cộng và trừ. Các phép tính cùng mức ưu tiên làm từ trái sang phải.";break;
            case "grouping_example":extra="Có thể nhóm các số hạng sao cho tổng tròn chục hoặc tròn trăm để tính thuận tiện.";break;
            case "ratio_table":case "ratio_difference_table":case "ratio_link":
                extra="Hơn bao nhiêu đơn vị: lấy số lớn trừ số bé. Gấp mấy lần: lấy số lớn chia số bé. Gấp một số lên nhiều lần: dùng phép nhân.";break;
            default:if(q.optInt("lesson")==38&&"number".equals(q.optString("kind")))extra="Xác định phép tính phải làm trước, rồi thay bằng kết quả vừa tính.";
        }
        return extra.isEmpty()?old:(old.isEmpty()?extra:old+"\n"+extra);
    }
    static String solution(JSONObject q){
        String old=Part02Tutor.solution(q);if(!applies(q))return old;
        JSONObject v=Json.obj(q,"visual");String type=v.optString("type");List<String> lines=new ArrayList<>();
        Object expected="study".equals(q.optString("kind"))?q.opt("sampleAnswer"):q.opt("answer");
        if("long_multiplication".equals(type)){
            int n=v.optInt("multiplicand",-1),b=v.optInt("multiplier",-1);
            if(n>=0&&n<=999&&b>=1&&b<=9&&Json.same(expected,n*b)){
                List<Mul> rows=multiply(n,b);for(int i=0;i<rows.size();i++)lines.add(mulLine(rows.get(i),b,i,rows.size()));
                lines.add(n+" × "+b+" = "+n*b+".");
            }
        }else if("long_division".equals(type)){
            int n=v.optInt("dividend",-1),b=v.optInt("divisor",-1);
            if(n>=0&&n<=999&&b>=1&&b<=9&&expected instanceof JSONObject){
                JSONObject key=(JSONObject)expected;
                if(key.optInt("quotient",-1)==n/b&&key.optInt("remainder",-1)==n%b){
                    List<Div> rows=divide(n,b);for(int i=0;i<rows.size();i++)lines.add(divLine(rows.get(i),b,i));
                    lines.add(n+" : "+b+" = "+n/b+(n%b==0?"":" dư "+n%b)+". Thử lại: "+n/b+" × "+b+(n%b==0?"":" + "+n%b)+" = "+n+".");
                }
            }
        }else if("expression".equals(type)){
            Expression trace=expression(v.optString("expression"));
            if(trace!=null&&Json.same(expected,trace.value)&&trace.lines.size()>1){
                // Original source solution remains first (including convenient regrouping).
                lines.add(String.join(" = ",trace.lines));
            }
        }
        return lines.isEmpty()?old:old+"\n\nCách tính từng bước:\n"+String.join("\n",lines);
    }
    static final class Guide{
        final String name,rule,example,model;final int a,b;final String[] steps;
        Guide(String name,String rule,String example,String model,int a,int b,String...steps){
            this.name=name;this.rule=rule;this.example=example;this.model=model;this.a=a;this.b=b;this.steps=steps;
        }
    }
    private static Guide mg(String name,int n,int b){
        List<Mul> rows=multiply(n,b);String[] steps=new String[3];
        for(int i=0;i<3;i++)steps[i]=mulLine(rows.get(i),b,i,3)+(i==2?" Vậy "+n+" × "+b+" = "+n*b+".":"");
        return new Guide(name,"Nhân từ hàng đơn vị; cộng số nhớ ở lượt kế tiếp.",n+" × "+b,"multiply",n,b,steps);
    }
    private static Guide dg(String name,int n,int b){
        List<Div> rows=divide(n,b);String[] steps=new String[3];
        for(int i=0;i<3;i++)steps[i]=i<rows.size()?divLine(rows.get(i),b,i):"Kiểm tra lại phép chia.";
        steps[2]+=" Vậy "+n+" : "+b+" = "+n/b+(n%b==0?"":" dư "+n%b)+". Thử lại: "+n/b+" × "+b+(n%b==0?"":" + "+n%b)+" = "+n+".";
        return new Guide(name,"Chia → nhân → trừ → hạ. Số dư nhỏ hơn số chia.",n+" : "+b,"divide",n,b,steps);
    }
    static Guide[] guides(int l){
        switch(l){
            case 36:return new Guide[]{mg("Nhân có nhớ liên tiếp",237,3),mg("Nhân khi số có chữ số 0",208,4),mg("Nhân không nhớ",124,2)};
            case 37:return new Guide[]{dg("Chia hết",648,3),dg("Chia có chữ số 0 ở thương",437,4),dg("Chữ số đầu nhỏ hơn số chia",305,6)};
            case 38:return new Guide[]{
                new Guide("Cộng và trừ theo thứ tự","Cùng mức ưu tiên: tính từ trái sang phải.","58 − 18 + 12","expression",0,0,"Chọn phép trừ bên trái trước.","58 − 18 = 40.","40 + 12 = 52."),
                new Guide("Nhân trước khi cộng","Không có ngoặc: nhân, chia trước cộng, trừ.","25 + 6 × 4","expression",0,0,"Tính 6 × 4 trước.","25 + 24.","25 + 24 = 49."),
                new Guide("Trong ngoặc trước","Tính giá trị trong ngoặc rồi thay vào biểu thức.","48 : (9 − 3)","expression",0,0,"Trong ngoặc: 9 − 3 = 6.","48 : 6.","48 : 6 = 8.")};
            case 39:return new Guide[]{
                new Guide("Số lớn gấp mấy lần số bé","Chia số lớn cho số bé.","24 quả và 6 quả","ratio",24,6,"Xác định số lớn 24, số bé 6.","24 : 6 = 4.","24 gấp 6 là 4 lần."),
                new Guide("Hơn bao nhiêu khác gấp mấy lần","Hơn: trừ; gấp mấy lần: chia.","30 cm và 5 cm","ratio",30,5,"Hai đoạn dài 30 cm và 5 cm.","30 − 5 = 25 (cm).","30 : 5 = 6 (lần). Hai câu hỏi có đáp án khác nhau."),
                new Guide("So sánh hai khối lượng","Đổi về cùng đơn vị trước khi chia.","800 g và 200 g","ratio",800,200,"Hai số đo đã cùng đơn vị g.","800 : 200 = 4.","800 g gấp 200 g là 4 lần.")};
            case 40:return new Guide[]{
                mg("Ôn đặt tính nhân",309,3),dg("Ôn đặt tính chia có dư",829,4),
                new Guide("Tính thuận tiện","Nhóm các số hạng để được số tròn trăm.","126 + 38 + 62","expression",0,0,"Nhóm 38 và 62: 38 + 62 = 100.","126 + (38 + 62) = 126 + 100.","126 + 100 = 226.")};
            default:return new Guide[0];
        }
    }
}
