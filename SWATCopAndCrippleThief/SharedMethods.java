package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;

public class SharedMethods
{

    public static int randInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static void addNewElements(ArrayList<Integer> paste,ArrayList<Integer> copy)
    {
        for (int i=0;i!=copy.size();i++)
        {
            if (!paste.contains(copy.get(i)))
            {
                paste.add(copy.get(i));
            }
        }
    }



}
