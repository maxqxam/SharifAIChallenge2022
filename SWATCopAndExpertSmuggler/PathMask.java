package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.ArrayList;
import java.util.List;

public class PathMask
{
    // PRIVATE ATTRIBUTES


    // PUBLIC ATTRIBUTES
    public NodeMask firstNode;
    public NodeMask secondNode;
    public final double price;
    public final int id;

    // PUBLIC STATIC ATTRIBUTES
    public static List<PathMask> everyPath;


    // CONSTRUCTORS

    PathMask(AIProto.Path p_path,List<NodeMask> p_everyNode)
    {   // This Path automatically gets updated when it's corresponding
        // instances change in p_everyNode parameter
        firstNode = NodeMask.findNodeById(p_everyNode,p_path.getFirstNodeId());
        secondNode = NodeMask.findNodeById(p_everyNode,p_path.getSecondNodeId());
        id = p_path.getId();
        price = p_path.getPrice();
    }

    // ENCAPSULATION SETTERS

    // ENCAPSULATION GETTERS


    // PRIVATE METHODS

    // PUBLIC METHODS

    public boolean includesNode(int p_nodeId)
    {
        return (p_nodeId == firstNode.nodeId) || (p_nodeId == secondNode.nodeId);
    }

    public NodeMask getOpposingNode(int p_nodeId)
    {
        if (p_nodeId==firstNode.nodeId){return secondNode;}
        else if (p_nodeId==secondNode.nodeId){return firstNode;}
        else {throw new Error(Enums.Errors.NodeNotPartOfException.text);}
    }
    // PUBLIC STATIC METHODS

    public static void fillEveryPath(List<AIProto.Path> p_pathList,List<NodeMask> p_everyNode)
    {
        everyPath = new ArrayList<>();
        for (int i=0;i!=p_pathList.size();i++)
        {
            everyPath.add(new PathMask(p_pathList.get(i),p_everyNode));
        }
    }

}
