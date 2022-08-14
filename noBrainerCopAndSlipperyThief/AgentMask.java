package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.List;
import java.util.Vector;

public class AgentMask
{
    // NON-STATIC ATTRIBUTES
    AIProto.Agent agent;
    private boolean balanceAccess;
    private double  balance;

    private Vector<PathMask> affordablePaths;
    private Vector<PathMask> unaffordablePaths;

    // STATIC ATTRIBUTES
    static boolean staticsInitializationState=false;
    static int allyTeamID;

    private static Vector<PathMask> everyPath;
    private static Vector<AgentMask> everyAgent;

    private static Vector<AgentMask> enemyCops;
    private static Vector<AgentMask> allyCops;
    private static Vector<AgentMask> enemyThieves;
    private static Vector<AgentMask> allyThieves;

    // CONSTRUCTORS
    AgentMask(AIProto.Agent p_agent)
    {
        balanceAccess=false;
        agent = p_agent;

        update();
    }

    // SETTERS

    public void setBalance(double p_balance)
    {
        balance = p_balance;
        balanceAccess = true;
    }

    // GETTERS
    public double getBalance()
    {
        if (!balanceAccess){Log.throwError(Enums.logErrors.BalanceAccessException.ordinal());}
        return balance;
    }

    public Vector<PathMask> getAffordablePaths(){return affordablePaths;}
    public Vector<PathMask> getUnaffordablePaths(){return unaffordablePaths;}



    // NON-STATIC METHODS

    public void update()
    {
        fillPaths();
    }

    public double spend(double p_amount)
    {
        if (!balanceAccess){Log.throwError(Enums.logErrors.BalanceAccessException.ordinal());}
        if (p_amount>balance){Log.throwError(Enums.logErrors.LowBalanceException.ordinal());}
        balance-=p_amount;
        return balance;
    }

    public int moveTo(PathMask p_pathMask)
    {
        if (!balanceAccess){Log.throwError(Enums.logErrors.BalanceAccessException.ordinal());}
        if (p_pathMask.path.getPrice()>balance){Log.throwError(Enums.logErrors.LowBalanceException.ordinal());}
        spend(p_pathMask.path.getPrice());
        return (p_pathMask.getOtherSideId(agent.getNodeId()));
    }

    public Vector<IntMask> getTerritory()
    {
        Vector<IntMask> r_IntMaskVector = new Vector<>();
        r_IntMaskVector.add(new IntMask(agent.getNodeId()));

        for (int i=0;i!=everyPath.size();i++)
        {
            if (everyPath.get(i).containsNode(agent.getNodeId())){r_IntMaskVector.add((new IntMask(everyPath.get(i).getOtherSideId(agent.getNodeId()))));}
        }

        return r_IntMaskVector;
    }

    public Vector<PathMask> getAvailablePaths()
    {
        Vector<PathMask> r_PathMaskVector = new Vector<>();

        for (int i=0;i!=everyPath.size();i++)
        {
            if (everyPath.get(i).containsNode(agent.getNodeId())){r_PathMaskVector.add((everyPath.get(i)));}
        }

        return r_PathMaskVector;
    }

    private void fillPaths()
    {
        affordablePaths=new Vector<>();
        unaffordablePaths=new Vector<>();
        for (int i=0;i!=everyPath.size();i++)
        {
            if (everyPath.get(i).containsNode(agent.getNodeId()))
            {
                if (balance>=everyPath.get(i).path.getPrice())
                {
                    affordablePaths.add((everyPath.get(i)));
                }
                else
                {
                    unaffordablePaths.add((everyPath.get(i)));
                }
            }
        }


    }

    // STATIC METHODS


    public static Vector<AgentMask> getEnemyCops(){return enemyCops;}
    public static Vector<AgentMask> getEnemyThieves(){return enemyThieves;}
    public static Vector<AgentMask> getAllyCops(){return allyCops;}
    public static Vector<AgentMask> getAllyThieves(){return allyThieves;}

    public static void initializeStatics(List<AIProto.Agent> p_AgentList,int p_allyTeamID)
    {
        staticsInitializationState=true;
        allyTeamID=p_allyTeamID;
        everyAgent = getVector(p_AgentList);

        allyCops=new Vector<>();
        enemyCops=new Vector<>();
        allyThieves=new Vector<>();
        enemyThieves=new Vector<>();

        groupAgents();
    }

    public static void updateStatics(List<AIProto.Agent> p_AgentList)
    {
        if (!staticsInitializationState){Log.throwError(Enums.logErrors.InitializationLackException.ordinal());}
        everyAgent = getVector(p_AgentList);
        groupAgents();
    }

    private static void groupAgents()
    {
        allyThieves.clear();
        allyCops.clear();
        enemyThieves.clear();
        enemyCops.clear();
        for (int i=0;i!=everyAgent.size();i++)
        {
            if (everyAgent.get(i).agent.getTeam().getNumber()==allyTeamID)
            {
                if (everyAgent.get(i).agent.getType().getNumber()== AIProto.AgentType.THIEF_VALUE)
                {
                    allyThieves.add(everyAgent.get(i));
                }
                else
                {
                    allyCops.add(everyAgent.get(i));
                }
            }
            else
            {
                if (everyAgent.get(i).agent.getType().getNumber()== AIProto.AgentType.THIEF_VALUE)
                {
                    enemyThieves.add(everyAgent.get(i));
                }
                else
                {
                    enemyCops.add(everyAgent.get(i));
                }
            }

        }
    }


    public static String reportVisibleAgents()
    {
        if (!staticsInitializationState){Log.throwError(Enums.logErrors.InitializationLackException.ordinal());}

        return "EnemyCops: "+enemyCops.size()+" AllyCops: "+allyCops.size()+" EnemyThieves: "+enemyThieves.size()+" AllyThieves: "+allyThieves.size();
    }

    public static Vector<AgentMask> getVector(List<AIProto.Agent> p_AgentList)
    {
        Vector<AgentMask> r_AgentMaskVector = new Vector<>();

        for (int i=0;i!=p_AgentList.size();i++)
        {
            r_AgentMaskVector.add(new AgentMask(p_AgentList.get(i)));
        }

        return r_AgentMaskVector;
    }


    public static void insertEveryPath(List<AIProto.Path> p_everyPath)
    {
        everyPath = new Vector<>();
        for (int i=0;i!=p_everyPath.size();i++)
        {
            everyPath.add(new PathMask(p_everyPath.get(i)));
        }
    }

    /*
    subtracts a given path vector from every node colliding with it ( not the ones colliding with agent id) and then returns it
    */

    public static void subtractVector(int p_agentCurrentNode,Vector<PathMask> p_agentPathVector,Vector<IntMask> p_nodeIDVector)
    {

        for (int c=0;c!=p_nodeIDVector.size();c++)
        {
            for (int i=0;i!=p_agentPathVector.size();i++)
            {
                if
                (
                        (p_agentPathVector.get(i).containsNode(p_nodeIDVector.get(c).number))
                        &
                        (p_nodeIDVector.get(c).number!=p_agentCurrentNode)
                )
                {
                    p_agentPathVector.removeElementAt(i);
                    i--;
                    break;
                }
            }
        }

    }



}
