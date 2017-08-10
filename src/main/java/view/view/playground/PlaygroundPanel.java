package view.view.playground;


import clientModel.model.gameElement.*;
import clientModel.model.utilities.Point;
import clientModel.model.utilities.BlocksImages;
import view.view.utils.ImagesUtils;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static clientModel.model.utilities.BlocksImages.*;


/**
 * This class strengthens the BasePlayground
 * Created by Manuel Bottax and chiaravarini on 04/07/2017.
 */

public class PlaygroundPanel extends BasePlaygroundPanel implements PlaygroundView {

    public PlaygroundPanel(PlaygroundSettings playgroundsettings){

        super(playgroundsettings);
    }

    /**
     * Shows all labyrinth blocks in their specified position
     * @param blockList to render
     */
    @Override
    public void renderBlockList(List<Block> blockList){
        for ( Block b : blockList) {
            super.renderBlock((int) b.position().x(), (int) b.position().y(), chooseBlockImage(b, blockList));
        }
    }

    /**
     * Shows all etable elemnts (dots and fruits) in the specified position
     * @param eatableList to render
     */
    @Override
    public void renderEatableList(List<Eatable> eatableList){
        for (Eatable e : eatableList){
            if (e instanceof Dot){
                super.renderDot((int) e.position().x(), (int) e.position().y());
            }
            else if (e instanceof Pill) {
                super.renderPill((int) e.position().x(), (int) e.position().y());
            }
            else if (e instanceof Fruit) {
                super.renderFruit((int) e.position().x(), (int) e.position().y(), ImagesUtils.getFruitsImage(e));
            }
        }
    }

    private boolean lookAtLeft(Block block, List<Block> blockList){
        return lookAt(blockList,  p->((int)p.x() == (int) block.position().x()-1 && (int)p.y() == (int) block.position().y()));
    }

    private boolean lookAtRight(Block block, List<Block> blockList){
        return lookAt(blockList,  p->((int)p.x() == (int) block.position().x()+1 && (int)p.y() == (int) block.position().y()));
    }

    private boolean lookAtTop(Block block, List<Block> blockList){
        return lookAt(blockList,  p->((int)p.x() == (int) block.position().x() && (int)p.y() == (int) block.position().y()-1));
    }

    private boolean lookAtBottom(Block block, List<Block> blockList){
        return lookAt(blockList,  p->((int)p.x() == (int) block.position().x() && (int)p.y() == (int) block.position().y()+1));
    }

    private boolean lookAt(List<Block> blockList, Predicate<Point> predicate){  //Strategy
        return blockList
                .stream()
                .map(b->b.position())
                .filter(predicate)
                .collect(Collectors.toList())
                .size() >= 1;
    }

    private BlocksImages chooseBlockImage(Block block, List<Block> list){

        boolean isHorizontal = lookAtLeft(block,list) && lookAtRight(block,list) && !lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isLeftEnd = !lookAtLeft(block,list) && lookAtRight(block,list) && !lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isVerticalLeft = lookAtLeft(block,list) && !lookAtRight(block, list) && lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isLowerEnd = !lookAtLeft(block,list) && !lookAtRight(block,list) && lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isLowerLeftCorner = !lookAtLeft(block,list) && lookAtRight(block,list) && lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isHorizontalBottom = lookAtLeft(block,list) && lookAtRight(block,list) && !lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isLowerRightCorner = lookAtLeft(block,list) && !lookAtRight(block,list) && lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isRightEnd = lookAtLeft(block,list) && !lookAtRight(block,list) && !lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isVerticalRight = !lookAtLeft(block,list) && lookAtRight(block,list) && lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isUpperEnd = !lookAtLeft(block,list) && !lookAtRight(block,list) && !lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isUpperLeftCorner = !lookAtLeft(block,list) && lookAtRight(block,list) && !lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isHorizontalUp = lookAtLeft(block,list) && lookAtRight(block,list) && lookAtTop(block,list) && !lookAtBottom(block,list);
        boolean isUpperRightCorner = lookAtLeft(block,list) && !lookAtRight(block,list) && !lookAtTop(block,list) && lookAtBottom(block,list);
        boolean isVertical = !lookAtLeft(block,list) && !lookAtRight(block,list) && lookAtTop(block,list) && lookAtBottom(block,list);

        if(isHorizontal){
            return HORIZONTAL;

       } else if(isLeftEnd){
            return LEFT_END;

        }else if(isVerticalLeft){
           return VERTICAL_LEFT;

       }else if(isLowerEnd){
            return LOWER_END;

        }else if(isLowerLeftCorner){
           return LOWER_LEFT_CORNER;

        }else if(isHorizontalBottom){
            return HORIZONTAL_BOTTOM;

        }else if(isLowerRightCorner){
            return LOWER_RIGHT_CORNER;

        }else if(isRightEnd){
            return RIGHT_END;

        }else if(isVerticalRight){
            return VERTICAL_RIGHT;

        }else if(isUpperEnd) {
            return UPPER_END;

        }else if(isUpperLeftCorner){
            return UPPER_LEFT_CORNER;

        }else if(isHorizontalUp){
            return HORIZONTAL_UP;

        }else if(isUpperRightCorner){
            return UPPER_RIGHT_CORNER;

        }else if(isVertical){
            return VERTICAL;
        }else {
            return SINGLE;
        }
    }
}
