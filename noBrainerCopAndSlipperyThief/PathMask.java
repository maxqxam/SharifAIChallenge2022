package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.Vector;

public class PathMask
{
    // ATTRIBUTES
    AIProto.Path path;
    // CONSTRUCTORS

    PathMask(AIProto.Path p_path)
    {
        path = p_path;
    }


    // NON-STATIC METHODS

    public String getString()
    {
        return "("+path.getFirstNodeId()+","+path.getSecondNodeId()+","+(int)path.getPrice()+")";
    }

    public boolean containsNode(int p_nodeId)
    {
        return (p_nodeId == path.getFirstNodeId()) | (p_nodeId == path.getSecondNodeId());
    }

    public int getOtherSideId(int p_currentSideId)
    {
        if (!containsNode(p_currentSideId)){Log.throwError(Enums.logErrors.InvalidParameterException.ordinal());}
        if (p_currentSideId==path.getFirstNodeId()){return path.getSecondNodeId();}
        return path.getFirstNodeId();
    }

    // STATIC METHODS

    public static String getVectorString(Vector<PathMask> p_pathMaskVector)
    {
        String r_string = "[";

        for (int i=0;i!=p_pathMaskVector.size();i++)
        {
            r_string+=p_pathMaskVector.get(i).getString();
            if (i!=p_pathMaskVector.size()-1){r_string+=",";}
        }
        r_string += "]";
        return r_string;
    }

    public static void printString(Vector<PathMask> p_pathMaskVector)
    {
        System.out.println(getVectorString(p_pathMaskVector));
    }

    public static Vector<PathMask> getVectorCopy(Vector<PathMask> p_PathMaskVector)
    {
        Vector<PathMask> r_PathMaskVector = new Vector<>();
        r_PathMaskVector.addAll(p_PathMaskVector);
        return r_PathMaskVector;
    }

}
