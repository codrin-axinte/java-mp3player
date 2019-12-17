import javafx.util.Duration;
import javax.swing.*;
import java.awt.event.*;  
import java.io.*;
import java.util.*;
import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.imageio.ImageIO;
import javax.swing.Timer;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
/**
 * This class is responsible with handling the application's interface.
 *
 * @author Codrin Axinte
 * @version 16/11/2019
 */
public class GUI extends Application implements ActionListener, PlayerListener, ChangeListener
{          
    // Player Controls   
    JButton btnPlay;  
    JButton btnStop;
    JButton btnNext;
    JButton btnPrevious;
    JButton btnShuffle;
    JButton btnRepeat;
    JButton btnMute;
    JSlider _volumeSlider;
    JLabel _volumeLabel;
    
    // Search
    String _searchPlaceholder = "Search track...";
    JTextField textSearch;
    JButton btnSearch;
    
    //INFO
    JList _trackList; 
    JLabel _trackNameLabel;
    JSlider _timeSlider;
    JLabel _timerLabel;
    Timer _timer;
    int _timerRefreshRate = 1000; // milliseconds
    
    // Core    
    Saver _saver;
    Player _player;   
  
    
     @Override
    public void start (Stage stage) {
       _saver = new Saver(new SettingsFileDriver(), new PlaylistFileDriver());       
       // ArrayList<String> hardCodedList =  _saver.loadPlayList("");
       _player = new Player(_saver);
       _player.addListener(this);       
     
      ActionListener taskPerformer = new ActionListener() {
          public void actionPerformed(ActionEvent evt) {
              updateTimerValues();
          }
      };
      _timer = new Timer(_timerRefreshRate, taskPerformer);
       
       final SwingNode swingNode = new SwingNode();
       
       createSwingContent(swingNode, this);

       final StackPane pane = new StackPane();
       
       pane.getChildren().add(swingNode);
       
       stage.setTitle("Music Museum");
       stage.setScene(new Scene(pane, 900, 300));
       stage.show();
       
    }  
    
    private void createSwingContent(final SwingNode root, GUI listener) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {           
                
                // INFO PANEL
                _trackNameLabel = new JLabel("No Track");
                _timeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
                _timeSlider.addChangeListener(listener);    
                _timerLabel = new JLabel("00:00/00:00");

                btnPlay = getButton("Play", "play");               
                btnStop= getButton("Stop", "stop");
                btnPrevious = getButton("<", "previous");                
                btnNext = getButton(">", "next");
                
                JPanel infoPanel = new JPanel();               
                infoPanel.add(_trackNameLabel);
                infoPanel.add(_timeSlider);
                infoPanel.add(_timerLabel);
                infoPanel.add(btnPlay);  
                infoPanel.add(btnStop);
                infoPanel.add(btnPrevious);
                infoPanel.add(btnNext);
                
                btnMute = getButton("Mute", "mute");
                btnShuffle = getButton("Shuffle", "shuffle");
                btnRepeat = getButton("Repeat", "repeat");  

                
                // SETTINGS PANEL
                JPanel settingsPanel = new JPanel();
                settingsPanel.add(btnShuffle);
                settingsPanel.add(btnRepeat);
                settingsPanel.add(btnMute); 
                int volumeDefaultValue = 50;
                _volumeLabel = new JLabel("Volume: " + volumeDefaultValue);                
                _volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, volumeDefaultValue);
                _player.setVolume(volumeDefaultValue * 0.01);
                _volumeSlider.addChangeListener(listener);
                settingsPanel.add(_volumeLabel);
                settingsPanel.add(_volumeSlider);
                
