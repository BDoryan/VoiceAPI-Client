package doryanbessiere.voiceapi.client;

import java.io.IOException;

public class PlayThread extends Thread {

	private VoiceAPI voiceAPI;
	private byte buffer[] = new byte[10000];

	public boolean enable_speaker = false;

	public PlayThread(VoiceAPI voiceAPI) {
		this.voiceAPI = voiceAPI;
		this.enable_speaker = true;
	}

	@Override
	public void run() {
		try {
			while (this.voiceAPI.isOnline() && this.voiceAPI.getInput().read(buffer) != -1) {
				if (enable_speaker) {
					this.voiceAPI.speaker_dataline.write(buffer, 0, buffer.length);	
				}
			}

			this.voiceAPI.speaker_dataline.drain();
			this.voiceAPI.speaker_dataline.close();
		} catch (IOException e) {
			System.out.println("server closed.");
			System.exit(1);
		}
	}
}
