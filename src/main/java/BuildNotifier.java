import java.io.IOException;

import org.json.*;

import me.nithanim.gw2api.v2.GuildWars2Api;
import me.nithanim.gw2api.v2.api.build.Build;

public class BuildNotifier {
	private GuildWars2Api gw2api;
	private int CURRENTBUILD = 75528;
	private int MINUTELOOP = 5;
	
	public BuildNotifier(){
		gw2api = new GuildWars2Api();
	}
	
	public void runNotify() throws InterruptedException, IOException{
		while(true){
			int newBuild = gw2api.build().get().getId();
			if(CURRENTBUILD != newBuild){
				System.out.println("New Build Available: "+newBuild);
				Process process = Runtime.getRuntime().exec("H:\\Gaming\\Guild Wars 2\\Gw2-64.exe");
				break;
			} else {
				System.out.println("No New Build: "+CURRENTBUILD);
			}
			Thread.sleep(MINUTELOOP*60000);
		}
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		BuildNotifier notifier = new BuildNotifier();
		notifier.runNotify();
		
	}

}
