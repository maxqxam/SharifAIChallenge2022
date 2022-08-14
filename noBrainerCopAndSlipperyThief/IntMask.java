package ir.sharif.aic.hideandseek.ai;

import java.util.Vector;

public class IntMask
{
    // ATTRIBUTES
    int number;
    // CONSTRUCTORS
    IntMask(int p_number)
    {
        number = p_number;
    }


    // STATIC METHODS

    public static boolean getVectorInclusionState(Vector<IntMask> p_IntMaskVector, int number)
    {
        for (int i=0;i!=p_IntMaskVector.size();i++)
        {
            if (p_IntMaskVector.get(i).number== number)
            {
                return true;
            }
        }

        return false;
    }
}
