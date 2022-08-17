package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Agent.allyCops;
import static ir.sharif.aic.hideandseek.ai.Agent.enemyThieves;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;

public class PoliceAI extends AI {


    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

    int moveId;
    Agent self;
    Brain brain;
    String Payam;

    List<Integer> visibleTurnsList;

    @Override
    public int getStartingNode(GameView view) {

        init(view);
        update(view);
        return self.currentNode.id;
    }

    public void init(GameView view) {
        visibleTurnsList = view.getConfig().getTurnSettings().getVisibleTurnsList();


        brain = new Brain();
        Payam = "";
        long T0 = System.currentTimeMillis();

        List<AIProto.Node> viewNodes = view.getConfig().getGraph().getNodesList();
        List<AIProto.Path> viewEdges = view.getConfig().getGraph().getPathsList();

        for (int i=0;i!=viewNodes.size();i++)
        {
            new Node(viewNodes.get(i).getId());
        }
        Node.closeConstruction();
        for (int i=0;i!=viewEdges.size();i++)
        {
            new Edge(viewEdges.get(i).getId(),viewEdges.get(i).getFirstNodeId(),
                    viewEdges.get(i).getSecondNodeId(),viewEdges.get(i).getPrice());
        }
        Edge.closeConstruction();
        Node.initAll();

        self = new Agent(view.getViewer(),view.getBalance());

        Agent.initStatics(view.getVisibleAgentsList(),self.team);


        long T1 = System.currentTimeMillis();
        long T2 = T1 - T0;

        self.printReport(view,"<FirstExecutionTime:"+T2+">");
    }

    public void update(GameView view)
    {
        Agent.updateStatics(view.getVisibleAgentsList(),self.team,self);
        self.update(view.getViewer(),view.getBalance());
    }

    public void act(GameView view) {


        List<Node> options = self.affordableOptionsAsNodes;
        Node targetNode;

        targetNode = brain.think(enemyThieves,allyCops,view.getTurn().getTurnNumber(),self,visibleTurnsList.contains(view.getTurn().getTurnNumber()));

        if (options.contains(targetNode)){moveId=targetNode.id;}
        else{
            moveId = self.currentNode.id;
            Payam+="Target Node with id "+targetNode.id+" is not in the valid moves list!";
        }

    }

    @Override
    public int move(GameView view) {
        Payam = "";
        long T0 = System.currentTimeMillis();
        update(view);

        act(view);


        long T1 = System.currentTimeMillis();
        long T2 = T1 - T0;

        self.printReport(view,Payam+"<ExecutionTime:"+T2+">");
        return moveId;
    }
}