package utils;

/**
 * This enumeration manages the client.model.character.gameElement.character's movement.
 *
 * Created by Giulia Lucchi on 28/06/2017.
 */
public enum Direction {
    RIGHT("right"),
    LEFT("left"),
    DOWN("down"),
    UP("up");

    private String direction;

    Direction(final String direction) {
        this.direction = direction;
    }

    /**
     *
     *  @return the client.model.character.gameElement.character's direction
     * */
    public String getDirection() {
        return this.direction;
    }
}
