package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;

import static ir.sharif.aic.hideandseek.ai.Structures.Errors.*;
import static ir.sharif.aic.hideandseek.ai.Structures.Errors.NodeNotFoundException;

public class Edge
{
    final int id;
    final int index;
    final int node1Index;
    final int node2Index;
    final double price;
    private boolean isInitialized=false;

    private ArrayList<Integer> adjacentEdgesIndexList;

    public static ArrayList<Edge> edgeMap;
    private static boolean isComplete=false;

    Edge(int id,int node1Id,int node2Id,double price)
    {
        if (isComplete){throw new Error(EdgeConstructionException.text);}
        this.id = id;
        this.node1Index = Node.findNodeIndexById(node1Id);
        this.node2Index = Node.findNodeIndexById(node2Id);
        this.price = price;

        this.index = edgeMap.size();
        edgeMap.add(this);
    }

    public void init()
    {
        isInitialized=true;
        adjacentEdgesIndexList = new ArrayList<>();
        adjacentEdgesIndexList.addAll(Node.nodeMap.get(node1Index).getAdjacentEdgesIndexList());
        adjacentEdgesIndexList.addAll(Node.nodeMap.get(node2Index).getAdjacentEdgesIndexList());

        int i=0;
        int end=adjacentEdgesIndexList.size();
        for (i=0;i!=end;i++)
        {
            if (adjacentEdgesIndexList.get(i)==index)
            {
                adjacentEdgesIndexList.remove(i);
                i--;
                end--;
            }
        }

        adjacentEdgesIndexList.trimToSize();
//        System.out.println(this.toString());

    }

    public boolean includeNodeIndex(int nodeIndex) {return ((nodeIndex==node1Index)||(nodeIndex==node2Index));}

    public int getOpposingNodeIndex(int nodeIndex)
    {
        if (nodeIndex==node1Index){return node2Index;}
        else if(nodeIndex==node2Index){return node1Index;}
        throw new Error(EdgeDoesNotContainNode.text);
    }

    public Node getOpposingNode(int nodeIndex)
    {
        return Node.nodeMap.get(getOpposingNodeIndex(nodeIndex));
    }



    public ArrayList<Integer> getAdjacentEdgesIndexList() {return new ArrayList<>(adjacentEdgesIndexList);}

    public String toString(){return getString(this);}


    public static Edge findEdgeById(int edgeId)
    {
        if (!isComplete) {throw new Error(EdgeMapConstructionException.text);}
        for (int i=0;i!=edgeMap.size();i++)
        {
            if (edgeMap.get(i).id==edgeId){ return edgeMap.get(i);}
        }
        throw new Error(EdgeNotFoundException.text);
    }


    public static void closeConstruction(){isComplete=true;}

    public static String getString(Edge edge)
    {
        if (!edge.isInitialized){throw new Error(EdgeInitializationException.text);}
        return "<id:"+edge.id+"> <index:"+edge.index+"> <node1Index:"+edge.node1Index+"> <node2Index:"+edge.node2Index+">"
                +" <AEC:"+edge.getAdjacentEdgesIndexList().size()+">";
    }

    public static void initStatics()
    {
        edgeMap = new ArrayList<>();
    }

}
