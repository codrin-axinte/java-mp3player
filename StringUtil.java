import java.util.*;
import javafx.util.Duration;
/**
 * Write a description of class StringUtil here.
 *
 * @author Codrin Axinte
 * @version 09/12/2019
 */
public class StringUtil
{
  
  public static String getBoolText(boolean state){
      return state ? "On" : "Off";
  }
   
   public  static String formatTime(Duration elapsed, Duration duration) {
        int intElapsed = (int) Math.floor(elapsed.toSeconds());
        int elapsedHours = intElapsed / (60 * 60);
        
        if (elapsedHours > 0) {
            intElapsed -= elapsedHours * 60 * 60;
        }
        
        int elapsedMinutes = intElapsed / 60;
        int elapsedSeconds = intElapsed - elapsedHours * 60 * 60 - elapsedMinutes * 60;
        
        if (duration.greaterThan(Duration.ZERO)) {
            int intDuration = (int) Math.floor(duration.toSeconds());
            int durationHours = intDuration / (60 * 60);
           
            if (durationHours > 0) {
                intDuration -= durationHours * 60 * 60;
            }
            
            int durationMinutes = intDuration / 60;
            int durationSeconds = intDuration - durationHours * 60 * 60 - durationMinutes * 60;
            
            if (durationHours > 0) {
                return String.format("%d:%02d:%02d/%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds, durationHours, durationMinutes, durationSeconds);
            } 
           
            return String.format("%02d:%02d/%02d:%02d", elapsedMinutes, elapsedSeconds, durationMinutes, durationSeconds);            
        } 
        
        if (elapsedHours > 0) {
            return String.format("%d:%02d:%02d", elapsedHours, elapsedMinutes, elapsedSeconds);
        } 
          
        return String.format("%02d:%02d", elapsedMinutes, elapsedSeconds);        
  }
}
