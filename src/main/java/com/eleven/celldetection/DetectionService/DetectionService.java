package com.eleven.celldetection.DetectionService;

import ai.djl.Application;
import ai.djl.ModelException;
import ai.djl.inference.Predictor;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.modality.cv.output.DetectedObjects;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.YoloV5Translator;
import ai.djl.repository.zoo.Criteria;
import ai.djl.repository.zoo.ModelNotFoundException;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.repository.zoo.ZooModel;
import ai.djl.training.util.ProgressBar;
import ai.djl.translate.Pipeline;
import ai.djl.translate.TranslateException;
import ai.djl.translate.Translator;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.eleven.celldetection.util.YoloImgUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Predicate;

@Service
public class DetectionService {

    public static BufferedImage convertBase64ToImage(String base64String) {
        // Check if the base64String is not null
        if (base64String == null || base64String.isEmpty()) {
            return null;
        }

        // Decode the Base64 string to binary data
        byte[] imageBytes;
        try {
            imageBytes = Base64.getDecoder().decode(base64String);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding Base64 string: " + e.getMessage());
            return null;
        }

        // Convert byte array into BufferedImage
        try (ByteArrayInputStream bis = new ByteArrayInputStream(imageBytes)) {
            return ImageIO.read(bis);
        } catch (IOException e) {
            System.err.println("Error creating image from byte array: " + e.getMessage());
            return null;
        }
    }


    public static BufferedImage matToBufferedImage(Mat mat) {
        if (mat == null || mat.empty()) {
            throw new IllegalArgumentException("Cannot convert an empty Mat object.");
        }

        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (mat.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }

        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        mat.get(0, 0, ((DataBufferByte) image.getRaster().getDataBuffer()).getData()); // Extract bytes from mat into the BufferedImage
        return image;
    }

    JSONObject resultJson = new JSONObject();
    private static Map<String, Integer> cumulativeCounts = new HashMap<>();

