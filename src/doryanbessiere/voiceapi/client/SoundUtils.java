package doryanbessiere.voiceapi.client;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SoundUtils {
	
	public static short[] encodeToSample(byte[] srcBuffer, int numBytes) {
	    byte[] tempBuffer = new byte[2];
	    int nSamples = numBytes / 2;        
	    short[] samples = new short[nSamples];  // 16-bit signed value

	    for (int i = 0; i < nSamples; i++) {
	        tempBuffer[0] = srcBuffer[2 * i];
	        tempBuffer[1] = srcBuffer[2 * i + 1];
	        samples[i] = bytesToShort(tempBuffer);
	    }

	    return samples;
	}

	public static short bytesToShort(byte [] buffer) {
	    ByteBuffer bb = ByteBuffer.allocate(2);
	    bb.order(ByteOrder.BIG_ENDIAN);
	    bb.put(buffer[0]);
	    bb.put(buffer[1]);
	    return bb.getShort(0);
	}
	
	public static double[] calculatePeakAndRms(short[] samples) {
	    double sumOfSampleSq = 0.0;    // sum of square of normalized samples.
	    double peakSample = 0.0;     // peak sample.

	    for (short sample : samples) {
	        double normSample = (double) sample / 32768;  // normalized the sample with maximum value.
	        sumOfSampleSq += (normSample * normSample);
	        if (Math.abs(sample) > peakSample) {
	            peakSample = Math.abs(sample);
	        }
	    }

	    double rms = 10*Math.log10(sumOfSampleSq / samples.length);
	    double peak = 20*Math.log10(peakSample / 32768);
	    
	    return new double[] {rms, peak};
	}
}
