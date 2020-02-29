import java.util.ArrayList;
import java.util.List;

public class Application {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("You need to pass the result directory as the first parameter and the resolution as the second!");
        }

        List<Complex> constants = new ArrayList<>();
        constants.add(new Complex(0.285, 0.01));
        constants.add(new Complex(0.130,.745));
        constants.add(new Complex(0.4,0.2));
        constants.add(new Complex(-1,0));
        constants.add(new Complex(0.5,0.5));
        constants.add(new Complex(0.3,0.5));
        constants.add(new Complex(-0.72,0.11));
        constants.add(new Complex(0.158,0.013));

        constants.forEach(e -> compute(e, args[0], Integer.parseInt(args[1])));

        System.exit(0);
    }

    private static void compute(Complex e, String resultPath, int rez) {
        Fractals f = new Fractals(e.toString(), 5000, e);

        try {
            f.doYourStuff();
            f.writeResultToFile(resultPath + "/img" + System.currentTimeMillis() + ".jpg");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
