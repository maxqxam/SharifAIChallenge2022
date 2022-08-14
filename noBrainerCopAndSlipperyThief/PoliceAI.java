package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;



import java.util.List;
import java.util.Vector;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;

public class PoliceAI extends AI {

    // No-Brainer Cop
    // He does anything he can afford

    // This cop randomly moves to any node, from any path
    // if he can't pay for something, he'll take a cheaper one
    // if he can't move because he has no money, he says : I'm broke

    AgentMask self;


    public PoliceAI(Phone phone) {
        this.phone = phone;
    }


    public void initialize(GameView view)
    {
        AgentMask.insertEveryPath(view.getConfig().getGraph().getPathsList());

        self = new AgentMask(view.getViewer());
        self.setBalance(view.getBalance());
        AgentMask.initializeStatics(view.getVisibleAgentsList(),self.agent.getTeam().getNumber());

    }

    public void update(GameView view)
    {
        self.agent = view.getViewer();
        self.update();
        AgentMask.updateStatics(view.getVisibleAgentsList());
    }

    @Override
    public int getStartingNode(GameView view) {
        initialize(view);
        update(view);
        return self.agent.getNodeId();
    }


    @Override
    public int move(GameView view) {
        update(view);

        if (self.getAffordablePaths().size()!=0)
        {
            int ranChoice=randInt(0,self.getAffordablePaths().size());

            return self.moveTo(self.getAffordablePaths().get(ranChoice));
        }

        return self.agent.getNodeId();
    }

}
