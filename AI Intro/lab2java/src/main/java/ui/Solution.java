package ui;


import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;


public class Solution {
	public static String clauses_file, input_file, goal;
	public static List<Premise> premises;
	public static List<Premise> originalPremises = new ArrayList<>();
	public static List<String> negated_goals;
	public static boolean nil=false;
	public static String cookingGoal = null;

	public class Premise{
		public String wholePremise = null;
		public ArrayList<String> literals = null;
		public Premise[] parents = new Premise[2];
		public boolean isOriginalPremise = false;
		public Premise(String wholePremise){
			this.wholePremise = wholePremise;
			literals =  new ArrayList<>(Arrays.asList(wholePremise.toLowerCase().split(" ")));
			while(literals.remove("v"));
		}
		public Premise(String wholePremise, Premise p1, Premise p2){
			this.wholePremise = wholePremise.toLowerCase();
			this.parents = new Premise[]{p1, p2};
			literals =  new ArrayList<>(Arrays.asList(wholePremise.toLowerCase().split(" ")));
			while(literals.remove("v"));
		}

		public Premise(ArrayList<String> literals){
			this.literals = literals;
			this.wholePremise = this.toString();
		}

		@Override
		public String toString() {
			if(wholePremise!=null) return wholePremise;
			String returnString = "";
			for(int i=0; i<this.literals.size(); i++){
				if(i!=0)
					returnString =  returnString +" v "+this.literals.get(i);
				else
					returnString = this.literals.get(i)+returnString;
			}
			this.wholePremise=returnString;
			return returnString;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Premise premise = (Premise) o;
			return literals.equals(premise.literals);
		}

		@Override
		public int hashCode() {
			return Objects.hash(literals);
		}
	}
	public void readClausesFile() throws IOException {
		BufferedReader br
				= new BufferedReader(new FileReader(clauses_file));
		String line;
		Premise helper_premise;
		for(int i=0; (line=br.readLine())!=null; i++){
			if(line.trim().startsWith("#")) continue;
			goal = line;
			helper_premise = new Premise(line.toLowerCase());
			helper_premise.isOriginalPremise = true;
			premises.add(helper_premise);
			originalPremises.add(helper_premise);
		}
		premises.remove(premises.size()-1);
		getNegatedGoal();
	}

	public void getNegatedGoal(){
		Premise helper_premise;
		negated_goals = new ArrayList<>();
		ArrayList<String> aux;
		if(cookingGoal!=null) {
			premises.add(new Premise(goal.toLowerCase()));
			goal = cookingGoal;
		}
		aux =  new ArrayList<>(Arrays.asList(goal.toLowerCase().split(" ")));
		while(aux.remove("v"));
		for(String lit: aux){
			if(lit.startsWith("~"))
				lit = lit.substring(1);
			else
				lit = "~" + lit;
			negated_goals.add(lit.trim());
			helper_premise = new Premise(lit);
			premises.add(helper_premise);
		}

	}
	public void printPath(Premise p){
		Queue<Premise> q = new LinkedList<>();
		ArrayList<Premise> path = new ArrayList<>();
		q.add(p);
		while(!q.isEmpty()){
			p=q.remove();
			path.add(p);
			if(p!=null && p.parents[0]!=null && p.parents[1]!=null){
				q.add(p.parents[0]);
				q.add(p.parents[1]);
			}
		}
		ArrayList<Premise> redList = new ArrayList<>();
		for(Premise pre: path){
			if(!redList.contains(pre))
				redList.add(pre);
		}
		path=redList;
		for(Premise pre: originalPremises){
			if(cookingGoal!=null) continue;
			 System.out.println(pre);
		}
		System.out.println("=================");
		for(int i=path.size()-1; i>=0; i--){
			if(!path.get(i).isOriginalPremise && path.get(i).parents[0]!=null){
				System.out.println(path.get(i)+" parents: "+path.get(i).parents[0]+ ", "+path.get(i).parents[1]);
			}
		}
		System.out.println("=================");
	}
	public  String getNegatedLiteral(String literal){
		String negatedliteral=null;
		if(literal.contains("~"))
			negatedliteral = literal.replace("~", "");
		else negatedliteral = "~"+literal;
		return negatedliteral;
	}
	/*
	ako sadrzi tautologiju izbriši
	iteriraj po svim premisama, ako je jedna premisa(A) podskup neke druge(B) izbaci B
	 */
	public ArrayList<Premise> removeRedundantClauses(ArrayList<Premise> entryList){
		ArrayList<Premise> returnList= new ArrayList<Premise>();
		//makni dvostruke elemente;
		for (Premise element : entryList) {
			if (!returnList.contains(element)) {
				returnList.add(element);
			}
		}
		for(Premise outerpr: entryList){
			boolean tautology=false;
			//projvera tautologije
			for(int i=0; i<outerpr.literals.size(); i++){
				String negatedliteral=getNegatedLiteral(outerpr.literals.get(i));
				if(outerpr.literals.contains(negatedliteral)){
					returnList.remove(outerpr);
					tautology=true;
					break;
				}
			}
			if(tautology) continue;
			//provjera podskupa
			for(Premise innerpr: entryList) {
				if(outerpr.equals(innerpr)) continue;
				int check = 0;
				if(innerpr.literals.size()<outerpr.literals.size()) continue;
				for (int i = 0; i < outerpr.literals.size(); i++) {
					if(innerpr.literals.contains(outerpr.literals.get(i)))
						check++;
				}
				if(check == outerpr.literals.size()) returnList.remove(innerpr);
			}
		}
		return returnList;
	}
	/*
	vrati jednu premisu tehnikom resolve, ako se vraca vise od jedne znaci da je tautologija->vrati null
	iteriraj po literalima prve premise i trazi suprotni literal u drugoj premisi
	kada nades spoji literale ostatak prve i druge premise u listu -> ako je lista prazna NIL
	ako se vise od jednom nasli suprotni znakovi znaci tautologija -> vrati null
	 */
	public Premise plResolve(Premise p1, Premise p2){
		if(p1.isOriginalPremise && p2.isOriginalPremise) return null;
		int checkForComplement = 0;
		Premise returnPremise = null;
		for(String literal: p1.literals){
			//ako sadrži suprotan literal
			if(checkForComplement>1) return null;
			String negatedLiteral = getNegatedLiteral(literal);
			if(p2.literals.contains(negatedLiteral)){

				checkForComplement++;
				//spoji sve literale osim suprotnih
				List<String> firstlist = p1.literals.stream().filter(q->!q.contentEquals(literal)).collect(Collectors.toList());
				List<String> secondlist = p2.literals.stream().filter(q -> !q.contentEquals(negatedLiteral)).collect(Collectors.toList());
				firstlist.addAll(secondlist);
				if(firstlist.isEmpty()){
					Premise n = new Premise("NIL", p1, p2);
					printPath(n);
					nil = true;
					return null;
				}
				returnPremise = new Premise((ArrayList<String>) firstlist);
				if(p1!=null && p2!=null ) {
					returnPremise.parents[0] = p1;
					returnPremise.parents[1] = p2;
				}
			}
		}
		if(checkForComplement==1) return returnPremise;
		else {
			return null;
		}

	}

