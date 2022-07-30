package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

public class PoliceAI extends AI {

    AgentMask self;

    public PoliceAI(Phone phone) {
        this.phone = phone;
    }
    public void initialize(GameView view)
    {
        self = new AgentMask(view.getViewer(),view.getBalance());
        NodeMask.fillEveryNode(view.getConfig().getGraph().getNodesList());
        PathMask.fillEveryPath(view.getConfig().getGraph().getPathsList(),NodeMask.everyNode);


    }

    public void update(GameView view)
    {
        self.update(view.getBalance(),view.getViewer().getNodeId());
        self.update(NodeMask.everyNode,PathMask.everyPath);
    }

    @Override
    public int getStartingNode(GameView view) {
        initialize(view);
        update(view);
        return 7;
    }

    public void printOptions(List<PathMask> p_options)
    {
        String string = "[";

        for (int i=0;i!=p_options.size();i++)
        {
            string += "("+p_options.get(i).firstNode.nodeId +","+p_options.get(i).secondNode.nodeId+")";
        }

        string+="] : "+self.getNodeId();

        System.out.println(string);
    }

    @Override
    public int move(GameView view) {
        update(view);

        List<PathMask> options = self.getAffordablePaths();

        if (options.size()!=0){
            int choice = randInt(0,options.size());
            return options.get(choice).getOpposingNode(self.getNodeId()).nodeId;
        }
        return self.getNodeId();
    }
}
