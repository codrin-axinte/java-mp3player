import java.util.*;


/**
 * Write a description of class Saver here.
 *
 * @author Codrin Axinte
 * @version 12/11/2019
 */
public class Saver implements danSaver
{    
    private Settings _settings;
    private SaverDriver<Settings> _settingsDriver;
    private SaverDriver<ArrayList<String>> _playlistDriver;
    private String _savePath;
        
    public Saver(SaverDriver<Settings> settingsDriver, SaverDriver<ArrayList<String>> playlistDriver){
        _settings = new Settings();
        _settingsDriver = settingsDriver;
        _playlistDriver = playlistDriver;
        _savePath = "data\\";
       
    }
    
    public Saver(SaverDriver driver, SaverDriver<ArrayList<String>> playlistDriver,String savePath){
        _settings = new Settings();
        _settingsDriver = driver;
        _playlistDriver = playlistDriver;
        _savePath = savePath;
    }

    //settings functionality - band B requirements
    public void saveSettings() {
        _settingsDriver.save(_savePath, _settings);        
    }
    
    public void loadSettings() {
        _settings = _settingsDriver.load(_savePath);
    }
    
     public Settings getSettings(){    
        return _settings;
    }
    
    
    /**
     * Syncs the settings in the cloud.
     */
    public void syncSettings(){}
    
    public boolean getMute() {return _settings.getMute();}
    public void setMute(boolean mute) { _settings.setMute(mute); }
    public double getVolume() { return _settings.getVolume();}
    public void setVolume(double volume) { _settings.setVolume(volume); }
    
    
    //playlist functionality - band C requirements
    public void savePlayList(ArrayList<String> filepaths, String filepath) {
        _playlistDriver.save(filepath, filepaths);
    }
    
    public ArrayList<String> loadPlayList(String filepath) { 
        if(filepath == null){
          return new ArrayList<String>();
        }
        
        if(filepath == ""){
            return getHardCodedPlayList();
        }
        
        return _playlistDriver.load(filepath);        
    }
    
    
    public ArrayList<String> getHardCodedPlayList() { 
       //String libraryPath = "C:\\Users\\user\\Music\\Music\\";
       ArrayList<String> playlist = new ArrayList<String> ();
       playlist.add("sounds\\Axel Thesleff - Bad Karma.mp3");
       return playlist;
    }
}