    public JSONObject detect(MultipartFile file , String userName , Integer age) throws IOException, ModelException, ModelNotFoundException{
        //细胞的标签
        List<String> labels = new ArrayList<>(Arrays.asList("Erythrocyte", "Lymphocyte", "Monocyte", "Neutrophil", "Eosinophil", "Juvenile cell", "other", "impurity", "Lysate", "Uncommon"));
        //创建一个临时文件来存储上传的图片
        Path tempFile = Files.createTempFile("image", "jpg");
        //将上传的图片写入到创建的临时文件中
        file.transferTo(tempFile);
        // 使用 DJL 的 ImageFactory 从临时文件中读取图像。
        Image image = ImageFactory.getInstance().fromFile(tempFile);
//        Image image = ImageFactory.getInstance().fromFile(Paths.get("build\\files\\007_0019_66_11.jpg"));
        //创建pipeline，是DJL中处理图像的一系列操作的集合
        Pipeline pipeline = new Pipeline();
        pipeline.add(new Resize(1280,1280))   //尺寸
                .add(new ToTensor());

        // 创建一个翻译器，用于将图像转换成模型可以理解的格式，并将模型的输出转换为易于理解的格式。
        Translator<Image, DetectedObjects> translator = YoloV5Translator
                .builder()
                .setPipeline(pipeline)
                .optSynset(labels)
                .optThreshold(0.5f)
                .build();


        //创建一个Criteria对象，它定义了模型加载和执行推理的条件
        // 这里的换成自己的权重路径 在 model 路径下面 ；
        Criteria<Image, DetectedObjects> criteria = Criteria.builder()
                .optApplication(Application.CV.OBJECT_DETECTION) //指定应用类型为对象检测
                .setTypes(Image.class, DetectedObjects.class)
                .optModelPath(Paths.get("D:\\WeChat Files\\wxid_v3cz1l2dh2uq22\\FileStorage\\File\\2024-05\\CellDetection\\src\\main\\java\\com\\eleven\\celldetection\\model\\best.torchscript.pt"))
                .optModelName("best.torchscript.pt")
                .optEngine("PyTorch")
                .optTranslator(translator)
                .optProgress(new ProgressBar())
                .build();


        //使用ModelZoo加载模型，这是一个根据指定的criteria找到并加载模型的工具
        try(ZooModel<Image, DetectedObjects> model = ModelZoo.loadModel(criteria);
            Predictor<Image, DetectedObjects> predictor = model.newPredictor()){
            DetectedObjects results = predictor.predict(image);
            System.out.println(results);
            Path outputDir = Paths.get("build/output");
            if (Files.exists(outputDir)) {
                System.out.println("目录已存在");
            } else {
                try {
                    // 创建目录
                    Files.createDirectories(outputDir);
                    System.out.println("目录创建成功");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Object wrappedImage = image.getWrappedImage();

            BufferedImage bufferedImage = null;
            JSONObject resultJson = new JSONObject();
            if (wrappedImage instanceof Mat) {
                bufferedImage = matToBufferedImage((Mat) wrappedImage);

            } else {
//                resultJson = YoloImgUtil.drawBoundingBoxes((BufferedImage) image.getWrappedImage(), results);
                throw new IllegalStateException("Expected a Mat but received " + wrappedImage.getClass().getSimpleName());
            }
            resultJson = YoloImgUtil.drawBoundingBoxes(bufferedImage, results);


            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
            String dateTime = now.format(formatter);
            Path imagePath = outputDir.resolve("detected-"+dateTime+".jpg");

//            image.save(Files.newOutputStream(imagePath), "jpg");

            // 使用 ImageIO.write 保存图像
            try {
                ImageIO.write(bufferedImage, "JPEG", new File(imagePath.toString()));
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("保存图像失败");
            }
//            Image img = ImageFactory.getInstance().fromFile(Paths.get("build\\output\\detected-20240505-034243.jpg"));
            // 图片转为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            baos.flush();
            byte[] imageBytes = baos.toByteArray();
            baos.close();

            String encodedImage = Base64.getEncoder().encodeToString(imageBytes);
            String base64ImageString = "data:image/jpeg;base64," + encodedImage;


            String mapString = resultJson.getString("map");
            System.out.println("shenmeqingkuang"+mapString);
            updateCumulativeCounts(mapString);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", base64ImageString);
            jsonObject.put("resultJson",resultJson);
            jsonObject.put("cumulativeMap", cumulativeCounts);

            jsonObject.put("userName", userName);
            jsonObject.put("age", age);
            return jsonObject;
        } catch (TranslateException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to predict image.", e);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }


    private void updateCumulativeCounts(String mapString) {
        Map<String, Integer> map = parseMapString(mapString);
        System.out.println("cumulativeCounts1"+cumulativeCounts);
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String key = entry.getKey();
            Integer newValue = entry.getValue();
            cumulativeCounts.merge(key, newValue, Integer::sum);
            System.out.println("cumulativeCounts"+cumulativeCounts);
        }
    }

    private Map<String, Integer> parseMapString(String mapString) {
        System.out.println("nihaihaoma----"+mapString);
        Map<String, Integer> map = new HashMap<>();
        String cleanedString = mapString.replaceAll("[{}]", "").trim();
        String[] entries = cleanedString.split(",");

        for (String entry : entries) {
            String[] keyValue = entry.split("=");
            System.out.println("keyvalue"+keyValue[0]+"  "+keyValue[1]+"  io"+keyValue.length);
            if (keyValue.length == 2) {
                try {
                    map.put(keyValue[0].trim().replaceAll("\"", ""), Integer.parseInt(keyValue[1].trim()));
                    System.out.println("NO"+map);
                } catch (NumberFormatException e) {
                    // Handle the case where the integer cannot be parsed
                    System.err.println("Failed to parse number for key: " + keyValue[0]);
                }
            }
        }
        System.out.println("mapstring"+map);
        return map;}

    public ByteArrayInputStream createPdfReport(BufferedImage bufferedImage , String  UserName ) throws Exception{
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();


        //标题
        Chunk chunk = new Chunk("Cerebrospinal Fluid Cytology Report",FontFactory.getFont(FontFactory.HELVETICA_BOLD,16));
        Paragraph paragraph = new Paragraph(chunk);
        // 设置段落的对齐方式为居中
        paragraph.setAlignment(Element.ALIGN_CENTER);
        document.add(new Paragraph("\n\n"));
        paragraph.setSpacingAfter(15f);
        document.add(paragraph);


        //用户信息
        Paragraph username = new Paragraph(UserName);
//        Paragraph userage = new Paragraph("23");
        username.setSpacingAfter(15f); // 设置文字和图片之间的间隔
        document.add(username);
//        document.add(userage);
//        document.add(new Phrase("zhangsan"));
//        document.add(new Phrase("23"));
        //??
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage,"jpg", baos);
        com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());
        pdfImage.scaleToFit(400,400);
        pdfImage.setSpacingAfter(10f);
//        float x = (PageSize.A4.getWidth() - pdfImage.getScaledWidth()) / 2;
//        float y = (PageSize.A4.getHeight() - pdfImage.getScaledHeight()) / 2;
//        pdfImage.setAbsolutePosition(x, y);
        document.add(pdfImage);
        //???????
        BaseFont baseFont = BaseFont.createFont("Helvetica","GBK", BaseFont.NOT_EMBEDDED);
        Font textFont = new Font(baseFont, 12, Font.NORMAL);
        PdfPTable cellTable = new PdfPTable(2); //2??
        cellTable.setWidthPercentage(100);
        cellTable.setWidths(new int[]{1, 1});

        //???
        cellTable.addCell(getCell("The Type of Cell", Element.ALIGN_CENTER, 1));
        cellTable.addCell(getCell("The number of Cell", Element.ALIGN_CENTER, 1));
//        document.add(cellTable);
//        for (int i = 0; i < 2; i++) {
//            PdfPCell heardCell = new PdfPCell();
//            heardCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
//            heardCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            if(i == 0){
//            heardCell.setPhrase(new Phrase("Cell", textFont));}
//            else{
//                heardCell.setPhrase(new Phrase("???????", textFont));
//            }
//            cellTable.addCell(heardCell);
//        }
        //????????
        String mapString = resultJson.getString("map");
        System.out.println(mapString);
//        Map<String, Object> cell = JSON.parseObject(mapString, new TypeReference<Map<String, Object>>() {});
//        cellTable.addCell("???????");
//        System.out.println(cellTable.getHeader());
//        cellTable.addCell("???????");

        cumulativeCounts.forEach((cellName, cellCount)
                -> {
            PdfPCell setting1 = new PdfPCell();
            PdfPCell setting2 = new PdfPCell();
            setting1.setHorizontalAlignment(Element.ALIGN_CENTER);
            setting2.setHorizontalAlignment(Element.ALIGN_CENTER);
            setting1.setPhrase(new Phrase(cellName, textFont));
            cellTable.addCell(setting1);
            setting2.setPhrase(new Phrase(String.valueOf(cellCount), textFont));
            cellTable.addCell(setting2);
//            cellTable.addCell(String.valueOf(cellCount));
        });
//                document.add(new Paragraph(cellName + ":" + cellCount));
        document.add(cellTable);
        document.close();
//        ??????????????????out.toByteArray()???ByteArrayOutputStream?е?????????????PDF??????????????
//        ????????Щ??????????ByteArrayInputStream????????????????????????????????PDF???????????????????
        return new ByteArrayInputStream(out.toByteArray());
    }
    public PdfPCell getCell(String text, int alignment, int colspan) {
        Font font = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);  // ??????????????????
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setColspan(colspan);
        cell.setPadding(10);
        cell.setBorderWidth(2);
        cell.setBackgroundColor(BaseColor.DARK_GRAY); // ??????
        cell.setMinimumHeight(30);
        System.out.println("HEIGHT"+cell.getHeight()+"---"+cell.getPaddingRight());
        return cell;
    }
    public void resetCounts(){
        cumulativeCounts.clear(); //????????
    }



}
