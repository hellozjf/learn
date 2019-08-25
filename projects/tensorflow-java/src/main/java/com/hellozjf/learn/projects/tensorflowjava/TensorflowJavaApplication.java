package com.hellozjf.learn.projects.tensorflowjava;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.tensorflow.SavedModelBundle;
import org.tensorflow.Tensor;

import java.nio.FloatBuffer;
import java.nio.LongBuffer;

@SpringBootApplication
@Slf4j
public class TensorflowJavaApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(TensorflowJavaApplication.class, args);
    }

    public SavedModelBundle load(String path) throws Exception {
        long t1 = System.currentTimeMillis();
//        Resource resource = new ClassPathResource(path);
        Resource resource = new FileSystemResource(path);
        SavedModelBundle savedModelBundle = SavedModelBundle.load(resource.getFile().getAbsolutePath(), "serve");
        long t2 = System.currentTimeMillis();
        log.debug("loadCost = {}", t2 - t1);
        return savedModelBundle;
    }

    @Override
    public void run(String... args) throws Exception {
        //导入图
        SavedModelBundle savedModelBundle = load("yxwx/1564105115");
        for (int i = 0; i < 100; i++) {
            long t1 = System.currentTimeMillis();
            long[] inputIds = new long[]{101, 1872, 966, 4925, 4638, 4925, 4372, 3221, 1914, 2208, 102, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            long[] inputMask = new long[]{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            long[] segmentIds = new long[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
            long[] shape = new long[]{1, 128};
            Tensor y = savedModelBundle.session().runner()
                    .feed("input_ids", Tensor.create(shape, LongBuffer.wrap(inputIds)))
                    .feed("input_mask", Tensor.create(shape, LongBuffer.wrap(inputMask)))
                    .feed("segment_ids", Tensor.create(shape, LongBuffer.wrap(segmentIds)))
                    .feed("unique_ids", Tensor.create(0L))
                    .fetch("loss/Softmax").run().get(0);
            float[] result = new float[]{0, 0};
            FloatBuffer floatBuffer = FloatBuffer.wrap(result);
            y.writeTo(floatBuffer);
            long t2 = System.currentTimeMillis();
            log.debug("predictCost = {}, result[0] = {}, result[1] = {}", t2 - t1, result[0], result[1]);
        }
    }
}
