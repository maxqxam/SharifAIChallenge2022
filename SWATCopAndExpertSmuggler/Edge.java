package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Node.*;


public class Edge
{
    int id;
    Node node1;
    Node node2;
    double price;

    List<Edge> AEL;

    static boolean allowConstruction=true;
    static List<Edge> everyEdge=new ArrayList<>();


    Edge(int id,int node1Id,int node2Id,double price)
    {
        if (!allowConstruction){throw new Error("EdgeConstruction is not allowed");};
        this.id = id;
        this.node1 = Node.findNodeById(node1Id);
        this.node2 = Node.findNodeById(node2Id);
        this.price=price;
        everyEdge.add(this);
    }


    boolean containsNode(Node node) {return ((node1.id==node.id)||(node2.id==node.id));}
    Node getOpposingNode(Node node)
    {
        if (node1.id==node.id) {return node2;}
        else if(node2.id==node.id) {return node1;}
        throw new Error("Could not find node with id "+node.id+ " in edge with id "+this.id);
    }

    private void init(){}

    static void initAll(){}
    static void constructionCheck(){if (allowConstruction){throw new Error("everyEdge is still not constructed, therefore cannot accessed.");};}
    static void closeConstruction() {
        System.out.println("everyEdge construction completed, "+everyEdge.size()+" Objects are made");
        allowConstruction=false;
    }


}
