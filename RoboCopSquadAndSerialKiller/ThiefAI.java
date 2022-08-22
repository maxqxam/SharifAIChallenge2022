package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Agent.*;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.getFattestNodes;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;


public class ThiefAI extends AI {


    int moveId;
    Agent self;
    Brain brain;

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

        int nodeId;
        try{
            nodeId = fattestNodes.get(randInt(0,fattestNodes.size())).id;
        }
        catch (Error e)
        {
            nodeId = getFattestNodes(80).get(0).id;
            System.out.println("Client Error at get_starting_node : "+e+ ", \n choosing the default node : "+nodeId);
        }

        return nodeId;
    }

    public void init(GameView view)
    {
        brain = new Brain();

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
        Agent.updateStatics(view.getVisibleAgentsList(),self.team,self);
        self.update(view.getViewer(),view.getBalance());
    }

    public void act(GameView view)
    {
        Node resultNode = self.currentNode;

        List<Edge> options = self.affordableOptions;
        List<Node> validChoices = new ArrayList<>();
        for (int i=0;i!=options.size();i++) {
            validChoices.add(options.get(i).getOpposingNode(self.currentNode));
        }
        validChoices.add(self.currentNode);
//        int identity = self.whoAmI();

        for (int i=0;i!=enemyCops.size();i++)
        {
            enemyCops.get(i).subtractOptions(self,options);
        }

        if (self.isInDanger) // moves him if he is in danger
        {
            if (options.size()!=0)
            {
                List<Edge> copyOptions = new ArrayList<>(options);
                Agent evilTwin = new Agent(self);

                Brain.ThiefState thiefState;
                int end = copyOptions.size();
                for (int i=0;i!=end;i++){
                    evilTwin.currentNode = copyOptions.get(i).getOpposingNode(self.currentNode);
                    thiefState = brain.getThiefState(evilTwin , enemyCops);

                    if (thiefState==Brain.ThiefState.SURROUNDED || thiefState==Brain.ThiefState.HARD_PINNED || thiefState==Brain.ThiefState.SOFT_PINNED){
                        copyOptions.remove(i);
                        i--;
                        end--;
                    }
                }

                List<Node> newOptions;
                if (!copyOptions.isEmpty()){
                    newOptions = getFattestOptions(self.currentNode,copyOptions,85);
                }
                else{
                    System.out.println("I am forced to make myself surrounded or pinned!");
                    newOptions = getFattestOptions(self.currentNode,options,85);
                }
                resultNode = newOptions.get(randInt(0,newOptions.size()));

            }
            else
            {
                List<Node> ANL = new ArrayList<>(self.currentNode.ANL);

                for (int i=0;i!=enemyCops.size();i++){ANL.remove(enemyCops.get(i).currentNode);}
                if (!ANL.isEmpty()){resultNode = ANL.get(randInt(0,ANL.size()));}

                System.out.println("I'm surrounded!! gambling");
            }
        }
        else // check to move him to a better place otherwise
        {
            List<Node> newOptions = getFattestOptions(self.currentNode,options,85);
            List<Node> tempOptions = new ArrayList<>();

            for (int i=0;i!=newOptions.size();i++) {
                if (newOptions.get(i).starValue>self.currentNode.starValue) {
                    tempOptions.add(newOptions.get(i));
                }
            }

            if (tempOptions.size()!=0) {
                resultNode = tempOptions.get(randInt(0,tempOptions.size()));
            }
        }

//        moveId = self.currentNode.id;
        moveId = resultNode.id;
    }

    @Override
    public int move(GameView view) {
        update(view);

        act(view);
        return moveId;
    }
}