	public void plResolution(){
		ArrayList<Premise> newClauses = new ArrayList<>();
		while(true){
			for (Premise p1 : premises) {
				for (Premise p2 : premises) {
					if(p1.equals(p2)) continue;
					Premise p3 = plResolve(p1, p2);
					if(p3!=null) newClauses.add(p3);
					if (nil) return;
				}
			}
			newClauses = removeRedundantClauses(newClauses);//zadnje mijenjano
			if(checkIfContainsAll((ArrayList<Premise>) premises, newClauses)) return;
			premises.addAll(newClauses);
			premises = removeRedundantClauses((ArrayList<Premise>) premises);

		}
	}
	public boolean checkIfContainsAll(ArrayList<Premise> largerList, ArrayList<Premise> smallerList){
		ArrayList<Premise> helperList = new ArrayList<>();
		helperList.addAll(largerList);
		helperList.addAll(smallerList);
		helperList = removeRedundantClauses(helperList);
		if(largerList.containsAll(helperList)) return true;
		else return false;
	}
	public void startCooking() throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(input_file));
		String line;
		Premise helper_premise;
		ArrayList<Premise> newlyAddedPremises = new ArrayList<>();
		ArrayList<Premise> newlyRemovedPremises = new ArrayList<>();
		for(int i=0; (line=br.readLine())!=null; i++){
			line = line.trim();
			String operator = line.substring(line.length() - 1);
			String clause = line.substring(0, line.length() - 1).trim();
			System.out.println("User's command: " + line);
			switch (operator){
				case ("?"):
					cookingGoal = clause;
					premises = new ArrayList<>();
					readClausesFile();
					for(Premise pre: newlyAddedPremises){
						if(!premises.contains(pre)) premises.add(pre);
					}
					for(Premise pre: newlyRemovedPremises){
						premises.remove(pre);
					}
					plResolution();
					if (nil) System.out.println("[CONCLUSION]: " + cookingGoal.toLowerCase() + " is true");
					else System.out.println("[CONCLUSION]: " + cookingGoal.toLowerCase() + " is unknown");
					System.out.println();
					nil = false;
					break;
				case ("+"):
					helper_premise = new Premise(clause.toLowerCase());
					helper_premise.isOriginalPremise = true;
					if(!premises.contains(helper_premise)) {
						newlyAddedPremises.add(helper_premise);
						if(newlyRemovedPremises.contains(helper_premise))
							newlyRemovedPremises.remove(helper_premise);
						System.out.println(clause+" added");
					}
					else System.out.println(clause+ " is present");
					System.out.println();
					break;
				case ("-"):
					helper_premise = new Premise(clause.toLowerCase());
					if(premises.contains(helper_premise)){
						System.out.println(clause+" removed");
						newlyRemovedPremises.add(helper_premise);
					}
					else System.out.println(clause+" isn't contained");
					System.out.println();
					break;
			}
		}
	}
	public static void main(String ... args) throws IOException {
		Solution s = new Solution();
		boolean resolution = false;
		boolean cooking = false;
		for(int i=0; i< args.length; i++) {
			if(args[i].contentEquals("resolution")) {
				clauses_file = args[i + 1];
				resolution = true;
			}
			if(args[i].contentEquals("cooking")){
				clauses_file = args[i+1];
				input_file = args[i+2];
				cooking = true;
			}
		}

		if(resolution) {
			premises = new ArrayList<>();
			s.readClausesFile();
			s.plResolution();
			if (nil) System.out.println("[CONCLUSION]: " + goal.toLowerCase() + " is true");
			else System.out.println("[CONCLUSION]: " + goal.toLowerCase() + " is unknown");
		}
		if(cooking){

			 s.startCooking();
		}
	}

}
