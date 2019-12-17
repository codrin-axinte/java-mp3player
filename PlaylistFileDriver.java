import java.util.*;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

/**
 * Write a description of class PlaylistFileDriver here.
 *
 * @author Codrin Axinte
 * @version 09/12/2019
 */
public class PlaylistFileDriver implements SaverDriver<ArrayList<String>>
{
    public void save(String path, ArrayList<String> playlist) { 
        try {
            FileOutputStream t_outputStream = new FileOutputStream(path);
            XMLEncoder t_encoder = new XMLEncoder(t_outputStream);
            t_encoder.writeObject(playlist);
            t_encoder.close();
            t_outputStream.close();
        } 
        catch (Exception _ex) {
            System.out.println(_ex.getMessage());
        }         
    }
    
    public ArrayList<String> load(String path) { 
    
        try {
            FileInputStream input = new FileInputStream(path);
            XMLDecoder t_decode = new XMLDecoder(input);
            Object t_object = t_decode.readObject();
            ArrayList<String> t_temp = (ArrayList<String>) t_object;     
            t_decode.close();
            input.close();
            return t_temp;
        }
        catch (Exception _ex) {
            System.out.println(_ex.getMessage());
        }              
        
        return new ArrayList<String>();    
    }
}
