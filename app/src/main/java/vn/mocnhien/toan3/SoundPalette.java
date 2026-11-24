package vn.mocnhien.toan3;

import java.io.ByteArrayOutputStream;

/** Original, short synthesized sound cues: no recordings, network or external music. */
public final class SoundPalette {
    public enum Cue {CORRECT,TRY_AGAIN,JUMP,PETAL,GATE,FINISH,STATION}
    public static final int RATE=22050;
    public static int durationMs(Cue c){
        switch(c){
            case CORRECT:return 330;case TRY_AGAIN:return 210;case JUMP:return 115;
            case PETAL:return 150;case GATE:return 290;case FINISH:return 640;case STATION:return 360;
            default:throw new AssertionError();
        }
    }
    private static int[] notes(Cue c){
        switch(c){
            case CORRECT:return new int[]{523,659,784};
            case TRY_AGAIN:return new int[]{440,392};
            case JUMP:return new int[]{330,660};
            case PETAL:return new int[]{880,1175};
            case GATE:return new int[]{440,587,784};
            case FINISH:return new int[]{523,659,784,1047,784,1047};
            case STATION:return new int[]{392,523,659,880};
            default:throw new AssertionError();
        }
    }
    static void ascii(ByteArrayOutputStream o,String s){for(int i=0;i<s.length();i++)o.write(s.charAt(i));}
    static void le(ByteArrayOutputStream o,int n,int bytes){for(int i=0;i<bytes;i++)o.write(n>>>(i*8)&255);}
    public static byte[] wave(Cue cue){
        int[] notes=notes(cue);int n=RATE*durationMs(cue)/1000;
        ByteArrayOutputStream out=new ByteArrayOutputStream(44+n*2);
        ascii(out,"RIFF");le(out,36+n*2,4);ascii(out,"WAVEfmt ");le(out,16,4);le(out,1,2);le(out,1,2);
        le(out,RATE,4);le(out,RATE*2,4);le(out,2,2);le(out,16,2);ascii(out,"data");le(out,n*2,4);
        for(int i=0;i<n;i++){
            double pos=i*(double)notes.length/n;int k=Math.min(notes.length-1,(int)pos);double local=pos-k;
            double localTime=local*n/notes.length/RATE;
            double attack=Math.min(1,local/.12),release=Math.min(1,(1-local)/.28);
            double env=attack*release*Math.exp(-2.2*local);
            double phase=2*Math.PI*notes[k]*localTime;
            double wave=Math.sin(phase)+.19*Math.sin(phase*2)+.075*Math.sin(phase*3);
            // Keep all cues gentle and avoid clicks at sample boundaries.
            double volume=cue==Cue.TRY_AGAIN?.15:cue==Cue.PETAL?.17:.23;
            int pcm=(int)(32767*volume*env*wave);le(out,pcm,2);
        }
        return out.toByteArray();
    }
}
