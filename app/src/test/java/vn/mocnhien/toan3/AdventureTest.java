package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;
import org.json.*;
import java.util.*;

/** Runs real game physics, curriculum grader and the shared snapshot transaction on the JVM. */
public final class AdventureTest {
    static final class Disk implements ProgressStore.Storage {
        String data;
        boolean fail=false;
        int writes=0;
        public String load(){return data;}
        public boolean commit(String value){if(fail)return false;data=value;writes++;return true;}
    }
    final NativeCurriculumTest fixtures=new NativeCurriculumTest();

    // Use exactly the controls a learner can use: hold right, jump on approaching an obstacle.
    void walk(AdventureWorld world,AdventureWorld.State s){
        boolean jumped=false;
        int startGate=s.gate;
        int oldFalls=s.falls;
        for(int tick=0;tick<600&&!s.pending&&!s.finished;tick++){
            assertTrue("invalid motion before tick "+world.lesson+":"+tick,s.valid());
            boolean jump=false;
            if(world.grounded(s)){
                for(AdventureWorld.Hazard h:world.hazards){
                    int d=h.left-s.x;
                    if(d>=0&&d<= (h.pit()?25:35)){jump=true;break;}
                }
            }
            jumped|=jump;
            world.step(s,1,jump);
            assertTrue("invalid motion after tick "+world.lesson+":"+tick,s.valid());
            assertEquals("unexpected gate advance",startGate,s.gate);
        }
        assertTrue("stage segment never reached its destination: "+world.lesson+":"+startGate,s.pending||s.finished);
        assertEquals("scripted jump route must be feasible without falls",oldFalls,s.falls);
        if(startGate<AdventurePlan.GATES)assertTrue("must have needed a jump",jumped);
    }

    @Test public void everyStageHasDistinctCurriculumGatesAndAReachableRoute() throws Exception {
        Curriculum c=fixtures.load();AdventurePlan.verify(c);
        Set<String> ids=new HashSet<>(), layouts=new HashSet<>();
        int gates=0;
        for(int lesson=26;lesson<=44;lesson++){
            AdventureWorld w=new AdventureWorld(lesson);AdventureWorld.State s=new AdventureWorld.State();
            StringBuilder layout=new StringBuilder();
            for(AdventureWorld.Hazard h:w.hazards)layout.append(h.left).append(':').append(h.right).append(':').append(h.height).append(';');
            assertTrue("duplicate geometry",layouts.add(layout.toString()));
            for(int gate=0;gate<4;gate++){
                assertEquals(gate,s.gate);walk(w,s);
                assertTrue(s.pending);assertFalse(s.finished);assertFalse(s.answered);
                assertTrue(ids.add(AdventurePlan.questionId(lesson,gate)));
                JSONObject q=c.questions.get(AdventurePlan.questionId(lesson,gate));
                assertFalse(NativeGrader.grade(q,new JSONObject()));
                assertTrue(NativeGrader.grade(q,fixtures.draft(q)));
                try{s.advance();fail("unanswered gate advanced");}catch(IllegalStateException expected){}
                s.answered=true;s.advance();gates++;
            }
            walk(w,s);assertTrue(s.finished);assertEquals(4,s.gate);
            assertEquals(0,s.falls);assertTrue(s.flowerCount()>0);assertTrue(s.flowerCount()<=12);
        }
        assertEquals(76,gates);assertEquals(76,ids.size());assertEquals(19,layouts.size());
    }

