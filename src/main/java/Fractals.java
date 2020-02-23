import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class Fractals {

    private Executor executor;

    private final static int UPPER_BOUND = 2;

    private final static int LOWER_BOUND = -2;

    private int rez;

    private double step;

    private double[][] result;

    private Complex constant;

    private String name;

    private final static int THREAD_COUNT = 8;

    public Fractals(String name, int rez, Complex constant) {
        this.rez = rez;
        this.constant = constant;
        this.name = name;
        this.step = (UPPER_BOUND - LOWER_BOUND + 0.) / rez;

        result = new double[rez][rez];

        this.executor = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    public void doYourStuff() throws ExecutionException, InterruptedException {
        log("Starting!");
        long start = System.currentTimeMillis();
        int step = rez / THREAD_COUNT;

        List<FutureTask<Integer>> tasks = new ArrayList<>();

        for (int i = 0; i < THREAD_COUNT; i++) {
            final int crt = i;
            tasks.add(new FutureTask<>(() -> {
                compute(step * crt, step);
                return 0;
            }));
        }


        tasks.forEach(executor::execute);

        for (FutureTask<Integer> task : tasks) {
            task.get();
        }
        long stop = System.currentTimeMillis();


        log("Done in " + (stop - start) + "ms!");
    }


    private void compute(int start, int iterations) {
        for (int i = start; i < start + iterations; i++) {
            for (int j = 0; j < rez; j++) {
                result[i][j] = discrimation(rez, new Complex(LOWER_BOUND + (i * step), LOWER_BOUND + (j * step)));
            }

        }
    }

    public void writeResultToFile(String path) throws Exception {
        BufferedImage image = new BufferedImage(result.length, result[0].length, BufferedImage.TYPE_INT_RGB);
        double[] doubles = Arrays.stream(result).flatMapToDouble(Arrays::stream).toArray();
        double min = Arrays.stream(doubles).min().orElseThrow(() -> new Exception("This shouldn't happen!"));
        double max = Arrays.stream(doubles).max().orElseThrow(() -> new Exception("This shouldn't happen!"));
        double maxRGB = Math.pow(64, 3);
        double minInterval = Math.abs(min);
        double maxInterval = max + minInterval;

        for (int x = 0; x < rez; x++) {
            for (int y = 0; y < rez; y++) {
                double currentValue = result[x][y];

                currentValue += minInterval; // To make it >0
                currentValue /= maxInterval;
                int z = (int) (maxRGB * currentValue);
                image.setRGB(x, y, z);
            }
        }

        File ImageFile = new File(path);
        ImageIO.write(image, "jpg", ImageFile);

    }

    private double discrimation(int rez, Complex complex) {
        for (int i = 0; i < rez; i++) {
            if (module(complex) < 2) {
                complex = complex.times(complex).plus(constant);
            }
        }
        return module(complex);
    }

    private double module(Complex complex) {
        return Math.pow(Math.pow(complex.getReal(), 2) + Math.pow(complex.getImaginary(), 2), 0.5);
    }

    private void log(String log) {
        System.out.println(name + ": " + log);
    }

}
