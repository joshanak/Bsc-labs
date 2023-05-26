package ui;

import java.lang.reflect.Array;
import java.util.*;

public class Tree {
    public ArrayList<Node> all_nodes = new ArrayList<>();
    public ArrayList<String> predictions = new ArrayList<>();
    public int maxDepth = -1;
    public double accuracy = 0.;
    public Node rootNode = null;
    public Data data = null;

    public Tree(Data data){
        this.data = data;
    }

    public void fit(){
        HashMap<String, String> filter = new HashMap<>();
        HashMap<String, TreeSet<String>> x = new HashMap<>(data.value_types_per_feature);
        x.remove(data.class_label_name);
        this.rootNode = ID3(filter, filter, x, this.data.class_label_name, this.maxDepth);
    }


    public void predict(ArrayList<HashMap<String, String>> testData){
        String prediction = "";
        int current_correct = 0;
        for(HashMap<String, String> test_element: testData){
            prediction = predictOneElement(test_element);
            predictions.add(prediction);
            if(prediction.equals(test_element.get(data.class_label_name)))
                current_correct++;
        }
        accuracy = (double) current_correct/testData.size();
        accuracy = Math.round(accuracy*100000.0)/100000.0;
    }
    public void printPredictions(){
        System.out.print("[PREDICTIONS]:");
        for(String prediction: this.predictions){
            System.out.print(" "+prediction);
        }
        System.out.println();
    }
    public void printConfusionMatrix(){
        int number;
        for(String outer_class_label_value: data.value_types_per_feature.get(data.class_label_name)) {
            String space = "";
            for (String inner_class_label_value : data.value_types_per_feature.get(data.class_label_name)) {
                number = 0;
                for(HashMap<String, String> test_element: data.testData){
                    String prediction = predictOneElement(test_element);
                    String correct_outcome = test_element.get(data.class_label_name);
                    if(correct_outcome.equals(outer_class_label_value) && prediction.equals(inner_class_label_value))
                        number++;
                }
                System.out.print(space + number);
                space = " ";
            }
            System.out.println();
        }
    }

    public String predictOneElement(HashMap<String, String> testElement){
        String prediction = " ";
        Node helperNode = rootNode;
        HashMap<String, String> helper = new HashMap<>();
        for(String feature_name: testElement.keySet()){
            if(!data.value_types_per_feature.get(feature_name).contains(testElement.get(feature_name))){
                return FreqTarget(helper, data.class_label_name, data.testData);
            }
            else{
                helper.put(feature_name, testElement.get(feature_name));
            }
        }
        while(!helperNode.feature_name.equals(data.class_label_name)){

           for(String value: helperNode.children.keySet()){
               HashMap<String, String> aux = new HashMap<>();
               if(helperNode!=null) {
                   aux.put(helperNode.feature_name, value);
                   if (containsAll(testElement, aux)) {
                       if(helperNode.children!=null)
                         helperNode = helperNode.children.get(value);
                   }
               }
           }
        }
        prediction = helperNode.feature_value;
        return prediction;
    }


    //NAPRAVITI FILTER A NE MIJENJATI CIJELU BAZU PODATAKA
    public Node ID3(HashMap<String, String> path, HashMap<String, String> parent_path,HashMap<String, TreeSet<String>> value_types_per_feature, String class_label, int maxDepth){
        ArrayList<HashMap<String, String>> d = D(path);
        ArrayList<HashMap<String, String>> d_parent = D(parent_path);
        String v = null;
        if(d.isEmpty()){
            v= FreqTarget(parent_path, class_label, data.trainingData);
            return makeLeafNode(v, class_label, path);
        }
        if(maxDepth==0){
            v= FreqTarget(path, class_label, data.trainingData);
            return makeLeafNode(v, class_label, path);
        }
        v= FreqTarget(path, class_label, data.trainingData);
        /*oznaka da ili ne*/
        if((calculateEntropy(path)==0) || value_types_per_feature.isEmpty()){
            return makeLeafNode(v, class_label, path);
        }
        String nextFeature = DiscrimFeature(path, value_types_per_feature);
        HashMap<String, TreeSet<String>> new_value_types_per_feature = new HashMap<>(value_types_per_feature);
        new_value_types_per_feature.remove(nextFeature);
        Node node = new Node(nextFeature);
        for(String value: value_types_per_feature.get(nextFeature)){
                HashMap<String, String> newPath = new HashMap<>(path);
                newPath.put(nextFeature, value);
                Node childNode = ID3(newPath, path, new_value_types_per_feature, class_label, maxDepth-1);
                node.addChildNode(value, childNode);
        }
        return node;
    }

