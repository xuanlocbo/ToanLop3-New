package vn.mocnhien.toan3;

import java.io.ByteArrayOutputStream;

/** Original gentle instrumental loop, synthesized locally; no external assets. */
final class MusicPalette {
    static final int RATE=22050, NOTE_SAMPLES=8820;
    static final int[] MELODY={523,659,784,659,587,698,880,698,523,659,784,880,698,587,523,0};
    static final int[] BASS={262,294,262,349};
    static byte[] wave(){
        int n=NOTE_SAMPLES*MELODY.length;
        ByteArrayOutputStream out=new ByteArrayOutputStream(44+2*n);
        SoundPalette.ascii(out,"RIFF");SoundPalette.le(out,36+n*2,4);
        SoundPalette.ascii(out,"WAVEfmt ");SoundPalette.le(out,16,4);
        SoundPalette.le(out,1,2);SoundPalette.le(out,1,2);SoundPalette.le(out,RATE,4);
        SoundPalette.le(out,RATE*2,4);SoundPalette.le(out,2,2);SoundPalette.le(out,16,2);
        SoundPalette.ascii(out,"data");SoundPalette.le(out,n*2,4);
        for(int i=0;i<n;i++){
            int note=i/NOTE_SAMPLES, local=i%NOTE_SAMPLES, bassLocal=i%(NOTE_SAMPLES*4);
            double t=local/(double)RATE, u=local/(double)(NOTE_SAMPLES-1);
            double env=Math.min(1,u/.12)*Math.min(1,(1-u)/.25)*Math.exp(-1.6*u);
            double phase=2*Math.PI*MELODY[note]*t;
            double melody=MELODY[note]==0?0:.095*env*(Math.sin(phase)+.16*Math.sin(2*phase));
            double v=bassLocal/(double)(NOTE_SAMPLES*4-1);
            double bass=.034*Math.min(1,v/.08)*Math.min(1,(1-v)/.16)
                    *Math.sin(2*Math.PI*BASS[note/4]*bassLocal/RATE);
            // Every note and chord fades to zero at its boundaries; loop has no click.
            SoundPalette.le(out,(int)Math.round(32767*(melody+bass)),2);
        }
        return out.toByteArray();
    }
}
