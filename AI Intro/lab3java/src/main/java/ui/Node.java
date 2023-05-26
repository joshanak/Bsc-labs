package ui;

import java.util.*;

public class Node{
    /* razina u stablu */
    public int level = 0;
    /* children je u principu subtree -> key je vrijednost znacajke koja vodi */
    public HashMap<String, Node> children = new HashMap<>();
    /* node_feature_name_values mogu smatrati dosadasnjom putanjom */
    public HashMap<String, String> node_feature_name_values = new HashMap<>();
    /*ako je isLeaf true onda children=null  */
    public boolean isLeaf = false;
    /* feature_name i feature_value su prave oznake cvora (znacajka i vrijednost znacajke
       feature value ce najcesce biti null osim kod leafova a tamo je feature_name class_target label*/
    public String feature_name = null,  feature_value = null;
    /* ovo ce biti samo kod leafa */
    public Node(String feature_name, String feature_value){
        this.node_feature_name_values.put(feature_name, feature_value);
        this.feature_name = feature_name;
    }
    /* ovo je kod svih drugih nodeova */
    public Node(String feature_name){
        this.feature_name = feature_name;
    }
    /* Ovo je bilo samo da mogu stvoriti putanju nekakvu  */
    public Node(HashMap<String, String> feature_name_values){
        this.node_feature_name_values = feature_name_values;
    }

    public void setLeaf(){
        this.isLeaf=true;
        children = null;

    }

    public void addChildNode(String feature_value, Node node){
        this.children.put(feature_value, node);
        node.node_feature_name_values.putAll(this.node_feature_name_values);
        node.node_feature_name_values.put(this.feature_name, this.feature_value);
        node.level=this.level+1;
    }

    @Override
    public String toString() {
        return "{" +
                "" + this.feature_name +
                "}";
    }
}