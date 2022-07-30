package ir.sharif.aic.hideandseek.ai;

import ir.sharif.aic.hideandseek.client.Phone;
import ir.sharif.aic.hideandseek.protobuf.AIProto.GameView;


public class PoliceAI extends AI {


    public PoliceAI(Phone phone) {
        this.phone = phone;
    }



    @Override
    public int getStartingNode(GameView view) {
        return 0;
    }


    @Override
    public int move(GameView view) {
        return 0;
    }
}
