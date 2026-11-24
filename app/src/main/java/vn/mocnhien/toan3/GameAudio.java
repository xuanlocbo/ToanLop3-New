package vn.mocnhien.toan3;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import java.io.*;
import java.util.*;

/** Foreground-only short audio; honors the existing sound setting and never plays background music. */
final class GameAudio {
    final SoundPool pool;
    final EnumMap<SoundPalette.Cue,Integer> ids=new EnumMap<>(SoundPalette.Cue.class);
    final Set<Integer> loaded=new HashSet<>();
    final EnumMap<SoundPalette.Cue,Long> last=new EnumMap<>(SoundPalette.Cue.class);
    final Set<Integer> streams=new LinkedHashSet<>();
    boolean active=true,enabled=true,released=false;
    GameAudio(Context c){
        pool=new SoundPool.Builder().setMaxStreams(2).setAudioAttributes(
            new AudioAttributes.Builder().setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION).build()).build();
        pool.setOnLoadCompleteListener((p,id,status)->{if(status==0&&!released)loaded.add(id);});
        File dir=new File(c.getCacheDir(),"cue-v1");if(!dir.exists())dir.mkdirs();
        for(SoundPalette.Cue cue:SoundPalette.Cue.values()){
            try{
                File f=new File(dir,cue.name()+".wav");
                if(!f.isFile()||f.length()!=44+SoundPalette.RATE*SoundPalette.durationMs(cue)/1000*2){
                    try(FileOutputStream o=new FileOutputStream(f)){o.write(SoundPalette.wave(cue));}
                }
                int id=pool.load(f.getAbsolutePath(),1);if(id!=0)ids.put(cue,id);
            }catch(Exception e){android.util.Log.w("MocNhien","Audio unavailable: "+cue,e);}
        }
    }
    void enabled(boolean b){enabled=b;if(!b)stop();}
    void foreground(boolean b){active=b;if(!b)stop();}
    void play(SoundPalette.Cue cue){
        if(released||!active||!enabled)return;
        Integer id=ids.get(cue);if(id==null||!loaded.contains(id))return;
        long now=SystemClock.uptimeMillis(),previous=last.getOrDefault(cue,0L);
        if(now-previous<100)return;
        last.put(cue,now);
        int stream=pool.play(id,.73f,.73f,1,0,1f);
        if(stream!=0){
            streams.add(stream);
            if(streams.size()>12){Iterator<Integer> it=streams.iterator();it.next();it.remove();}
        }
    }
    void stop(){if(released)return;for(int id:streams)pool.stop(id);streams.clear();pool.autoPause();}
    void release(){if(released)return;stop();released=true;pool.release();loaded.clear();}
}