    @Test public void allGatesCommitDraftAnswerRewardsAndResumeAtomically() throws Exception {
        Curriculum c=fixtures.load();Disk disk=new Disk();ProgressStore store=new ProgressStore(c,disk);
        int expectedStars=0;
        for(int lesson=26;lesson<=44;lesson++){
            AdventureWorld w=new AdventureWorld(lesson);
            for(int gate=0;gate<4;gate++){
                AdventureWorld.State s=store.adventureState(lesson);
                assertEquals(gate,s.gate);
                // Resume midair during a feasible jump over the first pit.
                AdventureWorld.Hazard pit=w.hazards[gate*2];
                while(pit.left-s.x>25)w.step(s,1,false);
                w.step(s,1,true);
                assertTrue(s.y>0);
                assertTrue(store.saveAdventureMotion(lesson,s));
                store=new ProgressStore(c,disk);
                assertEquals(s.json().toString(),store.adventureState(lesson).json().toString());
                walk(w,s);
                assertTrue(store.saveAdventureMotion(lesson,s));
                if(gate==0)TrailMissionTest.complete(store,lesson);
                String id=AdventurePlan.questionId(lesson,gate);
                JSONObject q=c.questions.get(id);
                JSONObject original=Json.put(new JSONObject(),"originalLearningDraft",lesson*10+gate);
                assertTrue(store.saveDraft(id,original));
                JSONObject pending=Json.put(new JSONObject(),"showHint",true);
                assertTrue(store.saveAdventureDraft(lesson,gate,pending));
                store=new ProgressStore(c,disk);
                assertTrue(store.adventureDraft(lesson,gate).optBoolean("showHint"));
                assertEquals(original.toString(),store.draft(id).toString());
                assertFalse(store.advanceAdventure(lesson,gate));
                ProgressStore.Result wrong=store.submitAdventure(lesson,gate,new JSONObject());
                assertTrue(wrong.saved);assertFalse(wrong.correct);assertEquals(0,wrong.earned);
                assertTrue(store.adventureState(lesson).pending);assertFalse(store.adventureState(lesson).answered);
                assertTrue(store.review(id));assertEquals(expectedStars,store.balance());
                // A disk-write failure must neither grant the reward nor mark the gate answered.
                disk.fail=true;
                ProgressStore.Result failed=store.submitAdventure(lesson,gate,fixtures.draft(q));
                assertFalse(failed.saved);assertEquals(expectedStars,store.balance());assertFalse(store.adventureState(lesson).answered);
                disk.fail=false;store=new ProgressStore(c,disk);
                assertFalse(store.adventureState(lesson).answered);
                ProgressStore.Result right=store.submitAdventure(lesson,gate,fixtures.draft(q));
                assertTrue("correct gate rejected "+id,right.saved&&right.correct);
                int earned=gate<2?10:gate==2?80:220;
                assertEquals(earned,right.earned);expectedStars+=earned;
                assertEquals(expectedStars,store.balance());assertTrue(store.done(id));assertFalse(store.review(id));
                store=new ProgressStore(c,disk);
                assertEquals("correct",store.adventureDraft(lesson,gate).optString("status"));
                assertTrue(store.adventureState(lesson).answered);
                assertEquals(original.toString(),store.draft(id).toString());
                assertFalse(store.submitAdventure(lesson,gate,fixtures.draft(q)).saved);
                assertFalse(store.saveAdventureDraft(lesson,gate,new JSONObject()));
                disk.fail=true;assertFalse(store.advanceAdventure(lesson,gate));assertEquals(gate,store.adventureState(lesson).gate);
                disk.fail=false;assertTrue(store.advanceAdventure(lesson,gate));
                assertFalse(store.advanceAdventure(lesson,gate));
                assertFalse("stale motion cannot roll back an open gate",store.saveAdventureMotion(lesson,s));
                assertEquals(0,store.adventureDraft(lesson,gate).length());
            }
            AdventureWorld.State finish=store.adventureState(lesson);walk(w,finish);assertTrue(finish.finished);
            assertTrue(store.saveAdventureMotion(lesson,finish));store=new ProgressStore(c,disk);
            assertTrue(store.adventureState(lesson).finished);
            int flowers=finish.flowerCount();assertEquals(flowers,store.adventureBest(lesson));
            assertTrue(store.resetAdventure(lesson));assertEquals(0,store.adventureState(lesson).gate);
            assertEquals(flowers,store.adventureBest(lesson));assertEquals(expectedStars,store.balance());
            // Replaying an entire stage still requires its own answers, but adds no stars.
            for(int gate=0;gate<4;gate++){
                AdventureWorld.State replay=store.adventureState(lesson);walk(w,replay);
                assertTrue(store.saveAdventureMotion(lesson,replay));assertFalse(store.adventureState(lesson).answered);
                if(gate==0)TrailMissionTest.complete(store,lesson);
                JSONObject q=c.questions.get(AdventurePlan.questionId(lesson,gate));
                ProgressStore.Result right=store.submitAdventure(lesson,gate,fixtures.draft(q));
                assertTrue(right.saved&&right.correct);assertEquals(0,right.earned);
                assertTrue(store.advanceAdventure(lesson,gate));
            }
            AdventureWorld.State replayFinish=store.adventureState(lesson);walk(w,replayFinish);
            assertTrue(store.saveAdventureMotion(lesson,replayFinish));assertEquals(expectedStars,store.balance());
            assertTrue(store.adventureBest(lesson)>=flowers);
        }
        assertEquals(19*320,store.balance());
        assertEquals(19*320,new ProgressStore(c,disk).balance());
    }

