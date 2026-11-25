package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;

/** Freeze all legacy routes, nullable inputs and the Java-facing Kotlin ABI. */
public final class KotlinShellParityTest {
    @Test public void exactModuleMetadataAndOrdering(){
        String[] labels={"Học bài","Chơi game","Phần thưởng","Tiến độ"};
        String[] routes={"learn","adventure","awards","progress"};
        int[] colors={0xffffdce9,0xffe7ddfa,0xffffe5ad,0xffd6f0e0};
        AppModule[] modules=AppModule.values();
        assertEquals(4,modules.length);
        for(int i=0;i<modules.length;i++){
            assertEquals(labels[i],modules[i].label);
            assertEquals(routes[i],modules[i].route);
            assertEquals(colors[i],modules[i].color);
            assertEquals(modules[i],AppModule.valueOf(modules[i].name()));
        }
    }

    @Test public void workbookShortcutUsesLearningModule(){
        assertTrue(AppModule.known("practice"));
        assertEquals(AppModule.LEARNING,AppModule.owner("practice",false));
        assertEquals("home",AppModule.back("practice",false));
    }

    @Test public void everyLegacyScreenAndBackRoute(){
        String[] screens={"home","learn","lesson","list","question","adventure","game","gate","awards","progress","review","settings",null,"unknown",""};
        AppModule L=AppModule.LEARNING,G=AppModule.GAME,R=AppModule.REWARDS,P=AppModule.PROGRESS;
        AppModule[] normal={null,L,L,L,L,G,G,G,R,P,P,null,null,null,null};
        String[] back={"home","home","learn","lesson","list","home","adventure","game","home","home","progress","home","home","home","home"};
        for(int i=0;i<screens.length;i++){
            String s=screens[i];
            assertEquals(s,i<12,AppModule.known(s));
            assertEquals(s,normal[i],AppModule.owner(s,false));
            assertEquals(s,"question".equals(s)?P:normal[i],AppModule.owner(s,true));
            assertEquals(s,back[i],AppModule.back(s,false));
            assertEquals(s,"question".equals(s)?"review":back[i],AppModule.back(s,true));
        }
    }
}
