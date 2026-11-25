package vn.mocnhien.toan3;

/** Stable module routes. Saved 1.1.1 screen names remain valid. */
public enum AppModule {
    LEARNING("Học bài","learn",0xffffdce9),
    GAME("Chơi game","adventure",0xffe7ddfa),
    REWARDS("Phần thưởng","awards",0xffffe5ad),
    PROGRESS("Tiến độ","progress",0xffd6f0e0);
    public final String label,route;
    public final int color;
    AppModule(String label,String route,int color){this.label=label;this.route=route;this.color=color;}
    public static boolean known(String route){
        if(route==null)return false;
        switch(route){
            case "home":case "learn":case "lesson":case "list":case "question":
            case "adventure":case "game":case "gate":case "awards":
            case "progress":case "review":case "settings":return true;
            default:return false;
        }
    }
    public static AppModule owner(String route,boolean reviewMode){
        if(route==null)return null;
        switch(route){
            case "learn":case "lesson":case "list":return LEARNING;
            case "question":return reviewMode?PROGRESS:LEARNING;
            case "adventure":case "game":case "gate":return GAME;
            case "awards":return REWARDS;
            case "progress":case "review":return PROGRESS;
            default:return null;
        }
    }
    public static String back(String route,boolean reviewMode){
        if(route==null)return "home";
        switch(route){
            case "lesson":return LEARNING.route;
            case "list":return "lesson";
            case "question":return reviewMode?"review":"list";
            case "game":return GAME.route;
            case "gate":return "game";
            case "review":return PROGRESS.route;
            default:return "home";
        }
    }
}
