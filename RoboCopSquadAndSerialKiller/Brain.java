package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Brain
{

    // Enums & Structures
    Brain()
    {

    }

    public static Node CandidateNode;

    private static class ThiefBubble
    {
        //
        final Node center;
        final List<Node> BubbleLeakage;

        final boolean isInDanger;

        static List<Agent> copsList;
        static List<Agent> influencerList;

        public static void initStatics(List<Agent> p_copsList){
            copsList = p_copsList;
        }

        ThiefBubble(Node center){
            this.center = center;
            influencerList = new ArrayList<>();

            List<Node> options = new ArrayList<>(center.ANL);
            List<Node> selfNode = new ArrayList<>(); selfNode.add(center);

            int lastSize=(options.size()+selfNode.size());

            for (int i=0;i!=copsList.size();i++){
                copsList.get(i).subtractOptions(options);
                copsList.get(i).subtractOptions(selfNode);
                if ((options.size()+selfNode.size())!=lastSize) {influencerList.add(copsList.get(i));}

                lastSize=(options.size()+selfNode.size());
            }

            this.isInDanger = (selfNode.size() == 0);

            BubbleLeakage = options;
        }

    }

    enum ThiefState {
        FREE,
        SOFT_THREATENED,
        HARD_THREATENED,
        SOFT_PINNED,
        HARD_PINNED,
        SURROUNDED;

        static List<Agent> influencerList;
    }

    enum turnState {
        DEFINITE,
        PROBABLE,
    }

    // Methods

    public void Patrol(List<Agent> allyCops , Agent thinker){

        List<Node> bestNodes = SharedMethods.getFattestNodes(75);

        List<List<SharedMethods.Step>> stepListArray = new ArrayList<>(allyCops.size());

        int identity;
        for (int i=0;i!=allyCops.size();i++){
            identity = allyCops.get(i).whoAmI();
            if (identity<bestNodes.size()){
                stepListArray.add(SharedMethods.shortestPath(allyCops.get(i).currentNode,bestNodes.get(identity)));
            }
            else{
                stepListArray.add(SharedMethods.shortestPath(allyCops.get(i).currentNode,bestNodes.get(0)));
            }
        }

        for (int i=0;i!=allyCops.size();i++){
            identity = allyCops.get(i).whoAmI();
            if (identity==thinker.whoAmI()){
                CandidateNode = stepListArray.get(i).get(stepListArray.get(i).size()-1).node;
            }
        }


    }

    public void findPin(List<Agent> enemyThieves , List<Agent> allyCops , Agent thinker)
    {
        boolean forceBreak;
        ThiefState thiefState;
        for (int i=0;i!=enemyThieves.size();i++) {
            thiefState=getThiefState(enemyThieves.get(i),allyCops);
            forceBreak = (ThiefState.influencerList.size()==allyCops.size());

            if (thiefState==ThiefState.HARD_PINNED || thiefState==ThiefState.SOFT_PINNED){

                if (thiefState==ThiefState.HARD_PINNED){
                    System.out.println("Hard pinned thief detected {thiefId:"+enemyThieves.get(i).id+",copId:"+thinker.id+"}");
                }
                else{
                    System.out.println("Soft pinned thief detected {thiefId:"+enemyThieves.get(i).id+",copId:"+thinker.id+"}");
                }

                for (int c=0;c!=ThiefState.influencerList.size();c++){
                    if ((thinker.whoAmI()==0) && forceBreak){System.out.println("Force Break");break;}
                    if (thinker.id==ThiefState.influencerList.get(c).id){
                        CandidateNode = thinker.currentNode;
                        return;
                    }
                }
                List<SharedMethods.Step> stepList = SharedMethods.shortestPath(thinker.currentNode,enemyThieves.get(i).currentNode);
                CandidateNode = stepList.get(stepList.size()-1).node;
                return;
            }
            else if (thiefState==ThiefState.SURROUNDED){
                System.out.println("Surrounded Agent detected {thiefId:"+enemyThieves.get(i).id+",copId:"+thinker.id+"}");

                for (int c=0;c!=ThiefState.influencerList.size();c++){
                    if ((thinker.whoAmI()==0) && forceBreak){System.out.println("Force Break");break;}
                    if (thinker.id==ThiefState.influencerList.get(c).id){
                        CandidateNode = thinker.currentNode;
                        return;
                    }
                }

            }
        }
    }

    public void Approach(Agent targetThief , List<Agent> allyCops , Agent thinker)
    {// The goal is for the agents to go to Surround the target node , without populating a node more than one agent


        Node targetNode = targetThief.currentNode;
        Node resultNode=thinker.currentNode;

        List<List<SharedMethods.Step>> stepListArray = new ArrayList<>(allyCops.size());
        List<Node> L2ANL = new ArrayList<>();
        for (int i=0;i!=targetThief.currentNode.ANL.size();i++) {
            for (int c=0;c!=targetThief.currentNode.ANL.get(i).ANL.size();c++) {
                if (!L2ANL.contains(targetThief.currentNode.ANL.get(i).ANL.get(c))) {
                    L2ANL.add(targetThief.currentNode.ANL.get(i).ANL.get(c));
                }
            }
        }


        int end=L2ANL.size();
        for (int i=0;i!=end;i++) {
            for (int c=0;c!=allyCops.size();c++){
                if (allyCops.get(c).getTerritory().contains(L2ANL.get(i))){
                    L2ANL.remove(i);
                    i--;
                    end--;
                    break;
                }
            }
        }


        int identity;
        for (int i=0;i!=allyCops.size();i++) {
            identity = allyCops.get(i).whoAmI();

            if (identity>=targetNode.ANL.size()){
                if (identity<L2ANL.size()){
                    stepListArray.add(SharedMethods.shortestPath(allyCops.get(i).currentNode,L2ANL.get(identity)));
                } else{
                    stepListArray.add(new ArrayList<>());
                }
            }
            else{
                stepListArray.add(SharedMethods.shortestPath(allyCops.get(i).currentNode,targetNode.ANL.get(identity)));

            }
        }

        List<Node> reservedNodes = new ArrayList<>();
        Node stepNode;
        for (int i=0;i!=allyCops.size();i++){
            if (stepListArray.get(i).size()==0){continue;}
            stepNode = stepListArray.get(i).get(stepListArray.get(i).size()-1).node;
            if (allyCops.get(i).id==thinker.id)
            {
                if (!reservedNodes.contains(stepNode)){
                    resultNode=stepNode;
                }
                break;
            }
            if (allyCops.get(i).whoAmI()<thinker.whoAmI()){
                reservedNodes.add(stepNode);
            }
        }

        CandidateNode = resultNode;
//        return resultNode;
    }

    public void Catch(List<Agent> enemyThieves , List<Agent> allyCops , Agent thinker)
    {

        boolean hasCatcher=false;
        int firstCatchCopIdentity=0;
        int firstCatchThiefIdentity=0;

        for (int i=0;i!=enemyThieves.size();i++)
        {
            for (int c=0;c!=allyCops.size();c++)
            {
                if (allyCops.get(c).currentNode.ANL.contains(enemyThieves.get(i).currentNode)) {
                    if (!hasCatcher)
                    {
                        firstCatchCopIdentity=allyCops.get(c).whoAmI();
                        firstCatchThiefIdentity=enemyThieves.get(i).whoAmI();
                        hasCatcher=true;
                        continue;
                    }

                    if (firstCatchCopIdentity > allyCops.get(c).whoAmI()){
                        firstCatchCopIdentity=allyCops.get(c).whoAmI();
                        firstCatchThiefIdentity=enemyThieves.get(i).whoAmI();
                    }
                }
            }
        }

        if (hasCatcher) {

            int targetThiefIndex=0;
            for (int i=0;i!=enemyThieves.size();i++) {
                if ((enemyThieves.get(i).whoAmI()==firstCatchThiefIdentity)&&(thinker.whoAmI()==firstCatchCopIdentity)) {
                    CandidateNode = enemyThieves.get(i).currentNode;
                    enemyThieves.remove(i);
                    return;
                }
                if(enemyThieves.get(i).whoAmI()==firstCatchThiefIdentity){
                    targetThiefIndex=i;
                }

            }
            enemyThieves.remove(targetThiefIndex);
//            CandidateNode = thinker.currentNode;
        }


    }


    public Node scheme(List<Agent> enemyThieves , List<Agent> allyCops , int turnNumber , Agent thinker , boolean isVisible){
        
        return thinker.currentNode;
    }


    public Node think(List<Agent> enemyThieves , List<Agent> allyCops , int turnNumber , Agent thinker,boolean isVisible)
    {
        CandidateNode=thinker.currentNode;
//        System.out.println(isVisible+" isVisible");
        Collections.sort(allyCops);
        Collections.sort(enemyThieves);


        String buffer = "";


        if (enemyThieves.size()!=0) {
            Approach(enemyThieves.get(0),allyCops,thinker);
        }
        else{
            Patrol(allyCops,thinker);


            return CandidateNode;
        }

        findPin(enemyThieves,allyCops,thinker);
        Catch(enemyThieves,allyCops,thinker);

        return CandidateNode;
    }

    private void assume()
    {

    }

    private boolean isPinned(Agent thief, List<Agent> cops , int precision)
    {

        return true;
    }



    public ThiefState getThiefState(Agent thief , List<Agent> cops)
    {
        int bubbleMaxSize = 5;
        ThiefBubble.initStatics(cops);

        ThiefState.influencerList = new ArrayList<>();
        List<ThiefBubble> bubble = new ArrayList<>();
        List<Node> checkedNodesList = new ArrayList<>();
        List<Node> bubbleNodes = new ArrayList<>();

        bubble.add(new ThiefBubble(thief.currentNode));

        for (int j=0;j!=ThiefBubble.influencerList.size();j++) {
            if (!ThiefState.influencerList.contains(ThiefBubble.influencerList.get(j))) {
                ThiefState.influencerList.add(ThiefBubble.influencerList.get(j));
            }
        }

        if (bubble.get(0).BubbleLeakage.size()==0){
            if (bubble.get(0).isInDanger){
                return ThiefState.SURROUNDED;
            }
            return ThiefState.HARD_PINNED;
        }

        else if(bubble.get(0).BubbleLeakage.size()==thief.currentNode.ANL.size()){
            return ThiefState.FREE;
        }

        checkedNodesList.add(bubble.get(0).center);
        bubbleNodes.add(bubble.get(0).center);
        bubbleNodes.addAll(bubble.get(0).BubbleLeakage);

        if (bubbleNodes.size() > bubbleMaxSize) {
            if (bubble.get(0).isInDanger){
                return ThiefState.HARD_THREATENED;
            }
            return ThiefState.SOFT_THREATENED;
        }

        int end = bubbleNodes.size();
        for (int i=0;i!=end;i++)
        {
            if (!checkedNodesList.contains(bubbleNodes.get(i)))
            {
                bubble.add(new ThiefBubble(bubbleNodes.get(i)));

                for (int j=0;j!=ThiefBubble.influencerList.size();j++) {
                    if (!ThiefState.influencerList.contains(ThiefBubble.influencerList.get(j))) {
                        ThiefState.influencerList.add(ThiefBubble.influencerList.get(j));
                    }
                }

                checkedNodesList.add(bubble.get(bubble.size()-1).center);

                List<Node> pendingNodes = bubble.get(bubble.size()-1).BubbleLeakage;

                for (int c=0;c!= pendingNodes.size();c++) {
                    if (!bubbleNodes.contains(pendingNodes.get(c))){bubbleNodes.add(pendingNodes.get(c));}
                }

                end = bubbleNodes.size();

                if (bubbleNodes.size() > bubbleMaxSize) {
                    if (bubble.get(0).isInDanger){
                        return ThiefState.HARD_THREATENED;
                    }
                    return ThiefState.SOFT_THREATENED;
                }
            }
        }

        System.out.println("Bubble Node size : " + bubbleNodes.size() +" InfluencerList size : "+ThiefState.influencerList.size()+" from "+
                                    cops.size());

        return ThiefState.SOFT_PINNED;
    }







}
