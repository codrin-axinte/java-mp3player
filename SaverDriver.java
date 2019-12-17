
/**
 * Write a description of interface SaverDriver here.
 *
 * @author Codrin Axinte
 * @version 09/12/2019
 */
public interface SaverDriver<T>
{
    
    public void save(String path, T settings);
    public T load(String path);
}
