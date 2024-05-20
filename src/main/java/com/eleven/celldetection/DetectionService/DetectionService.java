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
import com.alibaba.fastjson.JSONObject;
import com.eleven.celldetection.util.YoloImgUtil;
import org.opencv.core.Mat;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.function.Predicate;

@Service
public class DetectionService {


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

    public JSONObject detect(MultipartFile file) throws IOException, ModelException, ModelNotFoundException{
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


            JSONObject jsonObject = new JSONObject();
            jsonObject.put("image", base64ImageString);
            jsonObject.put("resultJson",resultJson);
            return jsonObject;
        } catch (TranslateException e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to predict image.", e);
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }


}
