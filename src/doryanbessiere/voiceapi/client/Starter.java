package doryanbessiere.voiceapi.client;

public class Starter {

	public static void main(String[] args) {
		VoiceAPI voiceAPI = new VoiceAPI("77.144.207.27", 500);
		voiceAPI.start();
	}
}
