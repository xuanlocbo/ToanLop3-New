package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;
import org.json.JSONObject;
import java.util.*;

/** Verify the shell without changing the existing curriculum, game, grading or reward tests. */
public final class ModuleRoutingTest {
    @Test public void fourModulesHaveDistinctRoutesAndAllLegacyScreensRemainReachable(){
        Set<String> routes=new HashSet<>();
        assertEquals(4,AppModule.values().length);
        for(AppModule m:AppModule.values()){
            assertTrue(routes.add(m.route));
            assertTrue(AppModule.known(m.route));
            assertEquals(m,AppModule.owner(m.route,false));
            assertEquals("home",AppModule.back(m.route,false));
        }
        for(String screen:new String[]{"home","lesson","list","question","review","awards","settings","adventure","game","gate"})
            assertTrue(screen,AppModule.known(screen));
        assertFalse(AppModule.known(null));
        assertFalse(AppModule.known("unknown"));
        assertEquals(AppModule.LEARNING,AppModule.owner("question",false));
        assertEquals(AppModule.PROGRESS,AppModule.owner("question",true));
        assertEquals(AppModule.GAME,AppModule.owner("gate",false));
        assertEquals("learn",AppModule.back("lesson",false));
        assertEquals("lesson",AppModule.back("list",false));
        assertEquals("list",AppModule.back("question",false));
        assertEquals("review",AppModule.back("question",true));
        assertEquals("progress",AppModule.back("review",false));
        assertEquals("game",AppModule.back("gate",false));
        assertEquals("adventure",AppModule.back("game",false));
    }
    @Test public void sharedContextUsesTheSame959RecordsAndReadOnlyViews() throws Exception{
        NativeCurriculumTest fixture=new NativeCurriculumTest();
        Curriculum bank=fixture.load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        ModuleContext ctx=new ModuleContext(null,bank,store,null);
        assertSame(store,ctx.store);
        assertSame(bank,ctx.bank);
        assertEquals(437,ctx.section("sgk").size());
        assertEquals(484,ctx.section("vbt").size());
        assertEquals(38,ctx.section("bonus").size());
        assertEquals(0,ctx.completed());
        int sum=0;for(int l=26;l<=44;l++){sum+=ctx.count(l);assertEquals(0,ctx.done(l));}
        assertEquals(959,sum);
        assertEquals(-1,ctx.gameResume());
        String id="SGK.32.89.HD.1.b";
        JSONObject q=bank.questions.get(id);
        assertFalse(store.submit(id,new JSONObject()).correct);
        assertEquals(1,ctx.reviewCount());
        assertEquals(10,store.submit(id,fixture.draft(q)).earned);
        assertEquals(0,ctx.reviewCount());
        assertEquals(1,ctx.completed());
        assertEquals(1,ctx.done(32));
        for(int i=1;i<=2;i++){
            String bonus="BONUS.26."+i;
            assertTrue(store.submit(bonus,fixture.draft(bank.questions.get(bonus))).correct);
        }
        assertTrue(ctx.chest(26));
        assertEquals(310,store.balance());
        AdventureWorld.State motion=store.adventureState(26);
        new AdventureWorld(26).step(motion,1,false);
        assertTrue(store.saveAdventureMotion(26,motion));
        assertEquals(26,ctx.gameResume());
        String before=disk.data;
        ctx.completed();ctx.reviewCount();ctx.gameResume();ctx.clearedGames();ctx.section("sgk");ctx.chest(26);
        assertEquals("Read-only pages must not change persistence",before,disk.data);
    }
    @Test public void legacyNavigationAndBusinessSnapshotSurviveAllModuleSwitches() throws Exception{
        NativeCurriculumTest fixture=new NativeCurriculumTest();
        Curriculum bank=fixture.load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        String id="SGK.32.89.HD.1.b";
        assertTrue(store.submit(id,fixture.draft(bank.questions.get(id))).correct);
        JSONObject legacy=new JSONObject();
        Json.put(legacy,"screen","question");Json.put(legacy,"lesson",32);Json.put(legacy,"book","sgk");
        Json.put(legacy,"question",id);Json.put(legacy,"lastId",id);Json.put(legacy,"scroll",21);
        assertTrue(store.setNav(legacy));assertTrue(store.setSound(false));
        JSONObject baseline=new JSONObject(disk.data);baseline.remove("nav");
        for(AppModule m:AppModule.values()){
            store=new ProgressStore(bank,disk);
            assertTrue(AppModule.known(store.nav().optString("screen")));
            JSONObject n=store.nav();Json.put(n,"screen",m.route);Json.put(n,"scroll",0);
            assertTrue(store.setNav(n));
            JSONObject after=new JSONObject(disk.data);after.remove("nav");
            assertTrue(m.label,baseline.similar(after));
            assertEquals(10,store.balance());assertTrue(store.done(id));assertFalse(store.sound());
        }
        store=new ProgressStore(bank,disk);
        assertEquals("progress",store.nav().optString("screen"));
        assertEquals(id,store.nav().optString("lastId"));
        assertEquals(0,store.submit(id,fixture.draft(bank.questions.get(id))).earned);
    }
}
