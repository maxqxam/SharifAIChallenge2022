package ir.sharif.aic.hideandseek.ai;

public class Log
{

    public static String[] errors = {
        "BalanceAccessException: You do not have access to this agent's Balance!",
        "LowBalanceException: You're balance is not enough to make this action!",
        "InvalidParameterException: The given Node is not a part of this Path!",
        "InitializationLackException: The static values aren't initialized therefore they can't be accessed!"
    };



   public static void throwError(int p_errorId)
   {
       throw new Error(errors[p_errorId]);
   }

}
