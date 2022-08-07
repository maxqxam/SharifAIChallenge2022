package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.getPercent;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

public class Agent
{
    final int id;
    Node currentNode;
    double balance;


    boolean isInDanger=false;

    List<Edge> affordableOptions;

    final AIProto.Team team;
    final AIProto.AgentType type;

    static List<Agent> enemyCops;
    static List<Agent> enemyThieves;
    static List<Agent> allyCops;
    static List<Agent> allyThieves;


    Agent(int id, Node currentNode, double balance, AIProto.Team team, AIProto.AgentType type)
    {
        this.id = id;
        this.currentNode = currentNode;
        this.balance = balance;
        this.team = team;
        this.type = type;
    }

    Agent(AIProto.Agent agent,double balance)
    {
        Node.constructionCheck();
        this.id = agent.getId();
        this.currentNode = Node.findNodeById(agent.getNodeId());
        this.balance = balance;
        this.team = agent.getTeam();
        this.type = agent.getType();
    }

    Agent(AIProto.Agent agent)
    {
        Node.constructionCheck();
        this.id = agent.getId();
        this.currentNode = Node.findNodeById(agent.getNodeId());
        this.balance = -1;
        this.team = agent.getTeam();
        this.type = agent.getType();
    }


    public void update(AIProto.Agent agent,double balance)
    {
        this.isInDanger = false;
        this.currentNode = Node.findNodeById(agent.getNodeId());
        this.balance = balance;
        affordableOptions = getAffordablePaths();
    }


    private List<Edge> getAvailablePaths()
    {
        return new ArrayList<>(currentNode.AEL);
    }

    private List<Edge> getAffordablePaths()
    {
        List<Edge> result = getAvailablePaths();


        int end = result.size();
        for (int i=0;i!=end;i++)
        {
            if (result.get(i).price>this.balance)
            {
                result.remove(i);
                i--;
                end--;
            }
        }
        return result;
    }

    List<Node> getTerritory()
    {
        List<Node> result = new ArrayList<>(currentNode.ANL);
        result.add(currentNode);
        return result;
    }

    void subtractOptions(Agent agent , List<Edge> edgeList)
    {
        List<Node> territory = getTerritory();
        if (territory.contains(agent.currentNode))
        {
            agent.isInDanger=true;
        }

        int end = edgeList.size();
        for (int i=0;i!=end;i++)
        {
            if (territory.contains(edgeList.get(i).getOpposingNode(agent.currentNode)))
            {
                edgeList.remove(i);
                i--;
                end--;
            }
        }

    }

    List<Edge> getSubtractedOptions(Agent agent , List<Edge> edgeList)
    {
        List<Edge> result = new ArrayList<>(edgeList);
        subtractOptions(agent , result);
        return result;
    }



    static void initStatics(List<AIProto.Agent> everyAgent, AIProto.Team team)
    {
        allyCops = new ArrayList<>();
        allyThieves = new ArrayList<>();
        enemyCops = new ArrayList<>();
        enemyThieves = new ArrayList<>();

    }

    static void updateStatics(List<AIProto.Agent> everyAgent, AIProto.Team team)
    {

        String buffer = "";


        List<Agent> tempAllyCops = new ArrayList<>();
        List<Agent> tempAllyThieves = new ArrayList<>();
        List<Agent> tempEnemyCops = new ArrayList<>();
        List<Agent> tempEnemyThieves = new ArrayList<>();

        AIProto.Agent agent;
        for (int i=0;i!=everyAgent.size();i++)
        {

            agent = everyAgent.get(i);
            if (agent.getIsDead()){continue;}

            if (everyAgent.get(i).getTeam()==team)
            {
                if (everyAgent.get(i).getType() == AIProto.AgentType.POLICE)
                {
                    tempAllyCops.add(new Agent(agent));

                }
                else
                {
                    tempAllyThieves.add(new Agent(agent));

                }
            }
            else
            {
                if (everyAgent.get(i).getType() == AIProto.AgentType.POLICE)
                {
                    tempEnemyCops.add(new Agent(agent));

                }
                else
                {
                    tempEnemyThieves.add(new Agent(agent));

                }
            }
        }



        if (tempAllyCops.size()!=0){allyCops=tempAllyCops; }
        if (tempAllyThieves.size()!=0){allyThieves=tempAllyThieves;}
        if (tempEnemyCops.size()!=0){enemyCops=tempEnemyCops;}
        if (tempEnemyThieves.size()!=0){enemyThieves=tempEnemyThieves;}

//        buffer += "  vision : "+tempAllyCops.size()+" "+tempAllyThieves.size()+" "+tempEnemyCops.size()+" "+tempEnemyThieves.size();

//        System.out.println(buffer);

    }

    static Node getFattestOption(Node centerNode , List<Edge> edgeList,double percent)
    {
        List<Node> tempList = new ArrayList<>();

        double maxWeight=0;
        Node node;
        for (int i=0;i!=edgeList.size();i++)
        {
            node = edgeList.get(i).getOpposingNode(centerNode);

            if (node.starValue > maxWeight)
            {
                maxWeight=node.starValue;
            }
        }

        for (int i=0;i!=edgeList.size();i++)
        {
            node = edgeList.get(i).getOpposingNode(centerNode);
            if (getPercent(maxWeight,node.starValue)>percent)
            {
                tempList.add(node);
            }
        }

        return tempList.get(randInt(0,tempList.size()));
    }




}
