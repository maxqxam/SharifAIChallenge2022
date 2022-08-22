package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.getPercent;
import static ir.sharif.aic.hideandseek.ai.SharedMethods.randInt;

public class Agent implements Comparable
{
    final int id;
    Node currentNode;
    double balance;


    boolean isInDanger=false;

    List<Edge> affordableOptions;
    List<Node> affordableOptionsAsNodes;

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

    Agent(Agent agent)
    {
        Node.constructionCheck();
        this.id = agent.id;
        this.currentNode = agent.currentNode;
        this.balance = agent.balance;
        this.team = agent.team;
        this.type = agent.type;
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
        this.affordableOptions = getAffordablePaths();
        this.affordableOptionsAsNodes = new ArrayList<>();
        for (int i=0;i!=affordableOptions.size();i++){
            affordableOptionsAsNodes.add(affordableOptions.get(i).getOpposingNode(this.currentNode));
        }
        affordableOptionsAsNodes.add(this.currentNode);

    }


    public List<Agent> getLegend()
    {
        List<Agent> legend;

        if (type==AIProto.AgentType.POLICE){
            legend = new ArrayList<>(Agent.allyCops);
        }
        else{
            legend = new ArrayList<>(Agent.allyThieves);
        }
        legend.add(this);

        Collections.sort(legend);

        return legend;
    }

    public int whoAmI() {
        int result=0;

        List<Agent> legend = getLegend();

        int i;
        for (i=0;i!= legend.size();i++)
        {
            if (legend.get(i).id==this.id)
            {
                result=i;
                break;
            }
        }

        return result;
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

    void subtractOptions(List<Node> nodeList)
    {
        List<Node> territory = getTerritory();

        int end = nodeList.size();
        for (int i=0;i!=end;i++)
        {
            if (territory.contains(nodeList.get(i)))
            {
                nodeList.remove(i);
                i--;
                end--;
            }
        }
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

    static void updateStatics(List<AIProto.Agent> everyAgent, AIProto.Team team , Agent self)
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

        boolean shouldAddSelf = true;

        if (tempAllyCops.size()!=0){
            allyCops=tempAllyCops;
            if ((self.type==AIProto.AgentType.POLICE)) {
                for (int i=0;i!=allyCops.size();i++){
                    if (allyCops.get(i).id==self.id){
                        shouldAddSelf=false;
                    }
                }
                if (shouldAddSelf){allyCops.add(self);}
            }
        }
        if (tempAllyThieves.size()!=0){
            allyThieves=tempAllyThieves;
            if ((self.type==AIProto.AgentType.THIEF)) {
                for (int i=0;i!=allyThieves.size();i++){
                    if (allyThieves.get(i).id==self.id){
                        shouldAddSelf=false;
                    }
                }

                if (shouldAddSelf){allyThieves.add(self);}

            }
        }
        if (tempEnemyCops.size()!=0){enemyCops=tempEnemyCops;}
        if (tempEnemyThieves.size()!=0){enemyThieves=tempEnemyThieves;}

//        buffer += "  vision : "+tempAllyCops.size()+" "+tempAllyThieves.size()+" "+tempEnemyCops.size()+" "+tempEnemyThieves.size();

//        System.out.println(buffer);

    }

    static List<Node> getFattestOptions(Node centerNode , List<Edge> edgeList,double percent)
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

        return tempList;
    }



    public void printReport(AIProto.GameView view , String Payam)
    {
        String cop = "POLICE"; String thief = "THIEF";
        String first = "FIRST"; String second = "SECOND";
        String team; String type;

        if (this.type == AIProto.AgentType.POLICE) {type = cop;}
        else {type = thief;}

        if (this.team == AIProto.Team.FIRST) {team = first;}
        else {team = second;}

        String id = String.valueOf(this.id);
        String turnNumber = String.valueOf(view.getTurn().getTurnNumber());


        String finalPayam = "<TURN NUMBER:"+turnNumber+"> "+
                            "<TEAM:"+team+"> <TYPE:"+type+"> <id:"+id+"> <Payam: "+Payam+">";

        System.out.println(finalPayam);

    }

    @Override
    public int compareTo(Object o) {
        return  (Integer.compare(this.id, ((Agent) o).id));
    }
}
