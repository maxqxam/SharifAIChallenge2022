package ir.sharif.aic.hideandseek.ai;

public class Enums
{

    enum Errors
    {
        NodeNotFoundException("NodeNotFoundException: The requested node does not exist!"),
        NodeNotPartOfException("NodeNotPartOfException: The given node is a not a part of this Path!"),
        BalanceAccessException("BalanceAccessException: You do not have access to this agent's balance!");

        public final String text;
        Errors(String p_text){
            text = p_text;
        }
    }



}
