package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;
import java.util.List;

public class SharedMethods
{
    // PRIVATE ATTRIBUTES

    // PUBLIC ATTRIBUTES

    // PUBLIC STATIC ATTRIBUTES


    // CONSTRUCTORS


    // ENCAPSULATION SETTERS

    // ENCAPSULATION GETTERS


    // PRIVATE METHODS

    // PUBLIC METHODS

    // PUBLIC STATIC METHODS

    public static int randInt(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static List<NodeMask> getNodeTerritory(int p_nodeId,List<PathMask> p_pathList)
    {
        List<PathMask> connectedPaths = getNodeConnectedPaths(p_nodeId,p_pathList);
        return getNodeTerritory(connectedPaths,p_nodeId);
    }

    public static List<NodeMask> getNodeTerritory(List<PathMask> p_connectedPaths,int p_nodeId)
    { // This method is faster but it's error-prone
        List<NodeMask> r_nodeList = new ArrayList<>();
        for (int i=0;i!=p_connectedPaths.size();i++)
        {
            r_nodeList.add(p_connectedPaths.get(i).getOpposingNode(p_nodeId));
        }
        r_nodeList.add(NodeMask.findNodeById(p_nodeId));
        return r_nodeList;
    }

    public static List<PathMask> getNodeConnectedPaths(int p_nodeId,List<PathMask> p_pathList)
    {
        List<PathMask>  r_pathList = new ArrayList<>();

        for (int i=0;i!= p_pathList.size();i++)
        {
            if (p_pathList.get(i).includesNode(p_nodeId)){
                r_pathList.add(p_pathList.get(i));
            }
        }

        return r_pathList;
    }
}