    public String FreqTarget(HashMap<String, String> path, String class_label, ArrayList<HashMap<String, String>> d){
        HashMap<String, Integer> number_of_target = D_x_equals_v(path, class_label, d);
        String target_name = null;
        int max = 0;
        for(String key: number_of_target.keySet()){
            if(max<number_of_target.get(key)) {
                max = number_of_target.get(key);
                target_name = key;
            }
            if(target_name.compareTo(key)>0 && max==number_of_target.get(key)){
                max = number_of_target.get(key);
                target_name = key;
            }
        }
        return target_name;
    }
    public String DiscrimFeature(HashMap<String, String> path, HashMap<String, TreeSet<String>> value_types_per_feature){
        double max = 0., ig;
        String bestNextFeature = "Z";
            for(String feature_name: value_types_per_feature.keySet()){
                ig=calculateIG(path, feature_name);
                ig = Math.round(ig*10000.0)/10000.0;
                System.out.print("IG("+feature_name+")"+"= "+ig+"   ");
                if(ig>max) {
                    max = ig;
                    bestNextFeature = feature_name;
                }
                if((bestNextFeature.compareTo(feature_name)>0) && ig==max)
                    bestNextFeature = feature_name;
            }
            System.out.println("     "+bestNextFeature);
            return bestNextFeature;
    }


    //usporeduje samo elemente koje imaju sve iste prethodne znacajke
    public boolean containsAll(HashMap<String, String> larger_map, HashMap<String, String> smaller_map){
        for (String feature_name: smaller_map.keySet()) {
            if(larger_map.containsKey(feature_name)) {
                if (!larger_map.get(feature_name).equals(smaller_map.get(feature_name)))
                    return false;
            }
            else{
                return false;
            }
        }
        return true;
    }

    /*
    Sprema sve elemente koje zadovoljavaju neki podskup feature value-a od svih elemenata u data setu
     */
    public ArrayList<HashMap<String, String>> D(HashMap<String, String> feature_name_values){
        ArrayList<HashMap<String, String>> D = new ArrayList<>();
        for(HashMap<String, String> map: data.trainingData){
            if(containsAll(map, feature_name_values))
                D.add(map);
        }
        return D;
    }

