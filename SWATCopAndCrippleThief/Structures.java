package ir.sharif.aic.hideandseek.ai;

public class Structures
{
    enum Errors{
        NodeConstructionException("NodeConstructionException: Node Map is already constructed and closed!"),
        NodeMapConstructionException("NodeMapConstructionException: Node Map is NOT constructed! therefore cannot be accessed!"),
        NodeNotFoundException("NodeNotFoundException: The given nodeId were not found in the Node Map!"),
        NodeInitializationException("NodeInitializationException: The given node is not initialized therefore cannot be accessed!"),

        EdgeDoesNotContainNode("EdgeDoesNotContainNode: The given node is not a part of the edge"),

        EdgeConstructionException("EdgeConstructionException: Edge Map is already constructed and closed!"),
        EdgeMapConstructionException("EdgeMapConstructionException: Edge Map is NOT constructed! therefore cannot be accessed!"),
        EdgeNotFoundException("EdgeNotFoundException: The given EdgeId were not found in the Edge Map!"),
        EdgeInitializationException("EdgeInitializationException: The given Edge is not initialized therefore cannot be accessed!");

        public final String text;
        Errors(String text){this.text=text;}
    }




}
