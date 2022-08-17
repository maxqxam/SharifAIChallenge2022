package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Edge.*;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.getPercent;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.shortestPath;

public class Node
{
    final int id;

    List<Node> ANL;
    List<Edge> AEL;

    double starValue;


    static boolean allowConstruction=true;
    private static double biggestStar;

    static List<Node> everyNode=new ArrayList<>();

    Node(int id)
    {
        if (!allowConstruction){throw new Error("NodeConstruction is not allowed");};
        this.id=id;
        everyNode.add(this);
    }


    private void init()
    {
        ANL=new ArrayList<>();
        AEL=new ArrayList<>();

        for (int i=0;i!=everyEdge.size();i++)
        {
            if (everyEdge.get(i).containsNode(this))
            {
                ANL.add(everyEdge.get(i).getOpposingNode(this));
                AEL.add(everyEdge.get(i));
            }
        }

    }

    private void initStar()
    {
        List<Node> territory = new ArrayList<>(ANL);
        territory.add(this);

        for (int i=0;i!=territory.size();i++)
        {
            starValue+=territory.get(i).ANL.size();
        }

        if (starValue>biggestStar) {biggestStar=starValue;}

    }

    static void initAll()
    {
        constructionCheck();
        Edge.constructionCheck();

        for (int i=0;i!=everyNode.size();i++)
        {
            everyNode.get(i).init();
        }

        for (int i=0;i!=everyNode.size();i++)
        {
            everyNode.get(i).initStar();
        }



    }


    static void constructionCheck(){if (allowConstruction){throw new Error("everyNode is still not constructed, therefore cannot accessed.");};}
    static void closeConstruction() {
        System.out.println("everyNode construction completed, "+everyNode.size()+" Objects are made");
        allowConstruction=false;
    }

    static List<Node> getClosestNodes(Node source,List<Node> destList)
    {
        List<Node> result = new ArrayList<>();

        int minDist=1000;
        int dist;
        for (int i=0;i!=destList.size();i++)
        {
            if (i==0)
            {
                minDist=shortestPath(source,destList.get(i)).size();
            }

            dist=shortestPath(source,destList.get(i)).size();

            if (dist<minDist)
            {
                minDist=dist;
            }
        }

        for (int i=0;i!=destList.size();i++)
        {
            dist=shortestPath(source,destList.get(i)).size();
            if (dist==minDist)
            {
                result.add(destList.get(i));
            }
        }

        if (result.size()==0) {
            throw new Error("Error : Unexpected empty NodeList at Node.getClosestNodes");
        }

        return result;
    }


    static Node findNodeById(int nodeId)
    {
        constructionCheck();
        for (int i=0;i!=everyNode.size();i++)
        {
            if (everyNode.get(i).id==nodeId)
            {
                return everyNode.get(i);
            }
        }

        throw new Error("Node with id "+nodeId+" was not found in everyNode");
    }
}
