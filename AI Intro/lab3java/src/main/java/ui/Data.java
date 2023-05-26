package ui;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;


public class Data {
    public ArrayList<HashMap<String, String>> trainingData, testData = null;
    public HashMap<String, TreeSet<String>> value_types_per_feature = new HashMap<>();
    public String class_label_name = "";
    public ArrayList<String> feature_names = new ArrayList<>();
    public String trainingFile = null, testFile = null;

    public ArrayList<HashMap<String,String>> readData(String file) throws IOException {
        BufferedReader br
                = new BufferedReader(new FileReader(file));
        ArrayList<HashMap<String,String>> data = new ArrayList<HashMap<String,String>>();
        String line;
        ArrayList<String> feature_values;
        //value_types_per_feature = new HashMap<>();
        for(int i=0; (line=br.readLine())!=null; i++){
            if(i == 0){
                if(file.equals(trainingFile)) {
                    feature_names = new ArrayList<>(Arrays.asList(line.split(",")));
                    for (String name : feature_names) {
                        value_types_per_feature.put(name, new TreeSet<>());
                    }
                }
                continue;
            }
            feature_values = new ArrayList<>(Arrays.asList(line.split(",")));
            HashMap<String, String> feature_name_values = new HashMap<>();
			/*
			u ovoj for petlji za svaki element u data setu spajam ime znacajke sa vrijednoscu
			i radim mapu u kojoj povezujem ime znacajke sa svim mogucim vrijednostima
			 */
                for (int j = 0; j < feature_values.size(); j++) {
                    if(file.equals(trainingFile)) {
                        TreeSet<String> aux = new TreeSet<>(value_types_per_feature.get(feature_names.get(j)));
                        aux.add(feature_values.get(j));
                        value_types_per_feature.put(feature_names.get(j), aux);
                    }
                    feature_name_values.put(feature_names.get(j), feature_values.get(j));
                }

            data.add(feature_name_values);
        }
        if(file.equals(trainingFile))
            class_label_name = feature_names.get(feature_names.size()-1);
        return data;
    }
}
