package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.Vector;
import java.util.function.Predicate;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;


public class ThiefAI extends AI {

    // Slippery Thief


    // This thief randomly moves to nodes that aren't in the vicinity/current-node of a cop
    // It also spawns itself at a node meeting the needs above
    // If he can't pay for something he'll take a cheaper one
    // If he can't move to any place because of being surrounded by cops , he stays in his node and says : I'm Stuck
    // If he can't move to any place because of low balance , he stays in his node and says : I'm Broke
    // If he can't move to any place because of being surrounded by cops, and a cop is also in his vicinity , he stays in his node and says : I'm Caught

    AgentMask self;
    int turnCounter;


    public ThiefAI(Phone phone) {
        this.phone = phone;
    }


    public void initialize(GameView view)
    {
        AgentMask.insertEveryPath(view.getConfig().getGraph().getPathsList());

        self = new AgentMask(view.getViewer());
        self.setBalance(view.getBalance());
        AgentMask.initializeStatics(view.getVisibleAgentsList(),self.agent.getTeam().getNumber());
        turnCounter=-1;
    }

    public void update(GameView view)
    {
        self.agent = view.getViewer();
        self.update();
        AgentMask.updateStatics(view.getVisibleAgentsList());
        turnCounter++;

    }



    @Override
    public int getStartingNode(GameView view) {
        initialize(view);
        update(view);
        return 46;
    }


    @Override
    public int move(GameView view) {

        update(view);

        Vector<IntMask> collidingNodes = new Vector<>();
        Vector<PathMask> choicePathVector = PathMask.getVectorCopy(self.getAffordablePaths());

        for (int i=0;i!=AgentMask.getEnemyCops().size();i++)
        {
           for (int c=0;c!=AgentMask.getEnemyCops().get(i).getTerritory().size();c++)
           {
               if (!IntMask.getVectorInclusionState(collidingNodes,AgentMask.getEnemyCops().get(i).getTerritory().get(c).number))
               {
                   collidingNodes.add(AgentMask.getEnemyCops().get(i).getTerritory().get(c));
               }
           }
        }

        AgentMask.subtractVector(self.agent.getNodeId(),choicePathVector,collidingNodes);

        if ((self.getAffordablePaths().size()-choicePathVector.size())!=0)
        {
            if (choicePathVector.size()==0)
            {
                System.out.println
                        (
                                "<Turn: "+turnCounter+"> "+
                                        self.getAffordablePaths().size()+" "+
                                        choicePathVector.size()+" "+
                                        self.getUnaffordablePaths().size()+" |"+
                                        " Surrounded agent: "+self.agent.getId()+
                                        " Affordable options: "+
                                        PathMask.getVectorString(self.getAffordablePaths())+
                                        " Unaffordable options: "+
                                        PathMask.getVectorString(self.getUnaffordablePaths())+
                                        " Enemy Territory Size: "+collidingNodes.size()

                        );
            }
            else
            {
                System.out.println
                        (
                                "<Turn: "+turnCounter+"> "+
                                self.getAffordablePaths().size()+" "+
                                        choicePathVector.size()+" "+
                                        self.getUnaffordablePaths().size()+
                                        " | Dangered agent: "+self.agent.getId()+
                                        " Affordable options: "+
                                        PathMask.getVectorString(self.getAffordablePaths())+
                                        " Chosen options: "+
                                        PathMask.getVectorString(choicePathVector)+
                                        " Unaffordable options: "+
                                        PathMask.getVectorString(self.getUnaffordablePaths())+
                                        " Enemy Territory Size: "+collidingNodes.size()
                        );
            }

        }
        else
        {
            System.out.println
                    (
                            "<Turn: "+turnCounter+"> "+
                            self.getAffordablePaths().size()+" "+
                            choicePathVector.size()+" "+
                            self.getUnaffordablePaths().size()+" | "+
                            "Safe agent: "+self.agent.getId()+ " Affordable options: "+
                            PathMask.getVectorString(self.getAffordablePaths())+" Unaffordable options: "+
                            PathMask.getVectorString(self.getUnaffordablePaths())+" Enemy Territory Size: "+collidingNodes.size()
                    );
        }






        if (choicePathVector.size()!=0)
        {
            int ranChoice=randInt(0,choicePathVector.size());

            return self.moveTo(choicePathVector.get(ranChoice));
        }

        return self.agent.getNodeId();
    }

}
