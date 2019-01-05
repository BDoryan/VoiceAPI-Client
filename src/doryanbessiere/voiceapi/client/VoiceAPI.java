package doryanbessiere.voiceapi.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

public class VoiceAPI extends Thread {
	
	private String adress;
	private int port;
	private Socket server;
	
	private BufferedInputStream input;
	private BufferedOutputStream out;
	
	public ByteArrayOutputStream byteArrayOutputStream;

	public TargetDataLine microphone_dataline;
	public SourceDataLine speaker_dataline;

	public CaptureThread captureThread;
	public PlayThread playThread;
	
	public VoiceAPI(String adress, int port) {
		this.adress = adress;
		this.port = port;
	}
	
	private AudioFormat getAudioFormat() {
		float sampleRate = 16000.0F;
		int sampleSizeInBits = 16;
		int channels = 2;
		boolean signed = true;
		boolean bigEndian = false;
		return new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
	}
	
	public boolean isOnline() {
		return this.server.isConnected();
	}
	
	public BufferedInputStream getInput() {
		return input;
	}

	public BufferedOutputStream getOutput() {
		return out;
	}
	
	@Override
	public void run() {
		try {
			this.server = new Socket(this.adress, this.port);
			System.out.println("Client connected on "+this.adress+":"+this.port);
			
			this.input = new BufferedInputStream(server.getInputStream());
			this.out = new BufferedOutputStream(server.getOutputStream());

	        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();
	        for (int cnt = 0; cnt < mixerInfo.length; cnt++) {
	            System.out.println(cnt+":"+mixerInfo[cnt].getName());
	        }
	        
	        AudioFormat audioFormat = getAudioFormat();
	        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
	        Mixer microphone_mixer = AudioSystem.getMixer(mixerInfo[3]); 

	        try {
				microphone_dataline = (TargetDataLine) microphone_mixer.getLine(dataLineInfo);
		        microphone_dataline.open(audioFormat);
		        microphone_dataline.start();

		        this.captureThread = new CaptureThread(this);
		        this.captureThread.start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				System.exit(0);
			}
	        
	        Mixer speaker_mixer = AudioSystem.getMixer(mixerInfo[1]); 

	        try {
	            DataLine.Info dataLineInfo1 = new DataLine.Info(SourceDataLine.class, audioFormat);
				speaker_dataline = (SourceDataLine) speaker_mixer.getLine(dataLineInfo1);
		        speaker_dataline.open(audioFormat);
		        speaker_dataline.start();

		        this.playThread = new PlayThread(this);
		        this.playThread.start();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
				System.exit(0);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
