/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

/**
 *
 * @author samnishita
 */
public class MissedShotIcon extends Region {

    private Line line1;
    private Line line2;
    private Rectangle rect;
    private Shot shot;

    public MissedShotIcon(double transX, double transY, double height, double lineLength, double lineThickness, double extraX, double extraY, Shot shot) {
        line1 = new Line(height * lineLength, height * lineLength * -1, height * lineLength * -1, height * lineLength);
        line2 = new Line(height * lineLength, height * lineLength * -1, height * lineLength * -1, height * lineLength);
        rect = new Rectangle(4 * height * lineLength, 4 * height * lineLength);
        rect.setFill(Color.TRANSPARENT);
        rect.setTranslateX(height * transX);
        rect.setTranslateY(height * transY);
        System.out.println(rect.getLayoutBounds().getHeight());
        System.out.println(rect.getLayoutBounds().getWidth());
        System.out.println(rect.getX());
        System.out.println(rect.getY());
        line1.setTranslateX(height * transX + extraX);
        line1.setTranslateY(height * transY + extraY);
        line2.setTranslateX(height * transX + extraX);
        line2.setTranslateY(height * transY + extraY);
        line1.setStrokeWidth(height * lineThickness);
        line2.setStrokeWidth(height * lineThickness);
        line1.setFill(Color.RED);
        line2.setFill(Color.RED);
        line1.setStroke(Color.RED);
        line2.setStroke(Color.RED);
        line2.setRotationAxis(Rotate.Y_AXIS);
        line2.setRotate(180);
        this.shot = shot;

    }

    public Line getLine1() {
        return line1;
    }

    public Line getLine2() {
        return line2;
    }

    public Rectangle getRect() {
        return rect;
    }

    public Shot getShot() {
        return shot;
    }

}
