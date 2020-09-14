/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mainapp;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.transform.Rotate;

/**
 *
 * @author samnishita
 */
public class MissedShotIcon {

    private Line line1;
    private Line line2;

    public MissedShotIcon(double transX, double transY, double height, double lineLength, double lineThickness) {
        line1 = new Line(height * lineLength, height * lineLength * -1, height * lineLength * -1, height * lineLength);
        line2 = new Line(height * lineLength, height * lineLength * -1, height * lineLength * -1, height * lineLength);
        line1.setTranslateX(height * transX);
        line1.setTranslateY(height * transY);
        line2.setTranslateX(height * transX);
        line2.setTranslateY(height * transY);
        line1.setStrokeWidth(height * lineThickness);
        line2.setStrokeWidth(height * lineThickness);
        line1.setFill(Color.RED);
        line2.setFill(Color.RED);
        line1.setStroke(Color.RED);
        line2.setStroke(Color.RED);

        line2.setRotationAxis(Rotate.Y_AXIS);
        line2.setRotate(180);

    }

    public Line getLine1() {
        return line1;
    }

    public Line getLine2() {
        return line2;
    }

}
