package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;

import static ir.sharif.aic.hideandseek.ai.Structures.Errors.*;

public class Node {

    final int id;
    final int index;
    private int weight;

    private ArrayList<Integer> adjacentNodesIndexList;
    private ArrayList<Integer> adjacentEdgesIndexList;

    private boolean isInitialized=false;


    public static ArrayList<Node> nodeMap;
    private static boolean isComplete=false;

    Node(int id)
    {
        if (isComplete){throw new Error(NodeConstructionException.text);}
        this.id = id;
        this.index = nodeMap.size();
        this.weight=0;
        nodeMap.add(this);
    }

    public void init()
    {
        isInitialized=true;

        adjacentNodesIndexList = new ArrayList<>();
        adjacentEdgesIndexList = new ArrayList<>();
        for (int i=0;i!=Edge.edgeMap.size();i++)
        {
            if (Edge.edgeMap.get(i).includeNodeIndex(index)){
                adjacentNodesIndexList.add(Edge.edgeMap.get(i).getOpposingNodeIndex(index));
                adjacentEdgesIndexList.add(Edge.edgeMap.get(i).index);
            }
        }

        adjacentNodesIndexList.trimToSize();
        adjacentEdgesIndexList.trimToSize();

//        System.out.println(this.toString());
    }

    public void sumWeight(int amount) {this.weight += amount;}
    public void setWeight(int weight) {this.weight = weight;}
    public int getWeight() {return this.weight;}
    public String toString(){return getString(this);}


    public ArrayList<Integer> getAdjacentNodesIndexList() {return new ArrayList<>(adjacentNodesIndexList);}
    public ArrayList<Integer> getAdjacentEdgesIndexList() {return new ArrayList<>(adjacentEdgesIndexList);}

    public static Node findNodeById(int nodeId)
    {
        if (!isComplete) {throw new Error(NodeMapConstructionException.text);}
        for (int i=0;i!=nodeMap.size();i++)
        {
            if (nodeMap.get(i).id==nodeId){ return nodeMap.get(i);}
        }
        throw new Error(NodeNotFoundException.text);
    }

    public static int findNodeIndexById(int nodeId) {return findNodeById(nodeId).index;}


    public static void flushWeight()
    {
        for (int i=0;i!=nodeMap.size();i++)
        {
            nodeMap.get(i).weight=0;
        }
    }

    public static void closeConstruction(){isComplete=true;}

    public static String getString(Node node)
    {
        if (!node.isInitialized){throw new Error(NodeInitializationException.text);}
        return "<id:"+node.id+"> <index:"+node.index+"> <ANC:"+node.getAdjacentNodesIndexList().size()+"> <weight:"+ node.getWeight()+">";
    }

    public static void initStatics()
    {
        nodeMap = new ArrayList<>();
    }

}
