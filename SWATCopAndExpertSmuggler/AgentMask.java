package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.protobuf.AIProto;

import java.util.ArrayList;
import java.util.List;

import static ir.sharif.aic.hideandseek.ai.SharedMethods.*;

public class AgentMask
{
    // PRIVATE ATTRIBUTES

    private int nodeId;
    private double balance;

    // PUBLIC ATTRIBUTES
    public final boolean balanceAccess;
    public final int id;
    public final int team;
    public final int type;

    public List<PathMask> availablePaths;
    public List<NodeMask> territory;

    // PUBLIC STATIC ATTRIBUTES


    // CONSTRUCTORS
    AgentMask(AIProto.Agent p_agent)
    {
        id = p_agent.getId();
        team = p_agent.getTeamValue();
        type = p_agent.getTypeValue();
        nodeId = p_agent.getNodeId();

        balanceAccess=false;
    }
    AgentMask(AIProto.Agent p_agent,double p_balance)
    {
        id = p_agent.getId();
        team = p_agent.getTeamValue();
        type = p_agent.getTypeValue();
        nodeId = p_agent.getNodeId();

        balanceAccess=true;
        balance=p_balance;
    }

    // ENCAPSULATION SETTERS

    // ENCAPSULATION GETTERS
    public boolean hasBalanceAccess() { return balanceAccess; }
    public int getNodeId(){return nodeId;}
    public double getBalance() {
        if (!balanceAccess) { throw new Error(Enums.Errors.BalanceAccessException.text);}
        return balance;
    }

    // PRIVATE METHODS


    // PUBLIC METHODS

    public List<PathMask> getAffordablePaths()
    {
        List<PathMask> r_pathList = new ArrayList<>();
        if (!balanceAccess) { throw new Error(Enums.Errors.BalanceAccessException.text);}
        for (int i=0;i!=availablePaths.size();i++)
        {
            if (availablePaths.get(i).price<=balance)
            {
                r_pathList.add(availablePaths.get(i));
            }
        }
        return r_pathList;
    }



    public void update(List<NodeMask> p_everyNode, List<PathMask> p_everyPath)
    {
        availablePaths = getNodeConnectedPaths(nodeId,p_everyPath);
        territory = getNodeTerritory(availablePaths,nodeId);
    }

    public void update(int p_nodeId)
    {
        nodeId=p_nodeId;
    }

    public void update(double p_balance,int p_nodeId)
    {
        if (!balanceAccess) { throw new Error(Enums.Errors.BalanceAccessException.text);}
        nodeId=p_nodeId; balance=p_balance;
    }


    // PUBLIC STATIC METHODS






}
