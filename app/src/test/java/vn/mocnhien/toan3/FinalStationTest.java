package vn.mocnhien.toan3;
import org.junit.Test;
import static org.junit.Assert.*;

public final class FinalStationTest {
    static final int[][] SOLUTIONS={
        {0,1,2,3,4,5,6,7,8,9,11,12},
        {2,2,2,0,6,6,6,4,10,10,10,8,14,14,14,12},
        {0,1},{0,1,2,3},{0,1},{0,1,2,3,4},{0,1},{0,3,13,10},{0,2,3}
    };
    @Test public void allNineStationsAreSolvableAndEveryCommitRestoresWithoutRewards() throws Exception{
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        for(int lesson=36;lesson<=44;lesson++){
            assertTrue(FinalStationSession.supports(lesson));
            AdventureWorld.State motion=store.adventureState(lesson);
            new AdventureTest().walk(new AdventureWorld(lesson),motion);
            assertTrue(store.saveAdventureMotion(lesson,motion));
            FinalStationSession s=new FinalStationSession(store,lesson);
            int[] initial=s.snapshot(),copy=s.snapshot();copy[0]=999999;
            assertArrayEquals(initial,s.snapshot());
            assertEquals(FinalStationSession.Outcome.NOT_READY,s.check());
            disk.fail=true;
            assertEquals(FinalStationSession.Outcome.SAVE_FAILED,s.move(SOLUTIONS[lesson-36][0]));
            assertArrayEquals(initial,s.snapshot());
            disk.fail=false;
            for(int action:SOLUTIONS[lesson-36]){
                int[] expected=s.mission.move(s.snapshot(),action);
                assertEquals(FinalStationSession.Outcome.CHANGED,s.move(action));
                store=new ProgressStore(bank,disk);s=new FinalStationSession(store,lesson);
                assertArrayEquals(expected,s.snapshot());
            }
            assertTrue("lesson "+lesson,s.canComplete());
            disk.fail=true;
            assertEquals(FinalStationSession.Outcome.SAVE_FAILED,s.check());
            assertFalse(store.trailComplete(lesson));
            disk.fail=false;
            int balance=store.balance();
            assertEquals(FinalStationSession.Outcome.COMPLETE,s.check());
            assertEquals(FinalStationSession.Outcome.NO_CHANGE,s.check());
            assertEquals(balance,store.balance());
            assertFalse(store.done(AdventurePlan.questionId(lesson,0)));
            assertTrue(store.adventureState(lesson).pending);
            assertFalse(store.adventureState(lesson).answered);
            store=new ProgressStore(bank,disk);assertTrue(store.trailComplete(lesson));
        }
        for(int l=26;l<=35;l++)assertFalse(FinalStationSession.supports(l));
    }
    @Test public void invalidRectangleAndWrongOrderNeverComplete() throws Exception{
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        for(int lesson:new int[]{38,40,42,43}){
            AdventureWorld.State motion=store.adventureState(lesson);
            new AdventureTest().walk(new AdventureWorld(lesson),motion);
            assertTrue(store.saveAdventureMotion(lesson,motion));
            FinalStationSession s=new FinalStationSession(store,lesson);
            int[] wrong=lesson==43?new int[]{0,3,10,13}:new int[]{1,0};
            for(int action:wrong)s.move(action);
            assertEquals(FinalStationSession.Outcome.NOT_READY,s.check());
            assertFalse(store.trailComplete(lesson));
            for(int ignored:wrong)assertEquals(FinalStationSession.Outcome.CHANGED,s.move(s.mission.actionCount()-1));
            assertArrayEquals(s.mission.initial(),s.snapshot());
            for(int action:SOLUTIONS[lesson-36])assertEquals(FinalStationSession.Outcome.CHANGED,s.move(action));
            assertEquals(FinalStationSession.Outcome.COMPLETE,s.check());
            assertEquals(0,store.balance());
        }
    }
    @Test public void groupingCannotCreateOrLoseCrystals() throws Exception{
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        AdventureWorld.State motion=store.adventureState(37);
        new AdventureTest().walk(new AdventureWorld(37),motion);assertTrue(store.saveAdventureMotion(37,motion));
        FinalStationSession s=new FinalStationSession(store,37);
        assertEquals(FinalStationSession.Outcome.NO_CHANGE,s.move(1));
        for(int i=0;i<12;i++)assertEquals(FinalStationSession.Outcome.CHANGED,s.move(2));
        assertEquals(4,s.remainingOrValue());assertEquals(FinalStationSession.Outcome.NO_CHANGE,s.move(6));
        for(int i=0;i<4;i++)assertEquals(FinalStationSession.Outcome.CHANGED,s.move(4));
        assertEquals(0,s.remainingOrValue());assertEquals(FinalStationSession.Outcome.NO_CHANGE,s.move(0));
        assertFalse(s.canComplete());assertEquals(FinalStationSession.Outcome.CHANGED,s.move(3));
        int total=s.remainingOrValue();for(int n:s.snapshot())total+=n;assertEquals(124,total);
        int[] before=s.snapshot();disk.fail=true;
        assertEquals(FinalStationSession.Outcome.SAVE_FAILED,s.reset());assertArrayEquals(before,s.snapshot());
        disk.fail=false;assertEquals(FinalStationSession.Outcome.CHANGED,s.reset());assertEquals(124,s.remainingOrValue());
    }
}
