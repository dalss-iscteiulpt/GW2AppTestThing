import me.nithanim.gw2api.v2.GuildWars2Api;

public class GemsNotifier {
	private GuildWars2Api gw2api;
	
	private int OBJECTIVEGOLD = 25;
	private final int CONVERSION = 1000000;
	
	public GemsNotifier(){
		gw2api = new GuildWars2Api();
	}
	
	public double currentRatio(){
		return gw2api.commerce().exchange().coinsPerGem(CONVERSION).getCoinsPerGem();
	}
	
	public Ratio getRatio(){
		String coins = Integer.toString((int)currentRatio());
		Ratio currentRatio = new Ratio(Integer.parseInt(coins.length() < 2 ? coins : coins.substring(0, 2))
				,Integer.parseInt(coins.substring(Math.max(coins.length() - 2, 0))));
		return currentRatio;
	}
	
	public void notifyRatio() throws InterruptedException{
		while(true){
			Ratio currentRatio = getRatio();
			System.out.println("Approx 100 Gems: "+currentRatio);
			if(currentRatio.gold <= OBJECTIVEGOLD){
				System.out.println("Objective Reached");
			}
			Thread.sleep(2000);
		}
	}
	
	public static void main(String[] args) throws InterruptedException {
		GemsNotifier notifier = new GemsNotifier();
		notifier.notifyRatio();
	}
	
	
	
	public class Ratio{
		private int gold;
		private int silver;
		
		public Ratio(int gold, int silver ) {
			this.gold = gold;
			this.silver = silver;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return gold+ " gold "+silver+" silver ";
		}
		
	}
}


