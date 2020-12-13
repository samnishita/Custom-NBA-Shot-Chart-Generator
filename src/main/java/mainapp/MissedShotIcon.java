/*
 * Copyright 2020 Sam Nishita.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
