package ldcr.BedwarsXP.Utils;

import io.github.bedwarsrel.BedwarsRel;
import io.github.bedwarsrel.game.Game;
import io.github.bedwarsrel.game.GameState;
import java.util.ArrayList;
import ldcr.BedwarsXP.XPShop.ShopReplacer;
import org.bukkit.command.CommandSender;

public class BedwarsGameUtils {
  public static boolean isGameExists(String bw) {
    return !BedwarsRel.getInstance().getGameManager().getGame(bw).equals(null);
  }
  
  public static boolean isGameRunning(String bw) {
    return BedwarsRel.getInstance().getGameManager().getGame(bw).getState().equals(GameState.RUNNING);
  }
  
  public static boolean isAnyBedwarsRunning() {
    ArrayList<Game> bw = BedwarsRel.getInstance().getGameManager().getGames();
    for (Game game : bw) {
      if (game.getState().equals(GameState.RUNNING))
        return true; 
    } 
    return false;
  }
  
  public static void replaceAllShop(CommandSender sender) {
    ArrayList<Game> bw = BedwarsRel.getInstance().getGameManager().getGames();
    for (Game game : bw) {
      if (game.getState().equals(GameState.RUNNING))
        ShopReplacer.replaceShop(game.getName(), sender); 
    } 
  }
}
