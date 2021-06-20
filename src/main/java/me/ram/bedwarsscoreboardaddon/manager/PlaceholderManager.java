package me.ram.bedwarsscoreboardaddon.manager;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {
  private final Map<String, String> GamePlaceholder = new HashMap<>();
  
  private final Map<String, Map<String, String>> TeamPlaceholder = new HashMap<>();
  
  private final Map<String, Map<String, String>> PlayerPlaceholder = new HashMap<>();
  
  public void setGamePlaceholder(String placeholder, String info) {
    this.GamePlaceholder.put(placeholder, info);
  }
  
  public void removeGamePlaceholder(String placeholder) {
    this.GamePlaceholder.remove(placeholder);
  }
  
  public Map<String, String> getGamePlaceholder() {
    return this.GamePlaceholder;
  }
  
  public void setTeamPlaceholder(String team, String placeholder, String info) {
    Map<String, String> placeholders = this.TeamPlaceholder.getOrDefault(team, new HashMap<>());
    placeholders.put(placeholder, info);
    this.TeamPlaceholder.put(team, placeholders);
  }
  
  public void removeTeamPlaceholder(String team, String placeholder) {
    Map<String, String> placeholders = this.TeamPlaceholder.getOrDefault(team, new HashMap<>());
    placeholders.remove(placeholder);
    this.TeamPlaceholder.put(placeholder, placeholders);
  }
  
  public Map<String, String> getTeamPlaceholder(String team) {
    return this.TeamPlaceholder.getOrDefault(team, new HashMap<>());
  }
  
  public Map<String, Map<String, String>> getTeamPlaceholders() {
    return this.TeamPlaceholder;
  }
  
  public void setPlayerPlaceholder(String player, String placeholder, String info) {
    Map<String, String> placeholders = this.TeamPlaceholder.getOrDefault(player, new HashMap<>());
    placeholders.put(placeholder, info);
    this.PlayerPlaceholder.put(player, placeholders);
  }
  
  public void removePlayerPlaceholder(String player, String placeholder) {
    Map<String, String> placeholders = this.TeamPlaceholder.getOrDefault(player, new HashMap<>());
    placeholders.remove(placeholder);
    this.PlayerPlaceholder.put(placeholder, placeholders);
  }
  
  public Map<String, String> getPlayerPlaceholder(String player) {
    return this.PlayerPlaceholder.getOrDefault(player, new HashMap<>());
  }
  
  public Map<String, Map<String, String>> getPlayerPlaceholders() {
    return this.PlayerPlaceholder;
  }
}
