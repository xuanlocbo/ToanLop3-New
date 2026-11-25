package vn.mocnhien.toan3;

import android.content.Context;
import android.media.*;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import java.io.*;

/** Quiet music only on the active game screen. Pause on audio focus loss or lifecycle pause. */
final class GameMusic {
    private final Context context;
    private final AudioManager manager;
    private final AudioAttributes attributes=new AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
    private final AudioManager.OnAudioFocusChangeListener listener=this::focusChanged;
    private AudioFocusRequest request;
    private MediaPlayer player;
    private boolean wanted,prepared,requested,released;
    GameMusic(Context c){
        context=c.getApplicationContext();
        manager=(AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
    }
    void desired(boolean enabled){
        if(released||wanted==enabled)return;
        wanted=enabled;
        if(!wanted){pause();abandon();return;}
        if(player==null)prepare();
        else if(prepared)startWithFocus();
    }
    private void prepare(){
        try{
            File file=new File(context.getCacheDir(),"mocnhien-original-loop-v1.wav");
            int size=44+2*MusicPalette.NOTE_SAMPLES*MusicPalette.MELODY.length;
            if(!file.isFile()||file.length()!=size){
                File tmp=new File(context.getCacheDir(),"mocnhien-original-loop-v1.tmp");
                try(FileOutputStream out=new FileOutputStream(tmp)){out.write(MusicPalette.wave());}
                if(!tmp.renameTo(file))throw new IOException("Cannot save music cache");
            }
            player=new MediaPlayer();
            player.setAudioAttributes(attributes);player.setDataSource(file.getAbsolutePath());
            player.setLooping(true);player.setVolume(.36f,.36f);
            player.setOnPreparedListener(p->{if(released||p!=player)return;prepared=true;if(wanted)startWithFocus();});
            player.setOnErrorListener((p,what,extra)->{clearPlayer();return true;});
            player.prepareAsync();
        }catch(Exception e){android.util.Log.w("MocNhien","Music unavailable",e);clearPlayer();}
    }
    private void startWithFocus(){
        if(!wanted||!prepared||released||manager==null)return;
        if(!requested){
            int result;
            if(Build.VERSION.SDK_INT>=26){
                request=new AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(attributes)
                    .setOnAudioFocusChangeListener(listener,new Handler(Looper.getMainLooper())).build();
                result=manager.requestAudioFocus(request);
            }else result=manager.requestAudioFocus(listener,AudioManager.STREAM_MUSIC,AudioManager.AUDIOFOCUS_GAIN);
            requested=result==AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        }
        if(requested)play(.36f);
    }
    private void focusChanged(int change){
        if(released||!wanted||!requested)return;
        if(change==AudioManager.AUDIOFOCUS_GAIN)play(.36f);
        else if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK){if(prepared&&player!=null)player.setVolume(.09f,.09f);}
        else if(change==AudioManager.AUDIOFOCUS_LOSS){pause();abandon();}
        else if(change==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)pause();
    }
    private void play(float volume){
        if(!wanted||!prepared||player==null||released)return;
        try{player.setVolume(volume,volume);if(!player.isPlaying())player.start();}
        catch(IllegalStateException e){clearPlayer();}
    }
    private void pause(){
        if(prepared&&player!=null)try{if(player.isPlaying())player.pause();}catch(IllegalStateException ignored){}
    }
    private void abandon(){
        if(!requested||manager==null)return;
        requested=false;
        if(Build.VERSION.SDK_INT>=26&&request!=null)manager.abandonAudioFocusRequest(request);
        else manager.abandonAudioFocus(listener);
    }
    private void clearPlayer(){
        abandon();prepared=false;
        MediaPlayer old=player;player=null;
        if(old!=null)try{old.release();}catch(RuntimeException ignored){}
    }
    void release(){if(released)return;wanted=false;released=true;clearPlayer();}
}
