package ir.sharif.aic.hideandseek.ai;

import java.util.ArrayList;
import java.util.List;

final public class SharedMethods
{
    public static class Step
    {
        Node node;
        Node parentNode;
        int layer;
        Step(Node node,Node parentNode,int layer)
        {
            this.node=node;
            this.parentNode=parentNode;
            this.layer=layer;
        }

        public String toString()
        {
            return "<node:"+node.id+"> <parentNode:"+parentNode.id+"> <layer:"+layer+">";
        }
    }

    public static class StepMask
    {
        List<Step> stepList;
        Node fromNode;
        Node toNode;

        StepMask(List<Step> stepList , Node fromNode , Node toNode)
        {
            this.stepList = stepList;
            this.fromNode = fromNode;
            this.toNode = toNode;

//            System.out.println("Added the shortestPath from node "+fromNode.id+" to node "+toNode.id+" , to the solution collection!");
            stepListList.add(this);
        }

        static int getIndexIfExists(Node fromNode,Node toNode)
        {
            for (int i=0;i!=stepListList.size();i++)
            {
                if ((fromNode==stepListList.get(i).fromNode) && (toNode==stepListList.get(i).toNode))
                {
//                    System.out.println("The shortestPath from node "+fromNode.id+" , to node "+toNode.id+" , already exists in the solution collection!");
                    return i;
                }
            }
//            System.out.println("The shortestPath from node "+fromNode.id+" , to node "+toNode.id+" , is new and does not exist in the solution collection!");
            return -1;
        }



    }

    private static List<StepMask> stepListList = new ArrayList<>();


    static double getPercent(double All,double part)
    {
        double A = 100 / All;
        return part * A;
    }

    static List<Step> shortestPath(Node source,Node destination)
    {
        int index = StepMask.getIndexIfExists(source,destination);

        if (index!=-1)
        {
            return stepListList.get(index).stepList;
        }


        List<Step> stepList1 = new ArrayList<>();

        if (source==destination)
        {
            stepList1.add(new Step(destination,source,0));
            return stepList1;
        }

        List<Step> stepList = new ArrayList<>();

        int layer = 0;

        Step step;

        for (int i=0;i!=source.ANL.size();i++)
        {
            step = new Step(source.ANL.get(i),source,layer);
            if (step.node==destination)
            {
                stepList1.add(new Step(destination,source,0));
                return stepList1;
            }
            stepList.add(step);
        }

        boolean isRepeated=false;
        boolean breaker=false;
        int end = stepList.size();
        for (int i=0;i!=end;i++)
        {

            for (int c=0;c!=stepList.get(i).node.ANL.size();c++)
            {
                isRepeated=false;
                step = new Step(stepList.get(i).node.ANL.get(c),stepList.get(i).node,stepList.get(i).layer+1);

                for (int j=0;j!=stepList.size();j++)
                {
                    if (stepList.get(j).node==step.node){isRepeated=true;break;}
                }

                if (!isRepeated)
                {
                    stepList.add(step);
                    end++;
                    if (step.node==destination){breaker=true;break;}
                }

            }
            if (breaker){break;}
        }


        if (!breaker){
            throw new Error("Could not find the path to destination , total tries : " + stepList.size());
        }

        stepList1 = new ArrayList<>();

        for (int i=0;i!=stepList.size();i++)
        {
            if (stepList.get(i).node==destination)
            {
                stepList1.add(stepList.get(i));
            }
        }

        Step lastStep=stepList1.get(0); //error prone
        breaker=false;

        do {
            for (int i = 0; i != stepList.size(); i++) {
                if (stepList.get(i).node == lastStep.parentNode) {
                    lastStep = stepList.get(i);
                    stepList1.add(lastStep);
                    if (lastStep.layer == 0) {
                        breaker = true;
                        break;
                    }
                    break;
                }
            }
        } while (!breaker);


        int end1 = stepList1.size();

        new StepMask(stepList1,source,destination);
        new StepMask(stepList1,destination,source);


        return stepList1;

    }


    static List<Node> getFattestNodes(double percent)
    {
        List<Node> result = new ArrayList<>();

        double maxWeight = 0;
        Node node;
        double weight;
        for (int i=0;i!=Node.everyNode.size();i++)
        {
            node = Node.everyNode.get(i);
            weight = node.starValue;

            if (weight>maxWeight) {maxWeight=weight;}
        }

        for (int i=0;i!=Node.everyNode.size();i++)
        {
            node = Node.everyNode.get(i);
            weight = node.starValue;

            if (getPercent(maxWeight,weight)>percent)
            {
                result.add(node);
            }
        }


        return result;
    }

    static int randInt(int min, int max) {

        return (int) ((Math.random() * (max - min)) + min);

    }
}
