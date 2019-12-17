 import javafx.util.Duration;
import java.util.*;
import java.io.File;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.IOException; 
import java.util.Scanner; 
import javafx.scene.media.MediaView;
import java.util.Observable;  
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane; 

/**
 * Write a description of class Player here.
 *
 * @author Codrin Axinte
 * @version 10/11/2019
 */
public class Player implements danPlayer {

    private List<PlayerListener> _listeners = new ArrayList<PlayerListener>();
    private Saver _saver;
    private PlayerState _state = PlayerState.LOADING;
    /**
     * The current songs play list
     */
    private ArrayList<String> _tracks;
    private int _currentTrack;
    private File _trackFile;
    private MediaPlayer _mediaPlayer;  
    
    private Runnable _onPlaying;
    private Runnable _onPaused;
    private Runnable _onEnded;
    private Runnable _onStopped;
    private Runnable _onReady;
    private Runnable _onError;
    private danRandom _rng = new danRandom();
    
    private LoopType _repeat;
    private boolean _shuffle;
    
    
    public Player(Saver saver){
        _saver = saver;
         _tracks = new ArrayList<String>();
         init();
    }

    public Player(Saver saver, ArrayList<String> playlist){   
        _saver = saver;
         _tracks = playlist;
          if(_tracks.size() > 0){
             _trackFile = new File(playlist.get(0)); // Select the first track             
          }
          
         init();         
    }
    private void init(){
        _repeat = LoopType.NO_REPEAT;
        attachListeners();
        dispatch(PlayerEvent.LOADED);
    }

    private void attachListeners(){       
        _onPlaying = () -> { 
            _state = PlayerState.PLAYING;
            System.out.println("Track '" + getCurrentTrackName() + "' playing"); 
            dispatch(PlayerEvent.PLAYING);             
        };
        _onEnded = () -> { 
            System.out.println("Track '" + getCurrentTrackName() + "' ended.");           
            dispatch(PlayerEvent.ENDED);
            next();        
        };
        _onStopped = () -> { 
            _state = PlayerState.STOPPED;
            System.out.println("Track '" + getCurrentTrackName() + "' stopped."); 
            dispatch(PlayerEvent.STOPPED);
        };
        _onPaused = () -> { 
            _state = PlayerState.PAUSED;
            System.out.println("Track '"+ getCurrentTrackName() +  "' paused."); 
             dispatch(PlayerEvent.PAUSED);
        };
        _onReady = () -> { 
            _state = PlayerState.READY;
            System.out.println("Track '" + getCurrentTrackName() +  "' is ready.");
            dispatch(PlayerEvent.READY);
        };
        _onError = () -> { 
            _state = PlayerState.ERROR;
            //System.out.println("Something went wrong."); 
            debugPlayerError();
        };
       
        setNewPlayer(_trackFile);        
    }
    
    public void previous(){
       if(hasPrevious()){
           playTrack(_currentTrack - 1); // Play the previous track in the list
       }
       else if(_repeat == LoopType.REPEAT_ALL){
           playTrack(_tracks.size() - 1); // Play the last track in list
       }
    }
    
    public void next(){
       if(_repeat == LoopType.REPEAT_ONE){
           restart();
           return;
       }
       
       if(_shuffle && _tracks.size() > 2){
           int nextTrack = _currentTrack;
           do {
             nextTrack = _rng.getRandomInt(0, _tracks.size() - 1);
           } while(nextTrack == _currentTrack);
           
           playTrack(nextTrack);
           return;
       }
       
       if(hasNext()){
           playTrack(_currentTrack + 1); // Play the next track in the list
       }
       else if(_repeat == LoopType.REPEAT_ALL){
           playTrack(0); // Play the first track in the list
       }
    }
    
    private void debugPlayerError(){
        System.out.println("PLAYER ERROR -> [" + _mediaPlayer.getError().getType() +  "] " + _mediaPlayer.getError().getMessage());
    }
    
    public boolean hasPrevious() {
        return _currentTrack > 0;
    }
    
    public boolean hasNext(){
        return _currentTrack < _tracks.size() - 1;
    }
    
    public void playTrack(int trackNo) {
        if(selectTrack(trackNo) && trackNo != _currentTrack){
             play();            
        }  else {            
            System.out.println("Tried to play track " + trackNo);            
        }
    }
    
