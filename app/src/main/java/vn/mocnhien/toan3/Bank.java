package vn.mocnhien.toan3;
import java.util.*;
public final class Bank {
 public static final int CORE=24,N=26;
 public static final String[] TITLES={"Chia hết, chia có dư","Chia số có hai chữ số","Giảm một số đi một số lần","Bài toán hai bước tính","Luyện tập chung","Mi-li-mét","Gam","Mi-li-lít","Nhiệt độ","Thực hành đo lường","Luyện tập chung","Nhân số có ba chữ số","Chia số có ba chữ số","Biểu thức số","Số lớn gấp mấy lần số bé","Luyện tập chung","Ôn tập nhân, chia","Ôn tập biểu thức","Ôn tập hình học, đo lường","Luyện tập chung"};
 public static final class Q {
  public final String text,help; public final String[] options; public final int answer,kind,a,b,c;
  Q(String t,String h,String[] o,int ans,int k,int x,int y,int z){text=t;help=h;options=o;answer=ans;kind=k;a=x;b=y;c=z;}
 }
 static Q ask(int s,String t,String h,String ans,String... wrong){
  ArrayList<String> v=new ArrayList<>();v.add(ans);v.addAll(Arrays.asList(wrong));Collections.shuffle(v,new Random(s));
  return new Q(t,h,v.toArray(new String[0]),v.indexOf(ans),0,0,0,0);
 }
 static Q num(int s,String t,int a,String unit,String h){
  LinkedHashSet<Integer> v=new LinkedHashSet<>();v.add(a);
  for(int d:new int[]{1,-1,2,-2,3,10})if(a+d>=0&&v.size()<4)v.add(a+d);
  ArrayList<String> o=new ArrayList<>();for(int n:v)o.add(n+unit);
  return ask(s,t,h,o.get(0),o.get(1),o.get(2),o.get(3));
 }
 static Q div(int s,int a,int b){int q=a/b,r=a%b;return r==0?num(s,a+" : "+b+" = ?",q,"",a+" = "+b+" × "+q):ask(s,a+" : "+b+" = ?","Số dư nhỏ hơn số chia. "+a+" = "+b+" × "+q+" + "+r,q+" dư "+r,(q+1)+" dư "+r,q+" dư "+(r-1),(q+2)+" dư 0");}
 static Q mul(int s,int a,int b){return num(s,a+" × "+b+" = ?",a*b,"","Nhân từng hàng: "+a+" × "+b+" = "+a*b);}
 static Q group(int a,int b){return new Q("Chia đều "+a+" quả táo vào "+b+" giỏ. Để riêng số dư.","Mỗi giỏ "+a/b+" quả; dư "+a%b+" quả.",new String[0],0,1,a,b,a/b);}
 static Q water(int a,int b,int t){return new Q("Dùng ca "+a+" ml và "+b+" ml để đong đúng "+t+" ml.","Đổ đầy, rót sang ca kia hoặc đổ hết. Cần "+t+" ml.",new String[0],0,2,a,b,t);}
 static Q collect(int a){return new Q("Hãy thu hoạch đúng "+a+" quả táo.","Đếm từng quả: cần "+a+" quả.",new String[0],0,3,a,a+4,0);}
 static Q story(int s,int a,int b,int c){switch(s%3){
 case 0:return num(s,"Có "+a+" hoa hồng, hoa cúc gấp "+b+" lần. Có tất cả bao nhiêu bông?",a+a*b," bông","Hoa cúc: "+a+" × "+b+" = "+a*b+". Tổng: "+a+" + "+a*b+" = "+(a+a*b)+".");
 case 1:return num(s,"Có "+a+" túi, mỗi túi "+b+" quả. Tặng "+c+" quả. Còn lại bao nhiêu?",a*b-c," quả","Có "+a*b+" quả. Còn lại "+a*b+" − "+c+" = "+(a*b-c)+".");
 default:return num(s,"Chia đều "+a*b+" bánh vào "+b+" hộp, thêm "+c+" bánh mỗi hộp. Một hộp có mấy bánh?",a+c," bánh","Mỗi hộp "+a+" bánh. Thêm "+c+" được "+(a+c)+".");
 }}
 static Q expr(int s,int a,int b,int c){switch(s%4){
 case 0:return num(s,a+" + "+b+" × "+c+" = ?",a+b*c,"","Nhân trước: "+b+" × "+c+" = "+b*c+", rồi cộng "+a+".");
 case 1:return num(s,"("+a+" + "+b+") × "+c+" = ?",(a+b)*c,"","Trong ngoặc: "+(a+b)+", rồi nhân "+c+".");
 case 2:return num(s,a*b+" : "+b+" + "+c+" = ?",a+c,"","Chia trước được "+a+", rồi cộng "+c+".");
 default:return num(s,(a*b+c)+" − "+a+" × "+b+" = ?",c,"","Nhân trước rồi trừ.");
 }}
 static Q measure(int s,int k,int a,int b){switch(k){
 case 0:if(s%3==0){Q q=num(s,"Đo đoạn thẳng trên thước.",a+10," mm","Đọc từ vạch 0 đến "+(a+10)+" mm.");return new Q(q.text,q.help,q.options,q.answer,4,a+10,0,0);}return s%2==0?num(s,a+" cm = ? mm",a*10," mm","1 cm = 10 mm."):num(s,(a*10+b)+" mm = "+a+" cm và ? mm",b," mm","Tách phần cm và mm.");
 case 1:if(s%3==0){Q q=num(s,"Cân đang chỉ bao nhiêu gam?",a*100+b*10," g","Đọc số gam trên cân.");return new Q(q.text,q.help,q.options,q.answer,6,a*100+b*10,0,0);}return s%2==0?num(s,a+" kg = ? g",a*1000," g","1 kg = 1000 g."):num(s,(a*100)+" g + "+(b*20)+" g = ?",a*100+b*20," g","Cộng cùng đơn vị.");
 case 2:return s%4==0?water(500,300,200):s%4==1?water(700,400,300):s%2==0?num(s,a+" l = ? ml",a*1000," ml","1 l = 1000 ml."):num(s,(a*100)+" ml + "+(b*20)+" ml = ?",a*100+b*20," ml","Cộng cùng đơn vị.");
 default:if(s%3==0){Q q=num(s,"Nhiệt kế chỉ bao nhiêu độ C?",20+a," °C","Đọc đầu cột đỏ.");return new Q(q.text,q.help,q.options,q.answer,5,20+a,0,0);}return num(s,"Buổi sáng "+(20+a)+" °C, tăng "+b+" °C. Nhiệt độ mới?",20+a+b," °C","Cộng nhiệt độ với mức tăng.");
 }}
 static Q geom(int s,int a){switch(s%4){
 case 0:return ask(s,"Hình có bốn góc vuông và bốn cạnh bằng nhau?","Đó là hình vuông.","Hình vuông","Hình tròn","Tam giác","Hình chữ nhật");
 case 1:return num(s,"Bán kính hình tròn "+a+" cm. Đường kính?",2*a," cm","Đường kính gấp đôi bán kính.");
 case 2:return num(s,"Khối lập phương có bao nhiêu mặt?",6," mặt","Có 6 mặt hình vuông.");
 default:return ask(s,"Hình nào không có góc?","Hình tròn không có góc.","Hình tròn","Hình vuông","Hình chữ nhật","Tam giác");
 }}
 public static Q get(int l,int i){
  int s=l*100+i,a=2+(i*3+l)%8,b=2+(i*5+l)%7,c=2+i%5,q=3+i*2;
  switch(l){
  case 25:if(i%5==0)return group(10+i%9,2+i%4);if(i%5==1)return collect(3+i%9);return div(s,12+i*3,b);
  case 26:return div(s,31+i*2,b);
  case 27:return num(s,"Giảm "+a*q+" đi "+a+" lần được bao nhiêu?",q,"","Lấy "+a*q+" : "+a+" = "+q+".");
  case 28:return story(i,5+a,b,c);
  case 29:return i%3==0?story(i,5+a,b,c):i%3==1?div(s,28+i*2,b):mul(s,11+i,a);
  case 30:case 31:case 32:case 33:return measure(i,l-30,a,b);
  case 34:case 35:return measure(i,i%4,a,b);
  case 36:return mul(s,101+i*4,2+i%4);
  case 37:return div(s,105+i*23,b);
  case 38:case 42:return expr(i,a,b,c);
  case 39:return num(s,"Số "+a*q+" gấp số "+a+" bao nhiêu lần?",q," lần",a*q+" : "+a+" = "+q+".");
  case 40:case 41:return i%3==0?mul(s,101+i*3,2+i%5):i%3==1?div(s,200+i*13,b):story(i,a+5,b,c);
  case 43:return i%3==0?measure(i,i%4,a,b):geom(i,a);
  default:return i%4==0?story(i,5+a,b,c):i%4==1?expr(i,a,b,c):i%4==2?geom(i,a):measure(i,i%4,a,b);
  }
 }
 public static void main(String[] args){int n=0;for(int l=25;l<=44;l++)for(int i=0;i<N;i++){Q q=get(l,i);if(q.text.isEmpty()||q.help.isEmpty())throw new AssertionError();if(q.options.length>0&&(q.options.length!=4||new HashSet<>(Arrays.asList(q.options)).size()!=4||q.answer<0||q.answer>=4))throw new AssertionError(q.text);if(q.kind==1&&(q.a>20||q.c!=q.a/q.b))throw new AssertionError(q.text);if(q.kind==2&&q.c%gcd(q.a,q.b)!=0)throw new AssertionError(q.text);n++;}if(n!=520)throw new AssertionError(n);System.out.println("BANK_PASS "+n);}
 static int gcd(int a,int b){return b==0?a:gcd(b,a%b);}
}
