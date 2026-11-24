package vn.mocnhien.toan3;

import org.junit.Test;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import static org.junit.Assert.*;

public class SoundPaletteTest {
    @Test public void eachCueIsDistinctShortValidAndNonClipping(){
        Set<Integer> fingerprints=new HashSet<>();
        for(SoundPalette.Cue cue:SoundPalette.Cue.values()){
            byte[] wave=SoundPalette.wave(cue);
            int samples=SoundPalette.RATE*SoundPalette.durationMs(cue)/1000;
            ByteBuffer b=ByteBuffer.wrap(wave).order(ByteOrder.LITTLE_ENDIAN);
            assertEquals("RIFF",new String(wave,0,4,StandardCharsets.US_ASCII));
            assertEquals("WAVEfmt ",new String(wave,8,8,StandardCharsets.US_ASCII));
            assertEquals(wave.length-8,b.getInt(4));
            assertEquals(16,b.getInt(16));
            assertEquals(1,b.getShort(20));
            assertEquals(1,b.getShort(22));
            assertEquals(SoundPalette.RATE,b.getInt(24));
            assertEquals(SoundPalette.RATE*2,b.getInt(28));
            assertEquals(2,b.getShort(32));
            assertEquals(16,b.getShort(34));
            assertEquals("data",new String(wave,36,4,StandardCharsets.US_ASCII));
            assertEquals(samples*2,b.getInt(40));
            assertEquals(44+samples*2,wave.length);
            assertTrue(SoundPalette.durationMs(cue)<=700);
            int peak=0;for(int i=0;i<samples;i++)peak=Math.max(peak,Math.abs((int)b.getShort(44+2*i)));
            assertTrue("silent "+cue,peak>100);
            assertTrue("too loud "+cue,peak<12000);
            assertEquals(0,b.getShort(44));
            assertTrue(Math.abs(b.getShort(wave.length-2))<100);
            assertArrayEquals(wave,SoundPalette.wave(cue));
            assertTrue(fingerprints.add(java.util.Arrays.hashCode(wave)));
        }
        assertEquals(7,fingerprints.size());
    }
}
