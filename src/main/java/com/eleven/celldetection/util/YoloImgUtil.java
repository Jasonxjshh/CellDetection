package com.eleven.celldetection.util;

import ai.djl.modality.cv.output.BoundingBox;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.util.RandomUtils;
import com.alibaba.fastjson.JSONObject;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class YoloImgUtil {

    public static JSONObject drawBoundingBoxes(BufferedImage image, DetectedObjects detections){
        Map<String, Color> colorMap = new HashMap<>();
        String[] classNames = {"Erythrocyte", "Lymphocyte", "Monocyte", "Neutrophil", "Eosinophil", "Juvenile cell", "other", "impurity", "Lysate", "Uncommon"};
        Random random = new Random();
        for (String className : classNames) {
            // 为每个类随机生成一个颜色
            colorMap.put(className, new Color(random.nextFloat(), random.nextFloat(), random.nextFloat()));
        }

        Map<String, Integer> map = new HashMap<>();

        Graphics2D g = (Graphics2D) image.getGraphics();
        int stroke = 2;
        g.setStroke(new BasicStroke(stroke));
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        List<DetectedObjects.DetectedObject> list = detections.items();
        JSONObject jsonObject = new JSONObject();
        for(DetectedObjects.DetectedObject result : list){

            String className = result.getClassName();
            BoundingBox box = result.getBoundingBox();
            g.setPaint(colorMap.get(className).darker());
//            g.setPaint(randomColor().darker());
//            g.setPaint(Color.RED);
            ai.djl.modality.cv.output.Rectangle rectangle = box.getBounds();
            int x = (int) (rectangle.getX());
            int y = (int) (rectangle.getY());
            g.drawRect(
                    x,
                    y,
                    (int) (rectangle.getWidth()),
                    (int) (rectangle.getHeight())
            );
            System.out.println(x);
            drawText(g, className, x, y, stroke, 4);

            if(map.containsKey(className)) {
                map.put(className, map.get(className) + 1);
            }else {
                map.put(className, 1);
            }


            jsonObject.put("map",map.toString());

        }
        g.dispose();
        return  jsonObject;
    }

//    public static void main(String[] args) {
//        Map map= new HashMap();
//        map.put("1","1");
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("a1",map);
//        System.out.println(jsonObject.toJSONString());
//    }

    private static void drawText(Graphics2D g, String text, int x, int y, int stroke, int padding){
        FontMetrics metrics = g.getFontMetrics();
        x += stroke / 2;
        y += stroke / 2;
        int width = metrics.stringWidth(text) + padding * 2 - stroke / 2;
        int height = metrics.getHeight() + metrics.getDescent();
        int ascent = metrics.getAscent();
        Rectangle background = new Rectangle(x, y, width,height);
        g.fill(background);
        g.setPaint(Color.WHITE);
        g.drawString(text, x + padding, y + ascent);
    }

}