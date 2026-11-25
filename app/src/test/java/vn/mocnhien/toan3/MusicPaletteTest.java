package vn.mocnhien.toan3;
import java.nio.*;
import org.junit.Test;
import static org.junit.Assert.*;
public final class MusicPaletteTest {
    @Test public void deterministicQuietWaveWithSeamlessBoundaries(){
        byte[] wave=MusicPalette.wave();
        ByteBuffer b=ByteBuffer.wrap(wave).order(ByteOrder.LITTLE_ENDIAN);
        int count=MusicPalette.NOTE_SAMPLES*MusicPalette.MELODY.length;
        assertEquals(44+count*2,wave.length);
        assertEquals(wave.length-8,b.getInt(4));
        assertEquals(MusicPalette.RATE,b.getInt(24));
        assertEquals(count*2,b.getInt(40));
        int peak=0;
        for(int i=0;i<count;i++)peak=Math.max(peak,Math.abs((int)b.getShort(44+2*i)));
        assertTrue(peak>300&&peak<6000);
        assertEquals(0,b.getShort(44));assertEquals(0,b.getShort(wave.length-2));
        assertArrayEquals(wave,MusicPalette.wave());
    }
}