    public boolean selectTrack(int trackNo){
        if(trackNo >= _tracks.size() || trackNo < 0){
            return false;
        }
        
       _trackFile = new File(_tracks.get(trackNo));        
       setNewPlayer(_trackFile);
       _currentTrack = trackNo;
       
       return true;
    }
    
    private void setNewPlayer(File file){
        if(file == null){            
            return;   
        }
        Media media = new Media(_trackFile.toURI().toString());  
        _mediaPlayer = new MediaPlayer(media);        
        _mediaPlayer.setOnEndOfMedia(_onEnded);
        _mediaPlayer.setOnPlaying(_onPlaying);
        _mediaPlayer.setOnStopped(_onStopped);
        _mediaPlayer.setOnReady(_onReady);
        _mediaPlayer.setOnError(_onError);       
    }
    
    public MediaPlayer getMediaPlayer(){
        return _mediaPlayer;
    }
    
    /**
     * the very basics, band A requirements
     */
    public void play() 
    {     
     _mediaPlayer.play();
    }

    public void stop() {       
       _mediaPlayer.stop();
    }

    public void pause() {      
       _mediaPlayer.pause();
    }

    public void restart() {        
        setTime(_mediaPlayer.getStartTime());      
        dispatch(PlayerEvent.RESTARTED);
    }
    
    public boolean isPlayingTrack(int track){
        return _currentTrack == track;
    }
    
    public PlayerState getState(){
        return _state;
    }
    
    public boolean isState(PlayerState state){
        return _state == state;
    }
    
    public int getCurrentTrack(){
        return _currentTrack;
    }

    public boolean hasTrack() {
        return _trackFile != null;
    }
    
    //intermediate, band B requirements
    public String getCurrentTrackName() {
        if(_trackFile == null){
         return null;   
        }
        return stripExtension(_trackFile.getName());
    }

    public void toggleMute(){
        setMute(!getMute());
    }
    
    public void setMute(boolean mute) {
        _mediaPlayer.setMute(mute);
        dispatch(PlayerEvent.MUTE_CHANGED);
    }
    
    /**
     * The volume value must be between 0.0 and 1.0
     */
    public void setVolume(double volume) {
        if(_mediaPlayer == null){
            System.out.println("[setVolume] No media player is set.");
            return;
        }
        _mediaPlayer.setVolume(volume);
         dispatch(PlayerEvent.VOLUME_CHANGED);
    }

    public Duration getTime() {
        return _mediaPlayer.getCurrentTime();
    }

    public boolean getMute() {
        return _mediaPlayer.isMute();
    }

    public double getVolume() {
        return _mediaPlayer.getVolume();
    }

    //advanced, band C requirements
    public void setTime(Duration time) {
        _mediaPlayer.seek(time);
    }

    public Duration getTotalTime() {
        return _mediaPlayer.getTotalDuration();
    }

