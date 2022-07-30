package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.ArrayList;
import java.util.List;

public class NodeMask
{
    // PRIVATE ATTRIBUTES
    private int value;

    // PUBLIC ATTRIBUTES
    public final int nodeId;

    // PUBLIC STATIC ATTRIBUTES

    public static List<NodeMask> everyNode;

    // CONSTRUCTORS
    NodeMask(AIProto.Node p_node)
    {
        nodeId = p_node.getId();
        value = 0;
    }

    NodeMask(int p_nodeId)
    {
        nodeId = p_nodeId;
    }

    // ENCAPSULATION SETTERS

    public void setValue(int p_value){value = p_value;}

    // ENCAPSULATION GETTERS

    public int getValue(){ return value;}


    // PRIVATE METHODS

    // PUBLIC METHODS

    // PUBLIC STATIC METHODS

    public static NodeMask findNodeById(int p_NodeId)
    {
        return findNodeById(everyNode,p_NodeId);
    }

    public static NodeMask findNodeById(List<NodeMask> p_nodeList, int p_NodeId)
    {
        for (int i=0;i!=p_nodeList.size();i++)
        {
            if (p_nodeList.get(i).nodeId==p_NodeId)
            {
                return p_nodeList.get(i);
            }
        }
        throw new Error(Enums.Errors.NodeNotFoundException.text);
    }

    public static void fillEveryNode(List<AIProto.Node> p_nodeList)
    {
        everyNode = new ArrayList<>();
        for (int i=0;i!=p_nodeList.size();i++)
        {
            everyNode.add(new NodeMask(p_nodeList.get(i)));
        }
    }
}
