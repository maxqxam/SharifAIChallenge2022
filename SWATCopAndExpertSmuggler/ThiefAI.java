package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Agent.*;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.getFattestNodes;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;


public class ThiefAI extends AI {


    int moveId;
    Agent self;

    public ThiefAI(Phone phone) {
        this.phone = phone;
    }

    @Override
    public int getStartingNode(GameView view) {
        init(view);
        update(view);

        Node copsNode = enemyCops.get(0).currentNode;

        List<Node> fattestNodes = getFattestNodes(80);

        if (fattestNodes.contains(copsNode)) {fattestNodes.remove(copsNode);}

        return fattestNodes.get(randInt(0,fattestNodes.size())).id;
    }

    public void init(GameView view)
    {
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


    }

    public void update(GameView view)
    {
        Agent.updateStatics(view.getVisibleAgentsList(),self.team);
        self.update(view.getViewer(),view.getBalance());
    }

    public void act(GameView view)
    {
        List<Edge> options = self.affordableOptions;

        for (int i=0;i!= enemyCops.size();i++)
        {
            enemyCops.get(i).subtractOptions(self,options);
        }

        if (options.size()!=0)
        {

            Node newNode = Agent.getFattestOption(self.currentNode,options,75);

            if (!self.isInDanger)
            {
                if (newNode.starValue<self.currentNode.starValue)
                {
                    newNode = self.currentNode;
                }
            }

            moveId = newNode.id;
        }
        else
        {
            System.out.println("I'm surrounded");
            moveId = self.currentNode.id;
        }
    }

    @Override
    public int move(GameView view) {
        update(view);

        act(view);


        return moveId;
    }
}