                // CONTROLS PANEL
                JSplitPane controlsPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPanel, settingsPanel);
                //controlsPanel.add(sliderVolume);
           
                    
                // LEFT PANEL
                btnSearch = getButton("Search", "search");
                textSearch = new JTextField(_searchPlaceholder);
                textSearch.setColumns(12);  
                _trackList = new JList(_player.getTracksNames().toArray());              
                _trackList.setSelectedIndex(0);
                  MouseListener mouseListener = new MouseAdapter() {
                      public void mouseClicked(MouseEvent mouseEvent) {
                        JList list = (JList) mouseEvent.getSource();
                        if (mouseEvent.getClickCount() == 2) {
                          int index = list.locationToIndex(mouseEvent.getPoint());
                          if (index >= 0) {                            
                            _player.playTrack(index);
                            _player.play();
                            System.out.println("Double-clicked on: " + index);
                          }
                        }
                      }
                    };
                _trackList.addMouseListener(mouseListener);
                JPanel searchPanel = new JPanel();
                searchPanel.add(textSearch);
                searchPanel.add(btnSearch);
                
                JSplitPane leftPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
                leftPanel.add(searchPanel);
                leftPanel.add(_trackList);
                
                // Menu Panel
                JButton btnOpenFile = getButton("Open File", "open_file");
                JButton btnOpenPlaylist = getButton("Open Playlist", "open_playlist");
                JButton btnSavePlaylist = getButton("Save Playlist", "save_playlist");
                JButton btnClearPlaylist = getButton("Clear Playlist", "clear_playlist");
                JPanel menuBarPanel = new JPanel();
                menuBarPanel.add(btnOpenFile);
                menuBarPanel.add(btnOpenPlaylist);
                menuBarPanel.add(btnSavePlaylist);
                menuBarPanel.add(btnClearPlaylist);
                
                // RIGHT PANEL               
                JSplitPane rightPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, menuBarPanel, controlsPanel);
                
                // ROOT PANEL
                JSplitPane main = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);               
                                
                root.setContent(main);
            }
        });
    }
   
    
   
   
    @Override
    public void onPlayerUpdated(PlayerEvent e) {
        switch(e){
            case LOADED:
            updateTimerValues();
            _timeSlider.setVisible(_player.hasTrack());
            break;
            case READY:
            _trackNameLabel.setText(_player.getCurrentTrackName());
            _trackList.setSelectedIndex(_player.getCurrentTrack());
            _timeSlider.setVisible(_player.hasTrack());
            updateTimerValues();
            break;
            case PLAYING:
            _timer.start();
            btnPlay.setText("Pause");
            break;
            case PAUSED:
            _timer.stop();
            btnPlay.setText("Play");
            break;
            case STOPPED:
            _timer.stop();
            updateTimerValues();
            btnPlay.setText("Play");
            break;
            case RESTARTED:           
            _timer.restart();
            break;
            case ENDED:
            _timer.stop();
            if(!_player.isState(PlayerState.PLAYING)){               
               btnPlay.setText("Play");
            }            
            break;
            case PLAYLIST_CHANGED:
            _trackList.setListData(_player.getTracksNames().toArray());
            _trackList.setSelectedIndex(_player.getCurrentTrack());
            break;
        }       
    }
    
   public void stateChanged(ChangeEvent changeEvent) {
      Object source = changeEvent.getSource();
      if (!(source instanceof JSlider)) {
          return;
      }
        
      JSlider slider = (JSlider) source;
      if (slider == _timeSlider && slider.getValueIsAdjusting()) {
         Duration duration = _player.getTotalTime();
         Duration newDuration = duration.multiply(slider.getValue() / 100.0);
          
         _player.setTime(newDuration);
         updateTimerValues();
        System.out.println("Slider changed: " + slider.getValue() + " |  Duration: " + StringUtil.formatTime(newDuration, duration));
        return;
      }
    
      if(slider == _volumeSlider && slider.getValueIsAdjusting()){
          int rawValue = slider.getValue();
          double value = (double) rawValue * 0.01;
          // System.out.println("Volume: " + value); // debug value
          _volumeLabel.setText("Volume: " + rawValue);              
          _player.setVolume(value);
          return;
      }
   }
    
   public void actionPerformed(ActionEvent e) {
    String cmd = e.getActionCommand();
    if(cmd.equals("open_file")) {
        _player.openFile();
        return;
    }
    
    if(cmd.equals("open_playlist")) {
        _player.openPlayList();
        return;
    }
    
    if(cmd.equals("save_playlist")) {
        _player.savePlaylist();
        return;
    }
    
     if(cmd.equals("clear_playlist")) {
        _player.clearPlaylist();
        return;
    }
    
    if (cmd.equals("play")) {
        // If the player is already playing then pause it
        if(_player.isState(PlayerState.PLAYING)){
            _player.pause();
            btnPlay.setText("Play");           
            return;
        }
        // If the player is not in the playing state then start playing it
        if(_trackList.getSelectedIndex() != -1 && !_player.isState(PlayerState.PLAYING)){
            _player.playTrack(_trackList.getSelectedIndex());         
        }
        
        _player.play();
        btnPlay.setText("Pause");      
        return;
    }
    
    if (cmd.equals("stop")) {
        _player.stop();
        btnPlay.setText("Play"); // Reverting back the text to Play       
        return;
    } 
    
    if (cmd.equals("previous")) {
        _player.previous(); 
         return;
    }
    
    if (cmd.equals("next")) {
         _player.next();    
         return;
    }
    
    if(cmd.equals("mute")){
        _player.toggleMute();      
        btnMute.setText("Mute " + StringUtil.getBoolText(_player.getMute()));
        return;
    }
    
    if(cmd.equals("shuffle")){
        _player.toggleShuffle();        
        btnShuffle.setText("Shuffle " + StringUtil.getBoolText(_player.getShuffle()));
        return;
    }
    
    if(cmd.equals("repeat")){
        _player.cycleRepeat();
        btnRepeat.setText("Repeat " + _player.getRepeatName());
        return;
    }
    
    if(cmd.equals("search")){
      performSearch(textSearch.getText().trim());    
      return;
    }
   }

   private void updateTimerValues(){
       Duration totalDuration = _player.getTotalTime();
       Duration currentTime = _player.getTime();
       _timerLabel.setText(StringUtil.formatTime(currentTime, totalDuration));       
       if(totalDuration.greaterThan(Duration.ZERO) && !_timeSlider.getValueIsAdjusting()){
           double mills = currentTime.divide(totalDuration).toMillis();
           _timeSlider.setValue((int) (mills * 100.00));
       }
   }
   
    
    private JButton getButton(String label, String action){
        JButton button =  new JButton(label);                
        button.addActionListener(this);
        button.setActionCommand(action);

        return button;
    }
   
   private JButton getButton(String label, String action, String iconPath){
       try {
        BufferedImage buttonIcon = ImageIO.read(new File("icons\\" + iconPath));
        JButton button = new JButton(new ImageIcon(buttonIcon));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.addActionListener(this);
        button.setActionCommand(action);
        return button;
       } catch(Exception e) {
           e.printStackTrace();
       }
       
       // Defaults to a normal button
       return getButton(label, action);
   }
   
   private void performSearch(String query){
      // Check if the query equals the placeholder  
      if(query.equals(_searchPlaceholder)){
          return;
      }
      
      // If the query is empty, reset the data back to normal
      if(query.isEmpty()){
           _trackList.setListData(_player.getTracksNames().toArray());
           _trackList.setSelectedIndex(0);
           textSearch.setText(_searchPlaceholder);
           return;
      }
      
      // Perform the actual search and update the list view
      ArrayList<String> result = _player.search(query);       
      _trackList.setListData(result.toArray());      
      _trackList.setSelectedIndex(0);
   }
  
}
