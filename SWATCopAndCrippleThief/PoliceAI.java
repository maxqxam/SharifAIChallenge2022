package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;


public class PoliceAI extends AI {

    ArrayList<AIProto.Node> everyNode;
    ArrayList<AIProto.Path> everyEdge;
    Agent self;

    public PoliceAI(Phone phone) {this.phone = phone;}

    public void initialize(GameView view)
    {
        Agent.initStatics();

        everyNode = new ArrayList<>(view.getConfig().getGraph().getNodesList());
        everyEdge = new ArrayList<>(view.getConfig().getGraph().getPathsList());


        AIProto.Node tempNode;
        AIProto.Path tempEdge;

        Node.initStatics();
        Edge.initStatics();

        for (int i=0;i!=everyNode.size();i++)
        {
            tempNode = everyNode.get(i);
            new Node(tempNode.getId());
        }
        Node.closeConstruction();

        for (int i=0;i!=everyEdge.size();i++)
        {
            tempEdge = everyEdge.get(i);
            new Edge(tempEdge.getId(),tempEdge.getFirstNodeId(),tempEdge.getSecondNodeId(),tempEdge.getPrice());
        }
        Edge.closeConstruction();

        for (int i=0;i!=Node.nodeMap.size();i++)
        {
            Node.nodeMap.get(i).init();
        }

        for (int i=0;i!=Edge.edgeMap.size();i++)
        {
            Edge.edgeMap.get(i).init();
        }

        self = new Agent(view.getViewer(),view.getBalance());
    }

    public void update(GameView view)
    {
        self.update(view.getViewer(),view.getBalance());
        Agent.updateStatics(new ArrayList<AIProto.Agent>(view.getVisibleAgentsList()),self.team);
        Node.flushWeight();
    }


    @Override
    public int getStartingNode(GameView view) {
        initialize(view);
        update(view);
        return 46;
    }

    public static String buffer = "";
    @Override
    public int move(GameView view) {
        int result=view.getViewer().getNodeId();
        long t1 = System.currentTimeMillis();
        buffer = "";

        update(view);
        ArrayList<Integer> affordableEdges = self.getAffordableEdges();
        if (affordableEdges.size()!=0)
        {
            for (int i=0;i!=Agent.enemyThievesArrayList.size();i++)
            {
                Agent.enemyThievesArrayList.get(i).considerAgentTerritory(15);
            }

            affordableEdges = self.getFatEdges(self);

            buffer+="Agent nodeIndex: "+self.getNodeIndex()+"\n";
            ArrayList<Integer> importantArrayList = Node.nodeMap.get(self.getNodeIndex()).getAdjacentNodesIndexList();
            for (int i=0;i!=importantArrayList.size();i++)
            {
                buffer+="\t"+Node.nodeMap.get(importantArrayList.get(i)).toString()+"\n";
            }

            int ran=randInt(0,affordableEdges.size());
            int edgeIndex=affordableEdges.get(ran);
            int nodeId = Edge.edgeMap.get(edgeIndex).getOpposingNode(self.getNodeIndex()).id;
            result = nodeId;

        }
        else{
            System.out.println("Cant afford any");
        }


        long t2 = System.currentTimeMillis();

        System.out.println(view.getTurn().getTurnNumber()+" Police id :"+self.id+" <Time:"+(t2-t1)+">");


//        buffer+=("\t<Time:"+(t2-t1)+">");
//        System.out.println(buffer);

        return result;
    }
}
