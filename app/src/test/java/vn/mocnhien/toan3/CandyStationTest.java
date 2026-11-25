package vn.mocnhien.toan3;

import org.junit.Test;
import org.json.JSONObject;
import java.util.Arrays;
import static org.junit.Assert.*;

public final class CandyStationTest {
    @Test public void allFourStationsUseCommittedMovesAndKeepRewardAuthority() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        AdventureTest helper=new AdventureTest();
        for(int lesson=26;lesson<=29;lesson++) {
            assertTrue(CandyStationSession.supports(lesson));
            assertFalse(CandyStationSession.objective(lesson).isEmpty());
            AdventureWorld.State motion=store.adventureState(lesson);
            helper.walk(new AdventureWorld(lesson),motion);
            assertTrue(store.saveAdventureMotion(lesson,motion));
            CandyStationSession s=new CandyStationSession(store,lesson);
            TrailMission m=s.mission;
            assertEquals(CandyStationSession.Outcome.NOT_READY,s.check());
            assertFalse(store.trailComplete(lesson));
            int[] initial=s.snapshot();
            int[] external=s.snapshot();external[0]=10000;
            assertArrayEquals(initial,s.snapshot());
            int action=0;
            disk.fail=true;
            assertEquals(CandyStationSession.Outcome.SAVE_FAILED,s.move(action));
            assertArrayEquals(initial,s.snapshot());
            disk.fail=false;
            assertEquals(CandyStationSession.Outcome.CHANGED,s.move(action));
            String saved=m.encode(s.snapshot());
            store=new ProgressStore(bank,disk);
            s=new CandyStationSession(store,lesson);
            assertEquals(saved,m.encode(s.snapshot()));
            assertEquals(CandyStationSession.Outcome.CHANGED,s.reset());
            assertArrayEquals(initial,s.snapshot());
            if(m.kind==TrailMission.Kind.GROUP) {
                for(int i=0;i<m.bins;i++)for(int n=0;n<m.target;n++)
                    assertEquals(CandyStationSession.Outcome.CHANGED,s.move(i*2));
                assertEquals(m.total%m.bins,s.remainingOrValue());
            } else {
                assertEquals(CandyStationSession.Outcome.CHANGED,s.move(0));
                if(lesson==28)assertEquals(CandyStationSession.Outcome.CHANGED,s.move(1));
            }
            assertTrue(s.canComplete());
            disk.fail=true;
            assertEquals(CandyStationSession.Outcome.SAVE_FAILED,s.check());
            assertFalse(store.trailComplete(lesson));
            disk.fail=false;
            int before=store.balance();
            assertEquals(CandyStationSession.Outcome.COMPLETE,s.check());
            assertEquals(CandyStationSession.Outcome.NO_CHANGE,s.check());
            assertEquals(before,store.balance());
            assertTrue(store.trailComplete(lesson));
            assertTrue(store.adventureState(lesson).pending);
            assertFalse(store.adventureState(lesson).answered);
            String id=AdventurePlan.questionId(lesson,0);
            assertFalse(store.done(id));
            store=new ProgressStore(bank,disk);
            assertTrue(store.trailComplete(lesson));
            assertEquals(before,store.balance());
        }
        for(int l=30;l<=44;l++)assertFalse(CandyStationSession.supports(l));
    }

    @Test public void wrongOperationAndUndoNeverCompleteStation() throws Exception {
        Curriculum bank=new NativeCurriculumTest().load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(bank,disk);
        AdventureWorld.State state=store.adventureState(27);
        new AdventureTest().walk(new AdventureWorld(27),state);
        assertTrue(store.saveAdventureMotion(27,state));
        CandyStationSession s=new CandyStationSession(store,27);
        assertEquals(CandyStationSession.Outcome.CHANGED,s.move(1));
        assertEquals(32,s.remainingOrValue());
        assertEquals(CandyStationSession.Outcome.NOT_READY,s.check());
        assertEquals(CandyStationSession.Outcome.CHANGED,s.move(s.mission.actionCount()-1));
        assertEquals(36,s.remainingOrValue());
        assertEquals(CandyStationSession.Outcome.CHANGED,s.move(0));
        assertEquals(9,s.remainingOrValue());
        assertEquals(CandyStationSession.Outcome.COMPLETE,s.check());
        assertEquals(0,store.balance());
    }
}
