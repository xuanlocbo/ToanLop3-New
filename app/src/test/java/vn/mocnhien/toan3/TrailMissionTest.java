package vn.mocnhien.toan3;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import org.json.JSONObject;

/** Constructive solutions use the very same public controls available to the child. */
public final class TrailMissionTest {
    static int[] play(TrailMission m,int... actions){
        int[] s=m.initial();
        for(int a:actions){s=m.move(s,a);assertTrue(m.valid(s));}
        return s;
    }
    static int[] solution(TrailMission m){
        int[] s=m.initial();
        switch(m.kind){
            case GROUP:{
                int stride=m.total>60?4:2;
                for(int b=0;b<m.bins;b++){
                    int remaining=m.target;
                    if(stride==4)while(remaining>=10){s=m.move(s,b*stride+2);remaining-=10;}
                    for(int j=0;j<remaining;j++)s=m.move(s,b*stride);
                }break;
            }
            case BUILD:{
                for(int mask=1;mask<(1<<m.actionCount());mask++){
                    int sum=0;for(int i=0;i<m.actionCount();i++)if((mask&(1<<i))!=0)sum+=m.tile(i);
                    if(sum!=m.target)continue;
                    for(int i=0;i<m.actionCount();i++)if((mask&(1<<i))!=0)s=m.move(s,i);
                    return s;
                }
                throw new AssertionError("No subset solution: "+m.lesson);
            }
            case POUR:
                return play(m,0,4);
            case TEMP:
                return play(m,3,2,2);
            case STEPS:
                return m.lesson==27?play(m,0):play(m,0,1);
            case GRID:
                return play(m,0,3,13,10);
        }
        return s;
    }
    static void complete(ProgressStore store,int lesson){
        if(store.trailComplete(lesson))return;
        TrailMission m=TrailMission.of(lesson);int[] s=solution(m);
        assertTrue(m.solved(s));String value=m.encode(s);
        assertTrue(store.saveTrailDraft(lesson,value));assertTrue(store.completeTrail(lesson,value));
    }
    @Test public void everyStationHasReachableSolutionAndRoundTrips(){
        Set<TrailMission.Kind> kinds=new HashSet<>();Set<String> titles=new HashSet<>();
        for(int l=26;l<=44;l++){
            TrailMission m=TrailMission.of(l);assertTrue(kinds.add(m.kind)||kinds.contains(m.kind));
            assertTrue(titles.add(m.title));assertFalse("initial "+l,m.solved(m.initial()));
            int[] s=solution(m);assertTrue("unsolved "+l+" "+Arrays.toString(s),m.solved(s));
            assertArrayEquals(s,m.read(m.encode(s)));
            assertArrayEquals(m.initial(),m.read("invalid"));
            for(int a=0;a<m.actionCount();a++)assertTrue(m.valid(m.move(m.initial(),a)));
        }
        assertEquals(19,titles.size());assertEquals(6,kinds.size());
    }
    @Test public void wrongAnswersAndInvalidActionsAreNotAccepted(){
        TrailMission group=TrailMission.of(26);
        assertFalse(group.solved(new int[]{6,6,5}));
        assertFalse(group.valid(new int[]{17,17,17}));
        assertArrayEquals(group.initial(),group.move(group.initial(),1));
        TrailMission build=TrailMission.of(31);
        assertFalse(build.valid(new int[]{1<<build.actionCount()}));
        int[] one=build.move(build.initial(),0);
        assertArrayEquals(build.initial(),build.move(one,0));
        TrailMission pour=TrailMission.of(32);
        int[] s=play(pour,0,4);assertArrayEquals(new int[]{200,300},s);assertTrue(pour.solved(s));
        assertFalse(pour.valid(new int[]{501,0}));
        TrailMission steps=TrailMission.of(38);
        assertFalse(steps.solved(play(steps,1,0)));assertTrue(steps.solved(play(steps,0,1)));
        assertFalse(steps.valid(new int[]{-1,0}));assertFalse(steps.solved(play(steps,0,1,4)));
        TrailMission grid=TrailMission.of(43);
        assertTrue(grid.solved(play(grid,0,3,13,10)));
        assertTrue(grid.solved(play(grid,6,16,19,9)));
        assertFalse(grid.solved(play(grid,0,13,3,10)));
        assertFalse(grid.solved(play(grid,0,2,12,10)));
        assertFalse(grid.valid(new int[]{0,0,3,13}));
        TrailMission temp=TrailMission.of(33);assertFalse(temp.valid(new int[]{51}));
    }
    @Test public void stationAndDeferredChallengeRespectExistingProgressAndRewards() throws Exception{
        NativeCurriculumTest fixture=new NativeCurriculumTest();
        Curriculum c=fixture.load();
        AdventureTest.Disk disk=new AdventureTest.Disk();
        ProgressStore store=new ProgressStore(c,disk);
        AdventureTest helper=new AdventureTest();
        int lesson=26;AdventureWorld w=new AdventureWorld(lesson);AdventureWorld.State s=store.adventureState(lesson);
        TrailMission m=TrailMission.of(lesson);
        assertFalse(store.saveTrailDraft(lesson,m.encode(m.initial())));
        helper.walk(w,s);assertTrue(store.saveAdventureMotion(lesson,s));
        assertFalse(store.advanceAdventure(lesson,0));assertFalse(store.skipAdventureBonus(lesson,0));
        JSONObject q=c.questions.get(AdventurePlan.questionId(lesson,0));
        assertFalse(store.submitAdventure(lesson,0,fixture.draft(q)).saved);
        String partial=m.encode(m.move(m.initial(),0));assertTrue(store.saveTrailDraft(lesson,partial));
        store=new ProgressStore(c,disk);assertEquals(partial,store.trailDraft(lesson));assertFalse(store.trailComplete(lesson));
        assertFalse(store.completeTrail(lesson,partial));
        String solved=m.encode(solution(m));
        assertFalse(store.completeTrail(lesson,solved));assertTrue(store.saveTrailDraft(lesson,solved));
        disk.fail=true;assertFalse(store.completeTrail(lesson,solved));assertFalse(store.trailComplete(lesson));
        disk.fail=false;assertTrue(store.completeTrail(lesson,solved));store=new ProgressStore(c,disk);
        assertTrue(store.trailComplete(lesson));assertEquals(0,store.balance());
        assertFalse(store.saveTrailDraft(lesson,partial));
        assertTrue(store.submitAdventure(lesson,0,fixture.draft(q)).correct);assertTrue(store.advanceAdventure(lesson,0));
        s=store.adventureState(lesson);helper.walk(w,s);assertTrue(store.saveAdventureMotion(lesson,s));
        q=c.questions.get(AdventurePlan.questionId(lesson,1));assertTrue(store.submitAdventure(lesson,1,fixture.draft(q)).correct);
        assertTrue(store.advanceAdventure(lesson,1));int balance=store.balance();
        for(int gate=2;gate<4;gate++){
            s=store.adventureState(lesson);helper.walk(w,s);assertTrue(store.saveAdventureMotion(lesson,s));
            assertTrue(store.saveAdventureDraft(lesson,gate,Json.put(new JSONObject(),"value","1")));
            disk.fail=true;assertFalse(store.skipAdventureBonus(lesson,gate));assertEquals(gate,store.adventureState(lesson).gate);
            disk.fail=false;assertTrue(store.skipAdventureBonus(lesson,gate));store=new ProgressStore(c,disk);
            assertFalse(store.done(AdventurePlan.questionId(lesson,gate)));assertEquals(balance,store.balance());
        }
        s=store.adventureState(lesson);helper.walk(w,s);assertTrue(store.saveAdventureMotion(lesson,s));assertTrue(store.adventureState(lesson).finished);
        assertTrue(store.resetAdventure(lesson));assertFalse(store.trailComplete(lesson));assertEquals("",store.trailDraft(lesson));assertEquals(balance,store.balance());
    }
}
