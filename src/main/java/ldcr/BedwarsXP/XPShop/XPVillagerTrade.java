package ldcr.BedwarsXP.XPShop;

import io.github.bedwarsrel.villager.VillagerTrade;
import ldcr.BedwarsXP.Utils.ResourceUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class XPVillagerTrade extends VillagerTrade {
  private int XP = 0;
  
  public XPVillagerTrade(VillagerTrade t) {
    super(t.getItem1(), t.getItem2(), t.getRewardItem());
    setXP(ResourceUtils.convertResToXP(t.getItem1()) + ResourceUtils.convertResToXP(t.getItem2()));
  }
  
  public XPVillagerTrade(ItemStack convert) {
    super(convert, null, convert);
    setXP(ResourceUtils.convertResToXP(convert));
  }
  
  public XPVillagerTrade(int xp, ItemStack RewardItem) {
    super(new ItemStack(Material.EXP_BOTTLE, xp), RewardItem);
    setXP(xp);
  }
  
  public void setXP(int xp) {
    this.XP = xp;
  }
  
  public int getXP() {
    return this.XP;
  }
}
