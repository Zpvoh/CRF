import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            ArrayList<Template> templates=readTemplates("template.utf8");
            Viterbi alg=new Viterbi(templates);
            alg.trainTemplates("train.utf8");

            alg.setSentence("塞雷");
            //alg.runAlgorithm();
            alg.realViterbi();
            //System.out.println(alg.getLabels());
            alg.printSegment();
        } catch (FileNotFoundException e) {
            System.out.println("File cannot find.");
        }
    }

    public static ArrayList<Template> readTemplates(String filename) throws FileNotFoundException {
        ArrayList<Template> templates=new ArrayList<>();

        FileInputStream fileInputStream= null;
        fileInputStream = new FileInputStream(filename);
        Scanner scanner=new Scanner(fileInputStream);
        while(scanner.hasNext()){
            String line=scanner.nextLine();
            if(line.length()>0 && (line.charAt(0)=='B' || line.charAt(0)=='U')){
                templates.add(new Template(line));
            }
        }

        return templates;
    }
}
