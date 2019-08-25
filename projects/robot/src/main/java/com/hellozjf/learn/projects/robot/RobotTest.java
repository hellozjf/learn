package com.hellozjf.learn.projects.robot;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.Random;

/**
 * @author hellozjf
 */
public class RobotTest {


    public static void main(String[] args) throws AWTException {

        Robot robot = new Robot();
        Random random = new Random();
        int a = 0;
        robot.delay(3000);

        robot.mouseMove(1200, 700);
        a = Math.abs(random.nextInt()) % 100 + 50;
        robot.delay(a);

        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);

        a = Math.abs(random.nextInt()) % 50 + 50;
        robot.delay(a);

        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }
}
