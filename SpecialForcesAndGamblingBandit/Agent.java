package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.security.spec.RSAKeyGenParameterSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;
import static java.lang.Double.NaN;

public class Agent implements Comparable
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

    static double initialBalance;
    static double copsIncome;
    static double thievesIncome;

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

    void subtractOptions(Agent agent , List<Edge> edgeList)
    {
        List<Node> territory;
        if (this.balance==-1) {
            territory = this.getTerritory();
        }
        else
        {
            territory = new ArrayList<>();
            territory.add(this.currentNode);
            List<Edge> options = this.getAffordablePaths();

            if (options.size()!=(this.getTerritory().size()-1))
            {
                System.out.println("Enemy Cop Money Shortage Detected : Balance : "+this.balance+" , options : "+options.size()+" ANL size : "
                                        +this.currentNode.ANL.size() );
            }

            for (int i=0;i!=options.size();i++) {
                territory.add(options.get(i).getOpposingNode(this.currentNode));
            }

//            territory = this.getTerritory();
        }

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


    private static void updateAgents(List<Agent> lastAgentList ,List<Agent> recentAgentList)
    {


    }

    static void initStatics(List<AIProto.Agent> everyAgent, AIProto.Team team, AIProto.GameView view)
    {
        allyCops = new ArrayList<>();
        allyThieves = new ArrayList<>();
        enemyCops = new ArrayList<>();
        enemyThieves = new ArrayList<>();

        initialBalance = view.getBalance();
        copsIncome = view.getConfig().getIncomeSettings().getPoliceIncomeEachTurn();
        thievesIncome = view.getConfig().getIncomeSettings().getThievesIncomeEachTurn();

    }

    static void updateStatics(List<AIProto.Agent> everyAgent, AIProto.Team team,Agent viewer) {

        List<Agent> tempAllyCops = new ArrayList<>();
        List<Agent> tempAllyThieves = new ArrayList<>();
        List<Agent> tempEnemyCops = new ArrayList<>();
        List<Agent> tempEnemyThieves = new ArrayList<>();

        AIProto.Agent agent;
        for (int i=0;i!=everyAgent.size();i++) {
            agent = everyAgent.get(i);
            if (agent.getId()==viewer.id){continue;}
            if (agent.getIsDead()){continue;}

            if (everyAgent.get(i).getTeam()==team) {
                if (everyAgent.get(i).getType() == AIProto.AgentType.POLICE) {tempAllyCops.add(new Agent(agent));}
                else {tempAllyThieves.add(new Agent(agent));}
            }
            else {
                if (everyAgent.get(i).getType() == AIProto.AgentType.POLICE) {tempEnemyCops.add(new Agent(agent));}
                else {tempEnemyThieves.add(new Agent(agent));}
            }
        }

        if (tempAllyCops.size()!=0){allyCops=tempAllyCops; }
        if (tempAllyThieves.size()!=0){allyThieves=tempAllyThieves;}
        if (tempEnemyCops.size()!=0){

            double price;
            double income = copsIncome;
            double newBalance;
            Agent lastAgent;
            Agent recentAgent;

            if (enemyCops.size()!=0) {

                for (int i=0;i!=enemyCops.size();i++) {
                    lastAgent = enemyCops.get(i);

                    for (int c=0;c!=tempEnemyCops.size();c++) {
                        recentAgent = tempEnemyCops.get(c);
                        if (lastAgent.id==recentAgent.id) {
                            price = 0;
                            if (lastAgent.currentNode.id!=recentAgent.currentNode.id) {
                                try {
                                    price = Edge.findEdgeByNodes(lastAgent.currentNode,recentAgent.currentNode).price;
                                }
                                catch (Error e) {
                                    String Payam = "["+lastAgent.toString()+"] ["+recentAgent.toString()+"]";
                                    System.out.println("Unexpected Error : " + Payam +"\n"+ e);
                                }
                            }

                            newBalance = lastAgent.balance + (income*2) - price;
                            tempEnemyCops.get(c).balance = newBalance;
                            break;
                        }
                    }

                }

                enemyCops=tempEnemyCops;

            }
            else
            {
                enemyCops=tempEnemyCops;
                for (int i=0;i!=enemyCops.size();i++) {
                    enemyCops.get(i).balance = initialBalance;
                }
            }







        }
        if (tempEnemyThieves.size()!=0){enemyThieves=tempEnemyThieves;}

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

    public String toString(){return "<agentId:"+this.id+"> <nodeId:"+this.currentNode.id+"> <balance:"+this.balance+">";}


    static Node getFurthestNode(Agent agent,List<Agent> agentList,List<Node> options) {
        return agent.currentNode;
    }





    @Override
    public int compareTo(Object o) {
        return  (Integer.compare(this.id, ((Agent) o).id));
    }


}
