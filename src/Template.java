import java.util.HashMap;

public class Template {
    private int type;
    private int length;
    private HashMap<String, Integer> scores=new HashMap<>();
    private int[] bias;

    public Template(String template){
        type=template.charAt(0)=='U'?Constants.unigram:Constants.bigram;

        String s=template.substring(4);
        String[] temps=s.split("/");
        length=temps[0].equals("")?0:temps.length;
        bias=new int[length];

        for(int i=0; i<length; i++){
            String x=temps[i].substring(3, temps[i].length()-1);
            bias[i]=Integer.parseInt(x.split(",")[0]);
        }
    }

    public void addFeatureNum(String feature){
        if(feature.length()!=length+type)
            return;

        if(!scores.containsKey(feature))
            scores.put(feature, 0);

        Integer newValue=scores.get(feature)+1;
        scores.replace(feature, newValue);
    }

    public void decreaseFeatureNum(String feature){
        if(feature.length()!=length+type)
            return;

        if(!scores.containsKey(feature))
            return;

        Integer newValue=scores.get(feature)-1;
        scores.replace(feature, newValue);
    }

    public int match(String sentence, String lastLabel, String thisLabel, int i){
        String feature=generateFeature(sentence, lastLabel, thisLabel, i);

        return scores.getOrDefault(feature, 0);
    }

    public String generateFeature(String sentence, String lastLabel, String thisLabel, int i){
        char[] fs=new char[bias.length];
        for(int t=0; t<bias.length; t++){
            if(i+bias[t]>=0 && i+bias[t]<sentence.length())
                fs[t]=sentence.charAt(i+bias[t]);
            else
                fs[t]=Constants.N.charAt(0);
        }

        String feature=new String(fs);
        feature+=(type==Constants.unigram)?thisLabel:(lastLabel+thisLabel);

        return feature;
    }

}
