package me.ram.bedwarsitemaddon.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.zip.GZIPOutputStream;
import javax.net.ssl.HttpsURLConnection;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.ServicePriority;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class BStatsMetrics {
  public static final int B_STATS_VERSION = 1;
  
  private static final String URL = "https://bStats.org/submitData/bukkit";
  
  private final boolean enabled;
  
  private static boolean logFailedRequests;
  
  private static boolean logSentData;
  
  private static boolean logResponseStatusText;
  
  private static String serverUUID;
  
  private final Plugin plugin;
  
  static {
    if (System.getProperty("bstats.relocatecheck") == null || !System.getProperty("bstats.relocatecheck").equals("false")) {
      String defaultPackage = new String(
          new byte[] { 
            111, 114, 103, 46, 98, 115, 116, 97, 116, 115, 
            46, 98, 117, 107, 107, 105, 116 });
      String examplePackage = new String(new byte[] { 
            121, 111, 117, 114, 46, 112, 97, 99, 107, 97, 
            103, 101 });
      if (BStatsMetrics.class.getPackage().getName().equals(defaultPackage) || BStatsMetrics.class.getPackage().getName().equals(examplePackage))
        throw new IllegalStateException("bStats Metrics class has not been relocated correctly!"); 
    } 
  }
  
  private final List<CustomChart> charts = new ArrayList<>();
  
  public BStatsMetrics(Plugin plugin) {
    if (plugin == null)
      throw new IllegalArgumentException("Plugin cannot be null!"); 
    this.plugin = plugin;
    File bStatsFolder = new File(plugin.getDataFolder().getParentFile(), "bStats");
    File configFile = new File(bStatsFolder, "config.yml");
    YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);
    if (!config.isSet("serverUuid")) {
      config.addDefault("enabled", Boolean.valueOf(true));
      config.addDefault("serverUuid", UUID.randomUUID().toString());
      config.addDefault("logFailedRequests", Boolean.valueOf(false));
      config.addDefault("logSentData", Boolean.valueOf(false));
      config.addDefault("logResponseStatusText", Boolean.valueOf(false));
      config.options().header(
          "bStats collects some data for plugin authors like how many servers are using their plugins.\nTo honor their work, you should not disable it.\nThis has nearly no effect on the server performance!\nCheck out https://bStats.org/ to learn more :)")
        
        .copyDefaults(true);
      try {
        config.save(configFile);
      } catch (IOException iOException) {}
    } 
    this.enabled = config.getBoolean("enabled", true);
    serverUUID = config.getString("serverUuid");
    logFailedRequests = config.getBoolean("logFailedRequests", false);
    logSentData = config.getBoolean("logSentData", false);
    logResponseStatusText = config.getBoolean("logResponseStatusText", false);
    if (this.enabled) {
      boolean found = false;
      for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
        try {
          service.getField("B_STATS_VERSION");
          found = true;
          break;
        } catch (NoSuchFieldException noSuchFieldException) {}
      } 
      Bukkit.getServicesManager().register(BStatsMetrics.class, this, plugin, ServicePriority.Normal);
      if (!found)
        startSubmitting(); 
    } 
  }
  
  public boolean isEnabled() {
    return this.enabled;
  }
  
  public void addCustomChart(CustomChart chart) {
    if (chart == null)
      throw new IllegalArgumentException("Chart cannot be null!"); 
    this.charts.add(chart);
  }
  
  private void startSubmitting() {
    final Timer timer = new Timer(true);
    timer.scheduleAtFixedRate(new TimerTask() {
          public void run() {
            if (!BStatsMetrics.this.plugin.isEnabled()) {
              timer.cancel();
              return;
            } 
            Bukkit.getScheduler().runTask(BStatsMetrics.this.plugin, () -> BStatsMetrics.this.submitData());
          }
        },300000L, 1800000L);
  }
  
  public JSONObject getPluginData() {
    JSONObject data = new JSONObject();
    String pluginName = this.plugin.getDescription().getName();
    String pluginVersion = this.plugin.getDescription().getVersion();
    data.put("pluginName", pluginName);
    data.put("pluginVersion", pluginVersion);
    JSONArray customCharts = new JSONArray();
    for (CustomChart customChart : this.charts) {
      JSONObject chart = customChart.getRequestJsonObject();
      if (chart == null)
        continue; 
      customCharts.add(chart);
    } 
    data.put("customCharts", customCharts);
    return data;
  }
  
  private JSONObject getServerData() {
    int playerAmount;
    try {
      Method onlinePlayersMethod = Class.forName("org.bukkit.Server").getMethod("getOnlinePlayers");
      playerAmount = onlinePlayersMethod.getReturnType().equals(Collection.class) ? (
        (Collection)onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).size() : (
        (Player[])onlinePlayersMethod.invoke(Bukkit.getServer(), new Object[0])).length;
    } catch (Exception e) {
      playerAmount = Bukkit.getOnlinePlayers().size();
    } 
    int onlineMode = Bukkit.getOnlineMode() ? 1 : 0;
    String bukkitVersion = Bukkit.getVersion();
    String javaVersion = System.getProperty("java.version");
    String osName = System.getProperty("os.name");
    String osArch = System.getProperty("os.arch");
    String osVersion = System.getProperty("os.version");
    int coreCount = Runtime.getRuntime().availableProcessors();
    JSONObject data = new JSONObject();
    data.put("serverUUID", serverUUID);
    data.put("playerAmount", Integer.valueOf(playerAmount));
    data.put("onlineMode", Integer.valueOf(onlineMode));
    data.put("bukkitVersion", bukkitVersion);
    data.put("javaVersion", javaVersion);
    data.put("osName", osName);
    data.put("osArch", osArch);
    data.put("osVersion", osVersion);
    data.put("coreCount", Integer.valueOf(coreCount));
    return data;
  }
  
  private void submitData() {
    final JSONObject data = getServerData();
    JSONArray pluginData = new JSONArray();
    for (Class<?> service : Bukkit.getServicesManager().getKnownServices()) {
      try {
        service.getField("B_STATS_VERSION");
        for (RegisteredServiceProvider<?> provider : Bukkit.getServicesManager().getRegistrations(service)) {
          try {
            pluginData.add(provider.getService().getMethod("getPluginData", new Class[0]).invoke(provider.getProvider()));
          } catch (NullPointerException|NoSuchMethodException|IllegalAccessException|java.lang.reflect.InvocationTargetException nullPointerException) {}
        } 
      } catch (NoSuchFieldException noSuchFieldException) {}
    } 
    data.put("plugins", pluginData);
    (new Thread(() -> {
      try {
        BStatsMetrics.sendData(BStatsMetrics.this.plugin, data);
      } catch (Exception e) {
        if (BStatsMetrics.logFailedRequests)
          BStatsMetrics.this.plugin.getLogger().log(Level.WARNING, "Could not submit plugin stats of " + BStatsMetrics.this.plugin.getName(), e);
      }
    })).start();
  }
  
  private static void sendData(Plugin plugin, JSONObject data) throws Exception {
    if (data == null)
      throw new IllegalArgumentException("Data cannot be null!"); 
    if (Bukkit.isPrimaryThread())
      throw new IllegalAccessException("This method must not be called from the main thread!"); 
    if (logSentData)
      plugin.getLogger().info("Sending data to bStats: " + data.toString()); 
    HttpsURLConnection connection = (HttpsURLConnection)(new URL("https://bStats.org/submitData/bukkit")).openConnection();
    byte[] compressedData = compress(data.toString());
    connection.setRequestMethod("POST");
    connection.addRequestProperty("Accept", "application/json");
    connection.addRequestProperty("Connection", "close");
    connection.addRequestProperty("Content-Encoding", "gzip");
    connection.addRequestProperty("Content-Length", String.valueOf(compressedData.length));
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("User-Agent", "MC-Server/1");
    connection.setDoOutput(true);
    DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
    outputStream.write(compressedData);
    outputStream.flush();
    outputStream.close();
    InputStream inputStream = connection.getInputStream();
    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
    StringBuilder builder = new StringBuilder();
    String line;
    while ((line = bufferedReader.readLine()) != null)
      builder.append(line); 
    bufferedReader.close();
    if (logResponseStatusText)
      plugin.getLogger().info("Sent data to bStats and received response: " + builder.toString()); 
  }
  
  private static byte[] compress(String str) throws IOException {
    if (str == null)
      return null; 
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    GZIPOutputStream gzip = new GZIPOutputStream(outputStream);
    gzip.write(str.getBytes(StandardCharsets.UTF_8));
    gzip.close();
    return outputStream.toByteArray();
  }
  
  public static abstract class CustomChart {
    final String chartId;
    
    CustomChart(String chartId) {
      if (chartId == null || chartId.isEmpty())
        throw new IllegalArgumentException("ChartId cannot be null or empty!"); 
      this.chartId = chartId;
    }
    
    private JSONObject getRequestJsonObject() {
      JSONObject chart = new JSONObject();
      chart.put("chartId", this.chartId);
      try {
        JSONObject data = getChartData();
        if (data == null)
          return null; 
        chart.put("data", data);
      } catch (Throwable t) {
        if (BStatsMetrics.logFailedRequests)
          Bukkit.getLogger().log(Level.WARNING, "Failed to get data for custom chart with id " + this.chartId, t); 
        return null;
      } 
      return chart;
    }
    
    protected abstract JSONObject getChartData() throws Exception;
  }
  
  public static class SimplePie extends CustomChart {
    private final Callable<String> callable;
    
    public SimplePie(String chartId, Callable<String> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      String value = this.callable.call();
      if (value == null || value.isEmpty())
        return null; 
      data.put("value", value);
      return data;
    }
  }
  
  public static class AdvancedPie extends CustomChart {
    private final Callable<Map<String, Integer>> callable;
    
    public AdvancedPie(String chartId, Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      JSONObject values = new JSONObject();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty())
        return null; 
      boolean allSkipped = true;
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        if (entry.getValue().intValue() == 0)
          continue; 
        allSkipped = false;
        values.put(entry.getKey(), entry.getValue());
      } 
      if (allSkipped)
        return null; 
      data.put("values", values);
      return data;
    }
  }
  
  public static class DrilldownPie extends CustomChart {
    private final Callable<Map<String, Map<String, Integer>>> callable;
    
    public DrilldownPie(String chartId, Callable<Map<String, Map<String, Integer>>> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    public JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      JSONObject values = new JSONObject();
      Map<String, Map<String, Integer>> map = this.callable.call();
      if (map == null || map.isEmpty())
        return null; 
      boolean reallyAllSkipped = true;
      for (Map.Entry<String, Map<String, Integer>> entryValues : map.entrySet()) {
        JSONObject value = new JSONObject();
        boolean allSkipped = true;
        for (Map.Entry<String, Integer> valueEntry : (Iterable<Map.Entry<String, Integer>>)((Map)map.get(entryValues.getKey())).entrySet()) {
          value.put(valueEntry.getKey(), valueEntry.getValue());
          allSkipped = false;
        } 
        if (!allSkipped) {
          reallyAllSkipped = false;
          values.put(entryValues.getKey(), value);
        } 
      } 
      if (reallyAllSkipped)
        return null; 
      data.put("values", values);
      return data;
    }
  }
  
  public static class SingleLineChart extends CustomChart {
    private final Callable<Integer> callable;
    
    public SingleLineChart(String chartId, Callable<Integer> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      int value = this.callable.call().intValue();
      if (value == 0)
        return null; 
      data.put("value", Integer.valueOf(value));
      return data;
    }
  }
  
  public static class MultiLineChart extends CustomChart {
    private final Callable<Map<String, Integer>> callable;
    
    public MultiLineChart(String chartId, Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      JSONObject values = new JSONObject();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty())
        return null; 
      boolean allSkipped = true;
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        if (entry.getValue().intValue() == 0)
          continue; 
        allSkipped = false;
        values.put(entry.getKey(), entry.getValue());
      } 
      if (allSkipped)
        return null; 
      data.put("values", values);
      return data;
    }
  }
  
  public static class SimpleBarChart extends CustomChart {
    private final Callable<Map<String, Integer>> callable;
    
    public SimpleBarChart(String chartId, Callable<Map<String, Integer>> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      JSONObject values = new JSONObject();
      Map<String, Integer> map = this.callable.call();
      if (map == null || map.isEmpty())
        return null; 
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        JSONArray categoryValues = new JSONArray();
        categoryValues.add(entry.getValue());
        values.put(entry.getKey(), categoryValues);
      } 
      data.put("values", values);
      return data;
    }
  }
  
  public static class AdvancedBarChart extends CustomChart {
    private final Callable<Map<String, int[]>> callable;
    
    public AdvancedBarChart(String chartId, Callable<Map<String, int[]>> callable) {
      super(chartId);
      this.callable = callable;
    }
    
    protected JSONObject getChartData() throws Exception {
      JSONObject data = new JSONObject();
      JSONObject values = new JSONObject();
      Map<String, int[]> map = this.callable.call();
      if (map == null || map.isEmpty())
        return null; 
      boolean allSkipped = true;
      for (Map.Entry<String, int[]> entry : map.entrySet()) {
        if (entry.getValue().length == 0)
          continue; 
        allSkipped = false;
        JSONArray categoryValues = new JSONArray();
        byte b;
        int i;
        int[] arrayOfInt;
        for (i = (arrayOfInt = entry.getValue()).length, b = 0; b < i; ) {
          int categoryValue = arrayOfInt[b];
          categoryValues.add(Integer.valueOf(categoryValue));
          b++;
        } 
        values.put(entry.getKey(), categoryValues);
      } 
      if (allSkipped)
        return null; 
      data.put("values", values);
      return data;
    }
  }
}