    /*
    filtrira neki skup po znacajci i vraca mapu gdje su kljucevi vrijednosti te znacajke
    a vrijednost mape je broj pronalaska tog elementa
     */
    public HashMap<String, Integer>  D_x_equals_v(HashMap<String, String> feature_name_values, String feature_name, ArrayList<HashMap<String, String>> d) {
        HashMap<String, Integer> value_occurrence = new HashMap<>();
        for (HashMap<String, String> map : d) {
            if (containsAll(map, feature_name_values)) {
                if (!value_occurrence.containsKey(map.get(feature_name))) {
                    value_occurrence.put(map.get(feature_name), 1);
                } else {
                    int count = value_occurrence.get(map.get(feature_name));
                    value_occurrence.replace(map.get(feature_name), count + 1);
                }
            }

        }
        return value_occurrence;
    }
    /*
    Iteriram po data setu i nalazim sve elemente data seta koje sadrzi dosadasnja znacajke
    Kada nadem dodam u mapu map_class_label_quantity koja sadrzi
    kao key vrijednosti ciljnih znacajki a value broj ponavljanja
    kada popunim tu mapu racunam entropu po formuli
    iteriram kroz map_class_label_quantity
    racunam P kao value te odredene vrijednosti kroz zbroj svih valuesa map_class_label_quantity
    taj P mnozim sa logaritmom po bazi dva od P i to sve zbrajam za entropiju
    */
    public double calculateEntropy(HashMap<String, String> feature_name_values){
        double entropy = 0.;
        HashMap<String, Integer> map_class_label_quantity = new HashMap<>();
        HashMap<String, String> curr_feature_name_values = new HashMap<>(feature_name_values);
        int total_count = 0;
        map_class_label_quantity = D_x_equals_v(curr_feature_name_values, data.class_label_name, data.trainingData);
        for(Integer count: map_class_label_quantity.values())
            total_count += count;
        for(Integer i: map_class_label_quantity.values()){
            double P = (double) i/total_count;
            entropy +=  P*log2(P);
        }
        entropy = -Math.round(entropy*1000.0)/1000.0;
        return entropy;
    }
    public double calculateIG(HashMap<String, String> feature_name_values, String feature_name) {
        double IG = calculateEntropy(feature_name_values), total_count = 0.;
        HashMap<String, Integer> map_feature_label_quantity = new HashMap<>();
        HashMap<String, Double> map_feature_value_entropy = new HashMap<>();
        Node aux = null;
        //za svaku vrijednost znacajke je povezana broj ponavljanja u data setu
        map_feature_label_quantity = D_x_equals_v(feature_name_values, feature_name, data.trainingData);
        for(Integer count: map_feature_label_quantity.values()) {
            total_count += count;
        }
        //za svaku vrijednost znacajke je povezana entropija
        for(String key: map_feature_label_quantity.keySet()){
            HashMap<String, String> help = new HashMap<String, String>(feature_name_values);
            help.put(feature_name, key);
            map_feature_value_entropy.put(key, calculateEntropy(help));
        }
        //iteriram po vrijednostima znacajke te su im broj ponavljanja i entropija povezane za isti key
        for(String key: map_feature_value_entropy.keySet()){
            double weight = (double) map_feature_label_quantity.get(key)/total_count;
            double curr_entropy = map_feature_value_entropy.get(key);
            IG -= weight * curr_entropy;
        }
        return IG;
    }
    public double log2(double n) {
        return Math.log(n) / Math.log(2);
    }
    public void printLeaves(){
        for(Node n: all_nodes){
            if(n.isLeaf)
                System.out.println(n);
        }
    }
    public Node makeLeafNode(String v, String class_label, HashMap<String, String> path){
        Node leafNode = new Node(class_label);
        leafNode.feature_value = v;
        leafNode.node_feature_name_values.putAll(path);
        leafNode.node_feature_name_values.put(class_label, v);
        leafNode.setLeaf();
        all_nodes.add(leafNode);
        return leafNode;
    }
    public void printBranches(int level,  Node node, String pastFeature){
        if (node.feature_name.equals(data.class_label_name)) { // LeafNode
            System.out.println(pastFeature + node.feature_value);
            return;
        }
        for (Map.Entry<String, Node> map : node.children.entrySet()) {
            String newAbove = pastFeature + level + ":" + node.feature_name + "=" + map.getKey() + " ";
            printBranches(level+1, map.getValue(), newAbove);
        }
    }
    public void printBranchesNonRecursive(Node node){
        Stack<Node> node_stack = new Stack<>();
        Stack<Integer> level_stack = new Stack<>();
        Stack<String> output = new Stack<>();
        int level = 1;
        String branch = "";
        node_stack.push(node);
        level_stack.push(level);
        output.push(branch);
        while(!node_stack.isEmpty()){
            node = node_stack.pop();
            level = level_stack.pop();
            branch = output.pop();
            if(node.feature_name.equals(data.class_label_name)){
                System.out.println(branch + node.feature_value);
                System.out.println(node_stack);
                node = node_stack.pop();
            }
            if(node.children==null){
                System.out.println(output);
                System.out.println(node_stack);
            }
            if(level>10) return;
            for (Map.Entry<String, Node> map : node.children.entrySet()){
                node_stack.push(map.getValue());
                level_stack.push(level+1);
                String new_branch = branch + level + ":" + node.feature_name + "=" + map.getKey() + " ";
                output.push(new_branch);
            }
        }

    }
    }

