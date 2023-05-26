package ui;
import java.lang.Math;
import java.io.*;
import java.util.*;


public class Solution {
	public static void main(String ... args) throws IOException {
		Data D = new Data();
		D.trainingFile = args[0];
		D.testFile = args[1];
		D.trainingData = new ArrayList<>(D.readData(D.trainingFile));
		D.testData = new ArrayList<>(D.readData(D.testFile));
        /*inicijalizacija stabla*/
		Tree tree = new Tree(D);
		if (args.length > 2) tree.maxDepth = Integer.parseInt(args[2]);

		tree.fit();

		System.out.println("[BRANCHES]:");
		//tree.printBranches(1, tree.rootNode, "");
		tree.printBranchesNonRecursive(tree.rootNode);
		if(tree.rootNode==null) System.out.println("HA");
        /*predikcija test data */
		tree.predict(D.testData);
		tree.printPredictions();
		System.out.print("[ACCURACY]: ");
        String acc = String.format("%.5f", tree.accuracy).replace(",",".");
        System.out.println(acc);
		System.out.println("[CONFUSION_MATRIX]:");
		tree.printConfusionMatrix();
	}
}


