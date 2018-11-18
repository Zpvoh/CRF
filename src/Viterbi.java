import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Viterbi {
    private ArrayList<Template> templates=new ArrayList<>();

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    private String sentence;

    public String getLabels() {
        return labels;
    }

    private String labels;
    private String actualLabels;

    public Viterbi(String sentence){
        this.sentence=sentence;
    }

    public Viterbi(String sentence, ArrayList<Template> templates){
        this.sentence=sentence;
        this.templates=templates;
    }

    public Viterbi(ArrayList<Template> templates){
        this.sentence=new String();
        this.templates=templates;
    }

    public void trainTemplates(String filename) throws FileNotFoundException {
        int sum=0;

        while(sum<5){
            System.out.println(sum);

            FileInputStream fileInputStream=new FileInputStream(filename);
            Scanner scanner=new Scanner(fileInputStream);
            int sentenceIndex=0;
            while (scanner.hasNext() && sentenceIndex<20000){
                readNextSentence(scanner);
                //System.out.println("sentence is "+sentence);
                //System.out.println("real labels are "+actualLabels);
                runAlgorithm();
                //System.out.println("labels are "+labels);

                for(int i=0; i<labels.length(); i++){
                    if(labels.charAt(i)!=actualLabels.charAt(i)){
                        String lastLabel=i>0?actualLabels.substring(i-1, i):Constants.N;
                        String realThisLabel=actualLabels.substring(i, i+1);
                        String fakeThisLabel=labels.substring(i, i+1);
                        for(int j=0; j<templates.size(); j++){
                            Template template=templates.get(j);
                            String realFeature=template.generateFeature(sentence, lastLabel, realThisLabel, i);
                            String fakeFeature=template.generateFeature(sentence, lastLabel, fakeThisLabel, i);
                            template.addFeatureNum(realFeature);
                            //System.out.println(realFeature+"++");

                            template.decreaseFeatureNum(fakeFeature);
                            //System.out.println(fakeFeature+"--");
                        }
                    }
                }

                sentenceIndex++;

            }

            double total=0;
            double errors=0;
            while (scanner.hasNext()){
                readNextSentence(scanner);
                //System.out.println("sentence is "+sentence);
                //System.out.println("real labels are "+actualLabels);
                runAlgorithm();
                //System.out.println("labels are "+labels);

                for(int i=0; i<labels.length(); i++){
                    total++;
                    if(labels.charAt(i)!=actualLabels.charAt(i)){
                        errors++;
                    }
                }

            }
            System.out.println("correct rate: "+(1-errors/total));

            sum++;
        }
    }

    public void runAlgorithm(){
        labels="";

        for(int i=0; i<sentence.length(); i++){
            String lastLabel=(labels.length()>=i && i>0)?labels.substring(i-1,i):Constants.N;
            int[] nums=new int[4];
            for(int j=0; j<templates.size(); j++){
                Template template=templates.get(j);
                nums[0]+=template.match(sentence, lastLabel, Constants.B, i);
                nums[1]+=template.match(sentence, lastLabel, Constants.E, i);
                nums[2]+=template.match(sentence, lastLabel, Constants.I, i);
                nums[3]+=template.match(sentence, lastLabel, Constants.S, i);
            }

            //String thisLabel="";
            int labelIndex=max(nums);
            switch (labelIndex){
                case 0:
                    labels=labels.concat(Constants.B);
                    break;
                case 1:
                    labels=labels.concat(Constants.E);
                    break;
                case 2:
                    labels=labels.concat(Constants.I);
                    break;
                case 3:
                    labels=labels.concat(Constants.S);
                    break;
            }
        }
    }

    public void printSegment(){
        StringBuffer buffer=new StringBuffer(sentence);
        boolean inB=true;
        int bias=1;
        for(int i=0; i<sentence.length(); i++){
            if(labels.substring(i,i+1).equals(Constants.B)){
                inB=true;
            }else if(inB && labels.substring(i,i+1).equals(Constants.E)){
                buffer.insert(i+bias,'/');
                bias++;
                inB=false;
            }else if(labels.substring(i,i+1).equals(Constants.S)){
                buffer.insert(i+bias,'/');
                bias++;
            }
        }

        System.out.println(buffer.toString());
    }

    private void readNextSentence(Scanner scanner){
        StringBuffer sentenceBuf=new StringBuffer();
        StringBuffer labelBuf=new StringBuffer();
        String line;
        while(scanner.hasNext() && !(line=scanner.nextLine()).equals("")){
            String[] split=line.split(" ");
            sentenceBuf.append(split[0]);
            labelBuf.append(split[1]);
        }

        sentence=sentenceBuf.toString();
        actualLabels=labelBuf.toString();
    }

    private int max(int[] nums){
        int max=nums[0];
        int maxIndex=0;

        for(int i=1; i<nums.length; i++){
            boolean maxBigger=max>nums[i];
            max=maxBigger?max:nums[i];
            maxIndex=maxBigger?maxIndex:i;
        }

        return maxIndex;
    }

}
