import java.util.*;

/**
 * Write a description of class Settings here.
 *
 * @author Codrin Axinte
 * @version 03/12/2019
 */
public class Settings
{  
    public static String PLAYLIST_EXT = ".mmp"; // Playlist file extension mmp = Music Museum Playlist
    public static String SETTINGS_EXT = ".mms"; // Settings file extension mms = Music Museum Settings
    
    protected boolean _isMute;
    protected boolean _shuffle;
    protected LoopType _repeat;
    protected double _volume;
    protected String _lastTrack;
    protected List<String> _libraryPaths = new ArrayList<String>();
    protected String _defaultPlaylistsPath;
    protected String _defaultSettingsPath;
 
    
    public Settings(){
        setDefaults();
    }
    
    public void setMute(boolean value) { _isMute = value;  }
    public void setShuffle(boolean value) { _shuffle = value; }
    public void setRepeat(LoopType value) { _repeat = value; }
    public void setVolume(double value) { _volume = value;}
    public void setLastTrack(String value) { _lastTrack = value;}
    public void setLibraryPaths(List<String> value) { _libraryPaths = value; }
        
    public double getVolume() { return _volume; }
    public boolean getShuffle() { return _shuffle; }
    public boolean getMute() { return _isMute; }
    public LoopType getRepepat() { return _repeat; }
    public List<String> getLibraryPaths() { return _libraryPaths; }
    
    public String getPlaylistsPath(){
        return _defaultPlaylistsPath;
    }
    
    public String getPlaylistsPath(String fileName){
        return _defaultPlaylistsPath + "\\" + fileName + PLAYLIST_EXT;
    }
    
    public String getSettingsPath(){
        return _defaultSettingsPath;
    }
    
    public String getSettingsPath(String fileName){
        return _defaultSettingsPath + "\\" + fileName + SETTINGS_EXT;
    }
    
    public void setDefaults(){
        _defaultPlaylistsPath = "playlists"; // the playlist directory inside the application root directory
        _defaultSettingsPath = ""; // the root directory of the application
        _isMute = false;
        _shuffle = false;
        _volume = 1.0;
        _repeat = LoopType.NO_REPEAT;        
    }
}
