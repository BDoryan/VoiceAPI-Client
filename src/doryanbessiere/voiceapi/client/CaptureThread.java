package doryanbessiere.voiceapi.client;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

class CaptureThread extends Thread {

	private VoiceAPI voiceAPI;
	private byte buffer[] = new byte[2048];

	public boolean enable_microphone = false;

	public CaptureThread(VoiceAPI voiceAPI) {
		this.voiceAPI = voiceAPI;
		this.enable_microphone = true;
	} 
	
	public double[] byte2Double(byte[] inData, boolean byteSwap) {
	    int j = 0, upper, lower;
	    int length = inData.length / 8;
	    double[] outData = new double[length];
	    if (!byteSwap)
	      for (int i = 0; i < length; i++) {
	        j = i * 8;
	        upper = (((inData[j] & 0xff) << 24)
	            + ((inData[j + 1] & 0xff) << 16)
	            + ((inData[j + 2] & 0xff) << 8) + ((inData[j + 3] & 0xff) << 0));
	        lower = (((inData[j + 4] & 0xff) << 24)
	            + ((inData[j + 5] & 0xff) << 16)
	            + ((inData[j + 6] & 0xff) << 8) + ((inData[j + 7] & 0xff) << 0));
	        outData[i] = Double.longBitsToDouble((((long) upper) << 32)
	            + (lower & 0xffffffffl));
	      }
	    else
	      for (int i = 0; i < length; i++) {
	        j = i * 8;
	        upper = (((inData[j + 7] & 0xff) << 24)
	            + ((inData[j + 6] & 0xff) << 16)
	            + ((inData[j + 5] & 0xff) << 8) + ((inData[j + 4] & 0xff) << 0));
	        lower = (((inData[j + 3] & 0xff) << 24)
	            + ((inData[j + 2] & 0xff) << 16)
	            + ((inData[j + 1] & 0xff) << 8) + ((inData[j] & 0xff) << 0));
	        outData[i] = Double.longBitsToDouble((((long) upper) << 32)
	            + (lower & 0xffffffffl));
	      }

	    return outData;
	  }
	
	public double volumeRMS(double[] raw) {
	    double sum = 0d;
	    if (raw.length==0) {
	        return sum;
	    } else {
	        for (int ii=0; ii<raw.length; ii++) {
	            sum += raw[ii];
	        }
	    }
	    double average = sum/raw.length;

	    double sumMeanSquare = 0d;
	    for (int ii=0; ii<raw.length; ii++) {
	        sumMeanSquare += Math.pow(raw[ii]-average,2d);
	    }
	    double averageMeanSquare = sumMeanSquare/raw.length;
	    double rootMeanSquare = Math.sqrt(averageMeanSquare);

	    return rootMeanSquare;
	}

	@Override
	public void run() {
		this.voiceAPI.byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			while (this.voiceAPI.isOnline()) {
				int cnt = this.voiceAPI.microphone_dataline.read(buffer, 0, buffer.length);

				if (enable_microphone) {
					this.voiceAPI.getOutput().write(buffer);

					if (cnt > 0) {
						this.voiceAPI.byteArrayOutputStream.write(buffer, 0, cnt);
					}
				}
			}
			this.voiceAPI.byteArrayOutputStream.close();
		} catch (Exception e) {
			System.out.println(e);
			System.exit(0);
		}
	}
}