    public void openPlayList() {    
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Music Museum Playlist Files", "mmp");
        chooser.setFileFilter(filter);
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal == JFileChooser.APPROVE_OPTION) {
           String path = chooser.getSelectedFile().getAbsolutePath();
           _tracks =  _saver.loadPlayList(path);
           dispatch(PlayerEvent.PLAYLIST_CHANGED);
           System.out.println("You chose to open the playlist: " + path);
        }
    }
    
    public void savePlaylist(){
        if(_tracks.size() == 0){
            JOptionPane.showMessageDialog(null, "The tracklist is empty. There is nothing to save.", "Dialog", JOptionPane.WARNING_MESSAGE);
            return;
        }        
        
        
        JFileChooser chooser = new JFileChooser(); 
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Playlist Save Location");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);         
        // disable the "All files" option           
        chooser.setAcceptAllFileFilterUsed(false);
         
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) { 
          System.out.println("getCurrentDirectory(): " +  chooser.getCurrentDirectory());
          //System.out.println("getSelectedFile() : " +  chooser.getSelectedFile());
          String playlistName = JOptionPane.showInputDialog("Playlist Name");
          _saver.savePlayList(_tracks, chooser.getCurrentDirectory() + "\\" + playlistName + Settings.PLAYLIST_EXT);
          JOptionPane.showMessageDialog(null, "The playlist is saved", "Dialog", JOptionPane.INFORMATION_MESSAGE);
        }
        else {
          System.out.println("No Selection ");
        }
    }

    /**
     * 
     */
    public void openFile() {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Mp3 Files", "mp3");
        chooser.setFileFilter(filter);
        chooser.setMultiSelectionEnabled(true);
        chooser.setDialogTitle("Add Tracks");
        
        chooser.setCurrentDirectory(new File(System.getProperty("user.home")));
        int returnVal = chooser.showOpenDialog(null);
        if(returnVal != JFileChooser.APPROVE_OPTION) {
            return;       
        }        
        
        
        File[] files = chooser.getSelectedFiles();             
        System.out.println("You chose to open  " + files.length + " files");
        int tracksBefore = _tracks.size();
        // Add the files to the playlist        
        // TODO: Filter out the duplicates
        for(File file : files){ 
            file.isDirectory();
            _tracks.add(file.getAbsolutePath());
        }
    
        // If there is only one track in the playlist, this meaning the track that it was selected and added then select it
        // If this isn't the only one track, than will get the last one added which will be the one selected.
        if(_mediaPlayer == null || !isState(PlayerState.PLAYING)){
          int trackno = 0;   // Select the first track   
          if(tracksBefore == 0 && _tracks.size() > 1){
            // Select the first track from the newly added tracks
            trackno = _tracks.size() - 1;
          }           
          // Select the newly added track
          selectTrack(trackno);          
        }          
    
        dispatch(PlayerEvent.PLAYLIST_CHANGED);
        JOptionPane.showMessageDialog(null, "You have added " + files.length + " tracks", "Dialog", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearPlaylist(){
        stop();
        _tracks = new ArrayList<String>();
        _mediaPlayer = null;
        _trackFile = null;        
        dispatch(PlayerEvent.PLAYLIST_CHANGED);
    }
    
    public ArrayList<String> getPlayList() {
        return _tracks;
    }
    
    
    public void cycleRepeat(){
        switch(_repeat){
          case NO_REPEAT:
            setRepeat(LoopType.REPEAT_ALL);            
            break;
          case REPEAT_ALL:
            setRepeat(LoopType.REPEAT_ONE);
            break;
          case REPEAT_ONE:
            setRepeat(LoopType.NO_REPEAT);
             break;
        }
        
    }
    
    public String getRepeatName(){
        String state = "None";
         switch(_repeat){
          case NO_REPEAT:
               state = "None";           
               break;
          case REPEAT_ALL:
             state = "All";
             break;
          case REPEAT_ONE:
              state = "One";             
              break;
        }
        
        return state;
    }
    
    public void setRepeat(LoopType type){
        _repeat = type;
        dispatch(PlayerEvent.REPEAT_CHANGED);
    }
    
    public LoopType getRepeat(){
        return _repeat;
    }
    
    public void setShuffle(boolean mode){
        _shuffle = mode;
        dispatch(PlayerEvent.SHUFFLE_CHANGED);
    }
    
    public void toggleShuffle(){
        setShuffle(!_shuffle);
    }
    
    public boolean getShuffle(){
        return _shuffle;
    }
    
    public ArrayList<String> getTracksNames(){
         ArrayList<String> tracks = new ArrayList<String>();
        _tracks.forEach((String track) -> tracks.add(stripExtension(new File(track).getName())));
        
        return tracks;
    }
    
    protected String stripExtension(String fileName){
        if (fileName.indexOf(".") > 0) {
           return fileName.substring(0, fileName.lastIndexOf("."));
        }
        
        return fileName;          
    }
    
    public ArrayList<String> search(String query){
          System.out.println("Searching for '" + query +"'..." );
          ArrayList <String> found = new ArrayList<String>(); 
          ArrayList<String> data = getTracksNames();
           for (String string : data) {
               System.out.println("Comparing " + string);
               if(string.matches("(?i)(" + query +").*")) {
                   System.out.println(string + "matched " + query);
                   found.add(string);
               }
           }           
           return found;
    }
    public void dispatch(PlayerEvent e){
        _listeners.forEach((PlayerListener listener) -> { listener.onPlayerUpdated(e); });
    }
    
    public void addListener(PlayerListener listener) {
        _listeners.add(listener);
    }
}
