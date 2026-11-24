package vn.mocnhien.toan3;

import java.util.*;

/** Original, curriculum-aligned station puzzles. No Android dependency.
 * State is a small, validated value; actual manipulations, not button guesses, solve a station.
 * These puzzles are not copied exercises and never issue textbook completion or coins. */
public final class TrailMission {
    public enum Kind {GROUP,BUILD,POUR,TEMP,STEPS,GRID}
    public final int lesson;
    public final Kind kind;
    public final String title,brief,hint,unit;
    public final int total,target,bins,start;
    private final int[] tiles,expected;
    private final String[] operations;
    private TrailMission(int l,Kind k,String title,String brief,String hint,String unit,
                         int total,int target,int bins,int start,int[] tiles,String[] operations,int[] expected){
        this.lesson=l;kind=k;this.title=title;this.brief=brief;this.hint=hint;this.unit=unit;
        this.total=total;this.target=target;this.bins=bins;this.start=start;
        this.tiles=tiles.clone();this.operations=operations.clone();this.expected=expected.clone();
    }
    private static TrailMission group(int l,String title,String brief,int total,int bins,String unit){
        return new TrailMission(l,Kind.GROUP,title,brief,"Mỗi nhóm phải bằng nhau. Số còn lại phải nhỏ hơn số nhóm.",unit,
            total,total/bins,bins,0,new int[0],new String[0],new int[0]);
    }
    private static TrailMission build(int l,String title,String brief,String hint,int target,String unit,int... tiles){
        return new TrailMission(l,Kind.BUILD,title,brief,hint,unit,0,target,0,0,tiles,new String[0],new int[0]);
    }
    private static TrailMission pour(int l,String title,String brief,int a,int b,int target){
        return new TrailMission(l,Kind.POUR,title,brief,"Rót đến khi ca nguồn cạn hoặc ca nhận đầy. Có thể đổ hết rồi thử lại.","ml",
            0,target,0,0,new int[]{a,b},new String[0],new int[0]);
    }
    private static TrailMission steps(int l,String title,String brief,String hint,int start,String[] ops,int... sequence){
        return new TrailMission(l,Kind.STEPS,title,brief,hint,"",0,0,0,start,new int[0],ops,sequence);
    }
    public static TrailMission of(int l){
        switch(l){
            case 26:return group(l,"Bến chia hàng","Chia đều tối đa 17 kiện hàng vào 3 thuyền. Để riêng phần dư.",17,3,"kiện");
            case 27:return steps(l,"Máy thu nhỏ","Có 36 hạt giống. Hãy làm số hạt giảm đi 4 lần.","Giảm đi một số lần dùng phép chia, không phải phép trừ.",36,new String[]{":4","−4","×4","+4"},0);
            case 28:return steps(l,"Dựng hai nhịp cầu","Mai có 10 mảnh ghép. Nam ít hơn Mai 3 mảnh. Tìm số mảnh của Nam, rồi tìm tổng hai bạn.","Bước 1 bớt 3, bước 2 cộng số của Mai.",10,new String[]{"−3","+10","−10","+3"},0,1);
            case 29:return group(l,"Xếp hàng vào kho","Có 23 viên đá quý, chia đều tối đa vào 4 rương. Để riêng phần dư.",23,4,"viên");
            case 30:return build(l,"Ghép cầu thước","Chọn các tấm ván để ghép một chiếc cầu dài đúng 4 cm 2 mm. Các tấm nối sát, không chồng lên nhau.","Đổi cm sang mm rồi cộng các tấm ván.",42,"mm",10,20,15,12,5,7,20,3);
            case 31:return build(l,"Cân giỏ hoa quả","Đặt các quả cân để cân bằng giỏ hoa quả nặng 350 g.","Cộng khối lượng các quả cân đang đặt.",350,"g",200,100,50,20,100,200);
            case 32:return pour(l,"Pha nước cầu vồng","Dùng ca 500 ml và ca 300 ml. Hãy đong đúng 200 ml trong một ca.",500,300,200);
            case 33:return new TrailMission(l,Kind.TEMP,"Trạm nhiệt kế","Buổi sáng 21°C, đến trưa tăng 7°C. Điều chỉnh nhiệt kế đến nhiệt độ buổi trưa.","Nhiệt độ trưa bằng nhiệt độ sáng cộng mức tăng.","°C",0,28,0,21,new int[0],new String[0],new int[0]);
            case 34:return build(l,"Cân trong phòng thí nghiệm","Lấy đúng 1 kg gạo bằng các quả cân.","1 kg bằng 1000 g.",1000,"g",500,200,100,200,100,50);
            case 35:return pour(l,"Suối nước nhỏ","Dùng hai ca 400 ml và 150 ml để lấy đúng 250 ml trong một ca.",400,150,250);
            case 36:return build(l,"Xưởng đóng kiện","Có 3 thùng, mỗi thùng 124 viên pha lê. Chọn các bó 100, 10 và viên lẻ để tạo đúng tổng số pha lê.","Tính 124 × 3 rồi ghép các hàng trăm, chục và đơn vị.",372,"viên",100,100,100,10,10,10,10,10,10,10,10,1,1,1,1);
            case 37:return group(l,"Kho pha lê","Chia đều 124 viên pha lê vào 4 rương. Có thể chuyển từng viên hoặc từng bó 10 viên.",124,4,"viên");
            case 38:return steps(l,"Cổng thứ tự phép tính","Tính 8 + 6 × 4. Máy bắt đầu ở số 6. Thực hiện phép nhân trước, rồi cộng số còn lại.","Nhân, chia trước cộng, trừ.",6,new String[]{"×4","+8","×8","+4"},0,1);
            case 39:return build(l,"Ghép dải đo","Dải lớn dài 24 cm; mỗi dải nhỏ dài 6 cm. Chọn đủ dải nhỏ ghép bằng dải lớn. Đếm số dải đã chọn để biết dài gấp mấy lần.","Các dải nhỏ phải nối sát và tổng bằng 24 cm.",24,"cm",6,6,6,6,6,6);
            case 40:return steps(l,"Xe chở thức ăn","Bê nặng 120 kg, bò nặng gấp 3 lần bê. Tìm khối lượng bò, rồi tìm tổng khối lượng.","Tính số lớn trước, rồi cộng số bé.",120,new String[]{"×3","+120","+3",":3"},0,1);
            case 41:return build(l,"Chia phần ngôi sao","Có 15 ngôi sao. Chọn đúng một phần ba số ngôi sao để tặng bạn.","Chia 15 thành 3 phần bằng nhau; lấy 1 phần.",5,"sao",1,1,1,1,1,1,1,1,1,1,1,1,1,1,1);
            case 42:return steps(l,"Mê cung dấu ngoặc","Tính (24 + 12) : 4. Bắt đầu ở 24, chọn phép tính theo đúng thứ tự.","Trong ngoặc trước, rồi chia.",24,new String[]{"+12",":4","+4","×4"},0,1);
            case 43:return new TrailMission(l,Kind.GRID,"Dựng cửa hình chữ nhật","Chạm lần lượt 4 đỉnh để vẽ hình chữ nhật rộng 3 ô, cao 2 ô. Bấm kiểm tra để khép kín.","Hai cạnh ngang dài 3 ô, hai cạnh dọc dài 2 ô; không nối chéo.","ô",3,2,0,0,new int[0],new String[0],new int[0]);
            case 44:return build(l,"Kho nước học kỳ","Bình 1 l, đã rót 350 ml. Chọn các ca nhỏ để đong đúng lượng nước còn lại.","Đổi 1 l thành ml, trừ lượng đã rót.",650,"ml",500,200,100,50,250,100,300);
            default:throw new IllegalArgumentException("Unknown lesson "+l);
        }
    }
    public int[] initial(){
        switch(kind){
            case GROUP:return new int[bins];
            case BUILD:return new int[]{0};
            case POUR:return new int[]{0,0};
            case TEMP:return new int[]{start};
            case STEPS:{int[] s=new int[expected.length];Arrays.fill(s,-1);return s;}
            case GRID:return new int[]{-1,-1,-1,-1};
            default:throw new AssertionError();
        }
    }
    public int[] read(String value){
        if(value==null||value.isEmpty())return initial();
        try{
            String[] parts=value.split(",",-1);
            if(parts.length!=initial().length)return initial();
            int[] a=new int[parts.length];for(int i=0;i<a.length;i++)a[i]=Integer.parseInt(parts[i]);
            return valid(a)?a:initial();
        }catch(RuntimeException e){return initial();}
    }
    public String encode(int[] s){
        if(!valid(s))throw new IllegalArgumentException("Invalid station state");
        StringBuilder b=new StringBuilder();
        for(int i=0;i<s.length;i++){if(i>0)b.append(',');b.append(s[i]);}
        return b.toString();
    }
    int sum(int[] a){int n=0;for(int x:a)n+=x;return n;}
    public boolean valid(int[] s){
        if(s==null||s.length!=initial().length)return false;
        switch(kind){
            case GROUP:
                for(int x:s)if(x<0||x>total)return false;return sum(s)<=total;
            case BUILD:return s[0]>=0&&s[0]<(1<<tiles.length);
            case POUR:return s[0]>=0&&s[0]<=tiles[0]&&s[1]>=0&&s[1]<=tiles[1];
            case TEMP:return s[0]>=0&&s[0]<=50;
            case STEPS:{
                boolean hole=false;int n=start;
                for(int i:s){if(i==-1){hole=true;continue;}if(hole||i<0||i>=operations.length)return false;
                    n=operate(n,operations[i]);if(n==Integer.MIN_VALUE)return false;}
                return true;
            }
            case GRID:{
                Set<Integer> used=new HashSet<>();boolean hole=false;
                for(int i:s){if(i==-1){hole=true;continue;}if(hole||i<0||i>=25||!used.add(i))return false;}
                return true;
            }
            default:return false;
        }
    }
    private static int operate(int n,String op){
        int a=Integer.parseInt(op.substring(1));int result;
        switch(op.charAt(0)){
            case '+':result=n+a;break;
            case '−':result=n-a;break;
            case '×':result=n*a;break;
            case ':':if(a==0||n%a!=0)return Integer.MIN_VALUE;result=n/a;break;
            default:return Integer.MIN_VALUE;
        }
        return result<0||result>10000?Integer.MIN_VALUE:result;
    }
    public int result(int[] s){
        if(!valid(s))return 0;
        switch(kind){
            case GROUP:return total-sum(s);
            case BUILD:{int n=0;for(int i=0;i<tiles.length;i++)if((s[0]&(1<<i))!=0)n+=tiles[i];return n;}
            case TEMP:return s[0];
            case STEPS:{int n=start;for(int i:s)if(i>=0)n=operate(n,operations[i]);return n;}
            default:return 0;
        }
    }
    public int tile(int i){return tiles[i];}
    public int actionCount(){
        switch(kind){
            case GROUP:return bins*(total>60?4:2);
            case BUILD:return tiles.length;
            case POUR:return 6;
            case TEMP:return 4;
            case STEPS:return operations.length+1;
            case GRID:return 26;
            default:throw new AssertionError();
        }
    }
    public String label(int i){
        if(i<0||i>=actionCount())throw new IllegalArgumentException("action");
        switch(kind){
            case GROUP:{int size=total>60?4:2;return new String[]{"+1","−1","+10","−10"}[i%size];}
            case BUILD:return tiles[i]+" "+unit;
            case POUR:return new String[]{"Đầy A","Đổ hết A","Đầy B","Đổ hết B","A → B","B → A"}[i];
            case TEMP:return new String[]{"−5°C","−1°C","+1°C","+5°C"}[i];
            case STEPS:return i==operations.length?"Hoàn tác":operations[i].replace("−","− ");
            case GRID:return i==25?"Hoàn tác":"•";
            default:throw new AssertionError();
        }
    }
    public boolean selected(int[] s,int i){
        if(kind==Kind.BUILD)return (s[0]&(1<<i))!=0;
        if(kind==Kind.GRID&&i<25){for(int v:s)if(v==i)return true;}
        return false;
    }
    public int[] move(int[] before,int i){
        if(!valid(before)||i<0||i>=actionCount())throw new IllegalArgumentException("Invalid station move");
        int[] s=before.clone();
        switch(kind){
            case GROUP:{int size=total>60?4:2;s[i/size]+=new int[]{1,-1,10,-10}[i%size];break;}
            case BUILD:s[0]^=1<<i;break;
            case POUR:
                switch(i){
                    case 0:s[0]=tiles[0];break;case 1:s[0]=0;break;
                    case 2:s[1]=tiles[1];break;case 3:s[1]=0;break;
                    case 4:{int n=Math.min(s[0],tiles[1]-s[1]);s[0]-=n;s[1]+=n;break;}
                    case 5:{int n=Math.min(s[1],tiles[0]-s[0]);s[1]-=n;s[0]+=n;break;}
                }break;
            case TEMP:s[0]+=new int[]{-5,-1,1,5}[i];break;
            case STEPS:case GRID:{
                int undo=kind==Kind.GRID?25:operations.length;
                if(i==undo){for(int j=s.length-1;j>=0;j--)if(s[j]>=0){s[j]=-1;break;}}
                else{for(int j=0;j<s.length;j++)if(s[j]<0){s[j]=i;break;}}
                break;
            }
        }
        return valid(s)?s:before.clone();
    }
    public boolean solved(int[] s){
        if(!valid(s))return false;
        switch(kind){
            case GROUP:for(int x:s)if(x!=target)return false;return result(s)==total%bins;
            case BUILD:return result(s)==target;
            case POUR:return s[0]==target||s[1]==target;
            case TEMP:return s[0]==target;
            case STEPS:return Arrays.equals(s,expected);
            case GRID:
                for(int i:s)if(i<0)return false;
                int minX=5,maxX=-1,minY=5,maxY=-1;
                for(int i:s){minX=Math.min(minX,i%5);maxX=Math.max(maxX,i%5);minY=Math.min(minY,i/5);maxY=Math.max(maxY,i/5);}
                if(maxX-minX!=total||maxY-minY!=target)return false;
                for(int i=0;i<4;i++){
                    int a=s[i],b=s[(i+1)%4],x=a%5,y=a/5;
                    if((x!=minX&&x!=maxX)||(y!=minY&&y!=maxY))return false;
                    if((a%5==b%5)==(a/5==b/5))return false;
                }return true;
            default:return false;
        }
    }
    public String trace(int[] s){
        if(kind!=Kind.STEPS)return "";
        int n=start;StringBuilder b=new StringBuilder(Integer.toString(n));
        for(int i:s){if(i<0)break;n=operate(n,operations[i]);b.append(" ").append(operations[i]).append(" = ").append(n);}
        return b.toString();
    }
}
