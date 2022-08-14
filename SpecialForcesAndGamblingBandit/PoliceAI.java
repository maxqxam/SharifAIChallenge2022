package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.Agent.enemyThieves;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;

public class PoliceAI extends AI {


    public PoliceAI(Phone phone) {
        this.phone = phone;
    }

        int moveId;
        Agent self;
        String Payam;

        @Override
        public int getStartingNode(GameView view) {

            init(view);
            update(view);
            return self.currentNode.id;
        }

        public void init(GameView view)
        {
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

            Agent.initStatics(view.getVisibleAgentsList(),self.team,view);


            long T1 = System.currentTimeMillis();
            long T2 = T1 - T0;

            self.printReport(view,"<FirstExecutionTime:"+T2+">");
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

            int identity = self.whoAmI();

            if (enemyThieves.size()!=0) // moves the cops to the vicinity of a thief , if he can see any
            {
                Node target = enemyThieves.get(0).currentNode;

                List<Node> targetVicinity = target.ANL;
                List<Node> targetL2Vicinity = new ArrayList<>();

                List<Node> tempNodeList;
                for (int i=0;i!=targetVicinity.size();i++)
                {
                    tempNodeList=targetVicinity.get(i).ANL;
                    for (int c=0;c!=tempNodeList.size();c++) {
                        if (!targetVicinity.contains(tempNodeList.get(c)) && !(targetL2Vicinity.contains(tempNodeList.get(c)))) {
                            targetL2Vicinity.add(tempNodeList.get(c));
                        }
                    }
                }

                if (identity<targetVicinity.size()) {
                    List<Step> stepList = shortestPath(self.currentNode,targetVicinity.get(identity));
                    resultNode = stepList.get(stepList.size()-1).node;
                }
                else {
                    List<Step> stepList = shortestPath(self.currentNode,targetL2Vicinity.get(identity-targetVicinity.size()));
                    resultNode = stepList.get(stepList.size()-1).node;
                }

                // Kicks in when a thief is in the territory of this cop
                for (int i=0; i!=enemyThieves.size(); i++)
                {
                    if (self.currentNode.ANL.contains(enemyThieves.get(i).currentNode))
                    {
                        resultNode = enemyThieves.get(i).currentNode;
                        enemyThieves.remove(i);
                        break;
                    }
                }
            }
            else // If the cop sees no thief
            {
                List<Node> fattestNodes = getFattestNodes(75);
                if (identity<fattestNodes.size()) {
                    List<Step> stepList = shortestPath(self.currentNode,fattestNodes.get(identity));
                    resultNode = stepList.get(stepList.size()-1).node;
                }
            }

            moveId = self.currentNode.id;

            if (validChoices.contains(resultNode)){
                moveId = resultNode.id;
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