    @Test public void fallingAndCollisionReturnToCheckpointAndKeepFlowers(){
        for(int lesson=26;lesson<=44;lesson++){
            AdventureWorld w=new AdventureWorld(lesson);AdventureWorld.State s=new AdventureWorld.State();
            s.petals=1;
            for(int i=0;i<200&&s.falls==0;i++)w.step(s,1,false);
            assertEquals("walk into pit must fall",1,s.falls);assertEquals(40,s.x);assertEquals(0,s.y);assertTrue((s.petals&1)!=0);
            AdventureWorld.Hazard block=w.hazards[1];
            s.x=block.left-AdventureWorld.HALF-1;
            w.step(s,1,false);
            assertEquals("walk into block must return to checkpoint",2,s.falls);assertEquals(40,s.x);assertTrue((s.petals&1)!=0);
            AdventureWorld.State saved=AdventureWorld.State.read(s.json());
            assertEquals(s.json().toString(),saved.json().toString());
        }
    }

    @Test public void alreadyLearnedQuestionsNeverPayAgainAndOldSnapshotsRemainCompatible() throws Exception {
        Curriculum c=fixtures.load();Disk disk=new Disk();
        ProgressStore store=new ProgressStore(c,disk);
        String id=AdventurePlan.questionId(26,0);
        JSONObject q=c.questions.get(id);
        assertEquals(10,store.submit(id,fixtures.draft(q)).earned);
        // Simulate an installed part-7 snapshot with no game keys.
        JSONObject old=new JSONObject(disk.data);old.remove("adventures");old.remove("adventureBest");disk.data=old.toString();
        store=new ProgressStore(c,disk);assertEquals(10,store.balance());assertEquals(40,store.adventureState(26).x);
        AdventureWorld w=new AdventureWorld(26);AdventureWorld.State s=store.adventureState(26);walk(w,s);
        assertTrue(store.saveAdventureMotion(26,s));
        TrailMissionTest.complete(store,26);
        ProgressStore.Result replay=store.submitAdventure(26,0,fixtures.draft(q));
        assertTrue(replay.saved&&replay.correct);assertEquals(0,replay.earned);assertEquals(10,store.balance());
        assertEquals("correct",store.draft(id).optString("status"));
        assertTrue(store.advanceAdventure(26,0));assertEquals(1,store.adventureState(26).gate);
    }

    @Test public void malformedAndStaleMotionCannotSkipGates() throws Exception {
        Curriculum c=fixtures.load();Disk disk=new Disk();ProgressStore store=new ProgressStore(c,disk);
        AdventureWorld.State s=store.adventureState(26);
        AdventureWorld.State invalid=s.copy();invalid.gate=1;invalid.x=AdventureWorld.checkpoint(1);
        assertFalse(store.saveAdventureMotion(26,invalid));
        invalid=s.copy();invalid.answered=true;assertFalse(store.saveAdventureMotion(26,invalid));
        invalid=s.copy();invalid.petals=1<<20;assertFalse(store.saveAdventureMotion(26,invalid));
        invalid=s.copy();invalid.x=AdventureWorld.FINISH;assertFalse(store.saveAdventureMotion(26,invalid));
        assertFalse(store.advanceAdventure(26,0));assertFalse(store.submitAdventure(26,0,new JSONObject()).saved);
        assertEquals(40,store.adventureState(26).x);assertEquals(0,store.balance());
    }
}
