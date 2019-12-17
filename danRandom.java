import java.util.Random;

public class danRandom
{
    Random a_rand;
    
    public danRandom()
    {
        a_rand = new Random();
    }
    
    public int getRandomInt(int _min, int _max)
    {
        int l_range = _max - _min +1;
        int l_result = a_rand.nextInt(l_range);
        return l_result + _min;
    }
}
