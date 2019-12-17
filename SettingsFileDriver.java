import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.beans.XMLEncoder;
import java.beans.XMLDecoder;

/**
 * Write a description of class FileDriver here.
 *
 * @author Codrin Axinte
 * @version 09/12/2019
 */
public class SettingsFileDriver implements SaverDriver<Settings>
{
    public void save(String filename, Settings settings)
    {
        try 
        {
            FileOutputStream t_outputStream = new FileOutputStream(filename);
            XMLEncoder t_encoder = new XMLEncoder(t_outputStream);
            t_encoder.writeObject(settings);
            t_encoder.close();
            t_outputStream.close();
        } 
        catch (Exception _ex) 
        {
            System.out.println(_ex.getMessage());
        }         
    }
    
    public Settings load(String filename){
         try
        {
            FileInputStream input = new FileInputStream(filename);
            XMLDecoder t_decode = new XMLDecoder(input);
            Object t_object = t_decode.readObject();
            Settings t_temp = (Settings)t_object;     
            t_decode.close();
            input.close();
            return t_temp;
        }
        catch (Exception _ex) 
        {
            System.out.println(_ex.getMessage());
        }   
        
        return new Settings();
    }
}
