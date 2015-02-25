package lab4;

import lab4.client.GomokuClient;
import lab4.data.GomokuGameState;
import lab4.gui.GomokuGUI;

public class GomokuMain {
    public static void main(String[] args) {
        int port = 13337;
        if(args.length > 0) {
            String p = args[0];
            port = Integer.parseInt(p);
        }

        GomokuClient gc = new GomokuClient(port);
        GomokuGameState ggs = new GomokuGameState(gc);
        GomokuGUI gg = new GomokuGUI(ggs, gc);
    }
}