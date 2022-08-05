package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.ArrayList;



public class Agent
{


    final int id;
    final AIProto.Team team;
    final AIProto.AgentType type;

    private int nodeIndex;
    private double balance;

    public static ArrayList<Agent> enemyCopsArrayList;
    public static ArrayList<Agent> enemyThievesArrayList;

    public static ArrayList<Agent> allyCopsArrayList;
    public static ArrayList<Agent> allyThievesArrayList;





    Agent(AIProto.Agent agent,double balance)
    {
        this.id = agent.getId();
        this.team = agent.getTeam();
        this.type = agent.getType();

        update(agent,balance);
    }


    public void update(AIProto.Agent agent,double balance)
    {
        this.nodeIndex = Node.findNodeIndexById(agent.getNodeId());
        this.balance = balance;
    }

    public ArrayList<Integer> getAvailableEdges()
    {
        return Node.nodeMap.get(nodeIndex).getAdjacentEdgesIndexList();
    }

    public ArrayList<Integer> getSkinnyEdges(Agent self)
    {
        ArrayList<Integer> affordableEdges = new ArrayList<>(getAffordableEdges());

        int lMinWeight=0;
        int minWeight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(0)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
        int weight = 0;
        for (int i=0;i!=affordableEdges.size();i++)
        {
            weight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(i)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
            if (weight<minWeight){
                lMinWeight=minWeight;
                minWeight=weight;
            }
        }

        ArrayList<Integer> result = new ArrayList<>();

        for (int i=0;i!=affordableEdges.size();i++)
        {
            weight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(i)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
            if ((weight==minWeight)||(weight==lMinWeight)){result.add(affordableEdges.get(i));}
        }

        return result;
    }


    public ArrayList<Integer> getFatEdges(Agent self)
    {
        ArrayList<Integer> affordableEdges = new ArrayList<>(getAffordableEdges());

        int maxWeight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(0)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
        int weight = 0;
        for (int i=0;i!=affordableEdges.size();i++)
        {
            weight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(i)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
            if (weight>maxWeight){maxWeight=weight;}
        }

        ArrayList<Integer> result = new ArrayList<>();

        for (int i=0;i!=affordableEdges.size();i++)
        {
            weight = Node.nodeMap.get(Edge.edgeMap.get(affordableEdges.get(i)).getOpposingNodeIndex(self.nodeIndex)).getWeight();
            if (weight==maxWeight){result.add(affordableEdges.get(i));}
        }

        return result;
    }

    public ArrayList<Integer> getAffordableEdges()
    {
        ArrayList<Integer> affordableEdges = new ArrayList<>(getAvailableEdges());
        int begin = 0; int end = affordableEdges.size();

        for (int i=begin;i!=end;i++)
        {
            if (Edge.edgeMap.get(affordableEdges.get(i)).price>balance)
            {
                affordableEdges.remove(i);
                i--;
                end--;
            }
        }

        return affordableEdges;
    }

    public ArrayList<ArrayList<Integer>> getNodeIndexListLayers(int layers)
    {
        ArrayList<ArrayList<Integer>> arrayListArrayList = new ArrayList<>();
        ArrayList<Integer> integerArrayList = new ArrayList<>(Node.nodeMap.get(nodeIndex).getAdjacentNodesIndexList());
        ArrayList<Integer> integerArrayList2 = new ArrayList<>();

        integerArrayList.add(nodeIndex);

        arrayListArrayList.add(new ArrayList<>(integerArrayList));


        int i;
        for (i=0;i!=layers-1;i++)
        {
            integerArrayList = arrayListArrayList.get(i);
            for (int c=0;c!=integerArrayList.size();c++)
            {
//                integerArrayList2.addAll(Node.nodeMap.get(integerArrayList.get(c)).getAdjacentNodesIndexList());
                SharedMethods.addNewElements(integerArrayList2,Node.nodeMap.get(integerArrayList.get(c)).getAdjacentNodesIndexList());
            }

            arrayListArrayList.add(new ArrayList<>(integerArrayList2));
        }

//        integerArrayList = new ArrayList<>();
//
//        for (i=0;i!=arrayListArrayList.size();i++)
//        {
//            integerArrayList.addAll(arrayListArrayList.get(i));
//        }
        return arrayListArrayList;


    }



    public void considerAgentTerritory(int layers)
    {
        ArrayList<ArrayList<Integer>> nodeIndexArrayList = getNodeIndexListLayers(layers);

        for (int c=0;c!=nodeIndexArrayList.size();c++)
        {
            for (int i=0;i!=nodeIndexArrayList.get(c).size();i++)
            {
                Node.nodeMap.get(nodeIndexArrayList.get(c).get(i)).sumWeight((nodeIndexArrayList.size()-c));
            }
        }

    }

    public static void initStatics()
    {
        allyCopsArrayList = new ArrayList<>();
        allyThievesArrayList = new ArrayList<>();
        enemyCopsArrayList = new ArrayList<>();
        enemyThievesArrayList = new ArrayList<>();
    }

    public static void updateStatics(ArrayList<AIProto.Agent> agentArrayList, AIProto.Team team)
    {

        AIProto.Agent tempAgent;

        ArrayList<Agent> tempAllyCopsArrayList = new ArrayList<>();
        ArrayList<Agent> tempAllyThievesArrayList = new ArrayList<>();
        ArrayList<Agent> tempEnemyCopsArrayList = new ArrayList<>();
        ArrayList<Agent> tempEnemyThievesArrayList = new ArrayList<>();



        for (int i=0;i!=agentArrayList.size();i++)
        {
            tempAgent=agentArrayList.get(i);

            if (tempAgent.getTeam()==team)
            {
                if (tempAgent.getType()== AIProto.AgentType.POLICE)
                {
                    tempAllyCopsArrayList.add(new Agent(tempAgent,0));
                }
                else if (!tempAgent.getIsDead())
                {
                    tempAllyThievesArrayList.add(new Agent(tempAgent,0));
                }

            }
            else
            {
                if (tempAgent.getType()== AIProto.AgentType.POLICE)
                {
                    tempEnemyCopsArrayList.add(new Agent(tempAgent,0));
                }
                else if (!tempAgent.getIsDead())
                {
                    tempEnemyThievesArrayList.add(new Agent(tempAgent,0));
                }
            }
        }

        if (tempAllyCopsArrayList.size()!=0){allyCopsArrayList = new ArrayList<>(tempAllyCopsArrayList);}
        if (tempAllyThievesArrayList.size()!=0){allyThievesArrayList = new ArrayList<>(tempAllyThievesArrayList);}
        if (tempEnemyCopsArrayList.size()!=0){enemyCopsArrayList = new ArrayList<>(tempEnemyCopsArrayList);}
        if (tempEnemyThievesArrayList.size()!=0){enemyThievesArrayList = new ArrayList<>(tempEnemyThievesArrayList);}


    }



    public static boolean agentInclusionState(ArrayList<Agent> agentArrayList,int agentId)
    {
        for (int i=0;i!=agentArrayList.size();i++)
        {
            if (agentArrayList.get(i).id==agentId)
            {
                return true;
            }
        }

        return false;
    }

    public String toString(){return "id:"+id+" nodeIndex:"+nodeIndex;}
    public int getNodeIndex() {return nodeIndex;}
    public double getBalance() {return balance;}
}
