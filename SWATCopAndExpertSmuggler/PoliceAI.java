package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;

public class PoliceAI extends AI {


    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

        int moveId;
        Agent self;

        @Override
        public int getStartingNode(GameView view) {
            init(view);
            update(view);
            return 25;
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

            Node target = self.currentNode;
            boolean hasTarget=false;
            Agent targetThief;
            List<Node> ANL;
            if (Agent.enemyThieves.size()!=0)
            {
                hasTarget=true;
                targetThief = Agent.enemyThieves.get(0);
                ANL = targetThief.getTerritory();
                target = ANL.get(randInt(0,ANL.size()));
            }

            List<Step> stepList = shortestPath(self.currentNode,target);
            Node destNode = Node.findNodeById(1);

            for (int i=0;i!=stepList.size();i++) {
                if (stepList.get(i).layer==0)
                {
                    destNode = stepList.get(i).node;
                }
            }


            if (options.size()!=0) {
                if (hasTarget){

                    // Security check - checks if there is any Thief in the next choices, and if there is , he moves towards it
                    for (int i=0;i!=Agent.enemyThieves.size();i++)
                    {
                        targetThief=Agent.enemyThieves.get(i);
                        if (self.currentNode.ANL.contains(targetThief.currentNode))
                        {
                            destNode = targetThief.currentNode;
                            Agent.enemyThieves.remove(i);
                            System.out.println("Payam : Removed assumed enemy thief.");
                            break;
                        }
                    }

                    for (int i=0;i!=options.size();i++)
                    {
                        if (options.get(i).containsNode(destNode))
                        {
                            moveId = destNode.id;
                        }
                    }

                }
                else
                {
                    Node newNode = Agent.getFattestOption(self.currentNode,options,80);

                        moveId = newNode.id;

                }


            }
            else {
                System.out.println("Payam : I Can't move!");
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
