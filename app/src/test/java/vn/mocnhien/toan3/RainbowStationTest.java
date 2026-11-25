package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;

public final class RainbowStationTest {
    @Test public void sixStationsRestoreEveryCommittedActionAndNeverGrantRewards() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        int[][] solutions={{0,1,3},{0,1,2},{0,4},{3,2,2},{0,1,2,3},{0,4}};
        for(int lesson=30;lesson<=35;lesson++){
            assertTrue(RainbowStationSession.supports(lesson));
            AdventureWorld.State motion=store.adventureState(lesson);
            new AdventureTest().walk(new AdventureWorld(lesson),motion);
            assertTrue(store.saveAdventureMotion(lesson,motion));
            RainbowStationSession s=new RainbowStationSession(store,lesson);
            int[] initial=s.snapshot();
            assertEquals(RainbowStationSession.Outcome.NOT_READY,s.check());
            disk.fail=true;
            assertEquals(RainbowStationSession.Outcome.SAVE_FAILED,s.move(solutions[lesson-30][0]));
            assertArrayEquals(initial,s.snapshot());
            disk.fail=false;
            for(int action:solutions[lesson-30]){
                int[] expected=s.mission.move(s.snapshot(),action);
                assertEquals(RainbowStationSession.Outcome.CHANGED,s.move(action));
                store=new ProgressStore(bank,disk);
                s=new RainbowStationSession(store,lesson);
                assertArrayEquals(expected,s.snapshot());
            }
            assertTrue(s.canComplete());
            disk.fail=true;
            assertEquals(RainbowStationSession.Outcome.SAVE_FAILED,s.check());
            assertFalse(store.trailComplete(lesson));
            disk.fail=false;
            int balance=store.balance();
            assertEquals(RainbowStationSession.Outcome.COMPLETE,s.check());
            assertEquals(RainbowStationSession.Outcome.NO_CHANGE,s.check());
            assertEquals(balance,store.balance());
            assertFalse(store.done(AdventurePlan.questionId(lesson,0)));
            assertTrue(store.adventureState(lesson).pending);
            assertFalse(store.adventureState(lesson).answered);
            store=new ProgressStore(bank,disk);
            assertTrue(store.trailComplete(lesson));
        }
        for(int l=26;l<=29;l++)assertFalse(RainbowStationSession.supports(l));
        for(int l=36;l<=44;l++)assertFalse(RainbowStationSession.supports(l));
    }
    @Test public void pourConservationBoundsAndResetAreCorrect() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        for(int lesson:new int[]{32,35}){
            AdventureWorld.State motion=store.adventureState(lesson);
            new AdventureTest().walk(new AdventureWorld(lesson),motion);
            assertTrue(store.saveAdventureMotion(lesson,motion));
            RainbowStationSession s=new RainbowStationSession(store,lesson);
            assertEquals(RainbowStationSession.Outcome.NO_CHANGE,s.move(4));
            assertEquals(RainbowStationSession.Outcome.CHANGED,s.move(0));
            int before=s.snapshot()[0]+s.snapshot()[1];
            assertEquals(RainbowStationSession.Outcome.CHANGED,s.move(4));
            assertEquals(before,s.snapshot()[0]+s.snapshot()[1]);
            assertTrue(s.canComplete());
            disk.fail=true;
            int[] prior=s.snapshot();
            assertEquals(RainbowStationSession.Outcome.SAVE_FAILED,s.reset());
            assertArrayEquals(prior,s.snapshot());
            disk.fail=false;
            assertEquals(RainbowStationSession.Outcome.CHANGED,s.reset());
            assertArrayEquals(new int[]{0,0},s.snapshot());
            assertFalse(s.canComplete());
            assertEquals(0,store.balance());
        }
    }
}
