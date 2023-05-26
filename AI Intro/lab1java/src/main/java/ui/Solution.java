package ui;
import java.io.*;
import java.util.*;

public class Solution {
	public static String alg, opisnik_stanja, opisnik_h, start;
	public static Map<String,String> prijelazi = new HashMap<>();
	public static Map<String, Integer> h = new HashMap<String, Integer>();
	public  static List<String>  goals = new ArrayList<>();
	public static TreeSet<String> stanja = new TreeSet<String>();
	public static boolean found = false;
	public static boolean  consistent = true;
	public static boolean optimistic = true;
	public static double cost = 0.;
	public static Node lastNode = null;
	public static List<Node> path = null;
	public static HashSet<String> visited = null;
	// UPUTE - cvor, sadrzi stanje, roditelj i cijenu
	public class Node{
		String state=null;
		Node parent = null;
		double price = 0.;
		int h = 0;
		double f=price+h;

		public Node(String state, Node parent){
			this.state = state;
			this.parent = parent;
		}
		public Node(String state, double price){
			this.state = state;
			this.price = price;
		}
		public Node(String state, Node parent, double price){
			this.state = state;
			this.parent = parent;
			this.price = price;
		}
		public Node(String state, double price, int h){
			this.state = state;
			this.price = price;
			this.h = h;
		}
		public int getH() {
			return h;
		}
		public String getState(){
			return this.state;
		}
		public double getPrice(){
			return this.price;
		}

		public double getF() {
			return f;
		}

		public void setF(){
			f=this.h+this.price;
		}
		public void setParent(Node p){ this.parent = p; }
		public void optimalParent(Node newParent){
			if(alg.contentEquals("ucs")) {
				Node currentParent = this.parent;
				if (currentParent != null) {
					if (newParent.price < currentParent.price) {
						if (newParent != null) {
							this.parent = newParent;
							this.price = this.price + this.parent.getPrice();
						}
					}
				}
			}
			if(alg.contentEquals("astar")) {
				Node currentParent = this.parent;
				if (currentParent != null) {
					if (newParent.f < currentParent.f) {
						if (newParent != null) {
							this.parent = newParent;
							this.price = this.price + this.parent.getPrice();
							this.setF();
						}
					}
				}
			}
		}

		@Override
		public String toString() {

			return //"Node{" +  "state='" +
					this.state;
			//+ '\'' + ", price=" + price + '}';

		}

	}
	/*UPUTE
	Iz mape prijelaza dobivam sva susjedna stanja i njihovu cijenu na temelju kljuca koji je stanje argumenta node.
	Stvaram node na temelju procitane cijene i stanja te dodajem u nesortiranu listu
	Prema algoritmu koji dobivam iz argumenta sortiram ih na odreden nacin i dodajem u listu
	Tu listu vracam
	 */
	public List<Node> expand(Node node){
		List<Node> expanded = new ArrayList<>();
		Node helper_node;
		double cost = 0.;
		String[] x;
		String[] aux = prijelazi.get(node.state).split(" ");
		for(String s: aux) {
			x = s.split(",");
			s = x[0].trim();
			cost = Double.parseDouble(x[1].trim());
			if(alg.contentEquals("astar")) {
				int hval = h.get(node.state);
				helper_node = new Node(s, node.price+cost, hval);
				helper_node.f = node.f + hval + cost;

				//helper_node = new Node(s, cost+node.price, hval);
				//helper_node.setF();
			}
			else helper_node = new Node(s, cost+ node.price);
			expanded.add(helper_node);
		}
		Comparator<Node> stateComparator = Comparator.comparing(Node::getState);
		Collections.sort(expanded, stateComparator);
		return expanded;

	}

	public boolean checkGoal(Set<String> visited, Node n){
		if(goals.contains(n.state)){
			found = true;
			lastNode = n;
			path = recreatePath(n);
			cost=path.get(path.size()-1).price;
			/*List<Node> path = recreatePath(n);
			cost=path.get(path.size()-1).price;
			print(visited, path);*/
			return true;
		}
		return false;
	}
	/*UPUTE
	Stvaram red(queue) open i set visited(tako da mi se neponavljaju stanja).
	U open dodajem korijenski node te pokrecem while petlju koja se vrti dokle god
	postoje nezatvorenih cvorova ili dok se ne dode do rjesenja.
	U n(pomocni node) stavljam prvi node iz open(red) te ga dodajem u set posjecenih.
	Ako je n jednak jednom od ciljnih stanja digni zastavicu te pokreni proces rekonstrukcije puta.
	Ako nije u pomocnu listu cvorova m expandiraj n.
	Za svaki element(node) liste m prvo provjeri je li vec posjecen te ako nije stavi ga u red.
	 */
	//queue mi ne radi
	public void BFS(String s0){
		Node start = new Node(s0, null);
		Node n;
		List<Node> m = null;
		Queue<Node> open = new LinkedList<>();
		visited = new HashSet<>();
		open.offer(start);
		visited.add(start.state);
		while (!open.isEmpty()){
			n = open.remove();
			visited.add(n.state);
			if(checkGoal(visited, n)) return;
			m = expand(n);
			for (Node node : m) {
				if (!visited.contains(node.state))
					open.offer(node);
				node.setParent(n);
			}

		}
	}

	public void UCS(String s0){
		Node start = new Node(s0, null);
		Node n;
		List<Node> m = null;
		visited= new HashSet<>();
		/*
		Comparator<Node> priceComparator = Comparator.comparing(Node::getPrice);
		TreeSet<Node> open = new TreeSet<>(priceComparator);
		*/
		TreeSet<Node> open = new TreeSet<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Double p1 = n1.getPrice();
				Double p2 = n2.getPrice();
				String s1 = n1.getState();
				String s2 = n2.getState();
				if (p1.compareTo(p2) != 0) return p1.compareTo(p2);
				else return s1.compareTo(s2);
			}
		});
		open.add(start);
		visited.add(start.state);
		while (!open.isEmpty()){
			n = open.pollFirst();
			visited.add(n.state);
			if(checkGoal(visited, n)) return;
			m = expand(n);
			for (Node node : m) {
				if (!visited.contains(node.state)) {
					if(open.contains(node)){
						if(!node.state.contentEquals(s0))
							open.remove(node);
						node.optimalParent(n);
						//node.setParent(n);
						open.add(node);
					}
					else{
						node.setParent(n);
						open.add(node);
					}
				}
			}

		}
	}

	public void Astar(String s0){
		Node start = new Node(s0, null);
		Node n;
		List<Node> m = null;
		visited = new HashSet<>();
		TreeSet<Node> open = new TreeSet<Node>(new Comparator<Node>() {
			@Override
			public int compare(Node n1, Node n2) {
				Double p1 = n1.getF();
				Double p2 = n2.getF();
				String s1 = n1.getState();
				String s2 = n2.getState();
				if (p1.compareTo(p2) != 0) return p1.compareTo(p2);
				else return s1.compareTo(s2);
			}
		});
		open.add(start);
		visited.add(start.state);
		while (!open.isEmpty()){
			n = open.pollFirst();
			visited.add(n.state);
			if(checkGoal(visited, n)) return;
			m = expand(n);
			for (Node node : m) {
				if (!visited.contains(node.state)) {
					if(open.contains(node)){
						if(!node.state.contentEquals(s0))
							open.remove(node);
						node.optimalParent(n);
						open.add(node);
					}
					else{
						node.setParent(n);
						open.add(node);
					}
				}
			}

		}
	}
	/*UPUTE
	U listu path prvo dodajem n (koji sadrzi goal state) zatim stanja  svakog  roditelj od roditelja
	 */
	private List<Node> recreatePath(Node n) {
		List<Node> path = new ArrayList<>();
		path.add(n);
		while(n.parent != null){
			path.add(n.parent);
			n = n.parent;
		}
		// ako dva nodea imaju istog parenta makni sve izmedu
		Collections.reverse(path);
		return path;
	}


	public void print(Set<String> visited,  List<Node> path){
		System.out.println("[FOUND_SOLUTION]: yes" );
		System.out.println("[STATES_VISITED]: "+ (visited.size()));
		System.out.println("[PATH_LENGTH]: "+ path.size());
		System.out.println("[TOTAL_COST]: "+ path.get(path.size()-1).price);
		System.out.print("[PATH]: ");
		for(int i = 0; i<path.size(); i++){
			if(i<path.size()-1) System.out.print(path.get(i)+ " => ");
			else System.out.print(path.get(i));
		}

	}
	/*UPUTE
	Stvori citac na temelju patha(unutar stringa opisnik_stanja)
	Redom citaj txt datoteku. Ako ima komentar odbaci ga i makni redundantne razmake.
	Prva linija sadrzi polazisno a druga odredisna stanje
	Ostatak su linije funkcije prijelaza oblika state: next_state_1,cost next_state_2,cost
	Zato prvo splitam na temelju : i dobijem trenutno stanje i susjedna stanja
	To zapisujem u mapu prijelaza
	*/
	public  void procitajOpisnikStanja(String opisnik_stanja) throws IOException {
		BufferedReader br
				= new BufferedReader(new FileReader(opisnik_stanja));
		String line;
		while((line=br.readLine()).contentEquals("#"));
		for(int i=0; (line)!=null; i++){
			String unos = line.split("#")[0].trim();
			if (i==0) {
				start = unos;
			}
			else if(i==1) {
				goals = Arrays.asList(unos.split(" "));
			}
			else {
				String[] data = unos.split(":");
				if (data.length > 1) prijelazi.put(data[0], data[1].trim());
				else prijelazi.put(data[0], "");
			}
			line = br.readLine();
		}
	}

	public  void procitajOpisnikHeuristike(String opisnik_heuristike) throws IOException {
		BufferedReader br
				= new BufferedReader(new FileReader(opisnik_heuristike));
		String line;
		for(int i=0; (line=br.readLine())!=null; i++){
			String[] unos = line.split(":");
			int hs = Integer.parseInt(unos[1].trim());
			h.put(unos[0].trim(), hs);
			stanja.add(unos[0].trim());
		}
	}

	public static void main(String ... args) throws IOException {
		Solution s = new Solution();
		String check=null;
		for(int i=0; i< args.length; i++){
			if(args[i].contentEquals("--check-consistent"))
				check = "checkConsistent";
			if(args[i].contentEquals("--check-optimistic"))
				check = "checkOptimistic";
			if(args[i].contentEquals("--alg"))
				alg = args[i+1];
			if(args[i].contentEquals("--ss"))
				opisnik_stanja = args[i+1];
			if(args[i].contentEquals("--h"))
				opisnik_h = args[i+1];
		}
		s.procitajOpisnikStanja(opisnik_stanja);
		if(check!=null) {
			switch (check) {
				case "checkConsistent":
					System.out.println("# HEURISTIC-CONSISTENT " + opisnik_h);
					s.procitajOpisnikHeuristike(opisnik_h);
					s.CheckConsistent();
					break;
				case "checkOptimistic":
					System.out.println("# HEURISTIC-OPTIMISTIC " + opisnik_h);
					alg="ucs";
					s.procitajOpisnikHeuristike(opisnik_h);
					s.CheckOptimistic();
					break;
			}
		}
		if(check==null) {
			switch (alg) {
				case "bfs":
					System.out.println("# BFS");
					s.BFS(start);
					s.print(visited, path);
					break;
				case "ucs":
					System.out.println("# UCS");
					s.UCS(start);
					s.print(visited, path);
					break;
				case "astar":
					System.out.println("# A-STAR " + opisnik_h);
					s.procitajOpisnikHeuristike(opisnik_h);
					s.Astar(start);
					s.print(visited, path);
					break;
			}
		}
	}

	public void CheckOptimistic() {
		for(String stanje: stanja){
			UCS(stanje);
			double hval = (double)h.get(stanje);
			String result = null;
			if(hval<=cost){
				result = "[OK]";
			}
			else{
				optimistic=false;
				result = "[ERR]";
			}
			System.out.println("[CONDITION]: " + result +
					" h("+ stanje+ ") <= h*: " + hval
					+ " <= " + cost
			);
		}
		if(optimistic == true) System.out.println("[CONCLUSION]: Heuristic is optimistic.");
		if(optimistic==false) System.out.println("[CONCLUSION]: Heuristic is not optimistic.");
	}


	public void printConst(String state1, String state2, double cost){
		double hval1 = (double) h.get(state1);
		double hval2 = (double) h.get(state2);
		String result;
		if(hval1<=(hval2+cost)){
			result = "[OK]";
		}
		else{
			result = "[ERR]";
			consistent = false;
		}
		System.out.println("[CONDITION]: " + result +
				" h("+ state1+ ") <= h(" + state2+")" +" + "+ "c: " +
				hval1+" <= " +hval2+" + " + cost
		);
	}
	public void CheckConsistent() {
		for(String stanje: stanja){
			double cost = 0.;
			String[] x;
			String[] aux = prijelazi.get(stanje.trim()).split(" ");
			TreeSet<String> set = new TreeSet<String>(Arrays.asList(aux));
			for(String s: set) {
				x = s.split(",");
				s = x[0].trim();
				cost = Double.parseDouble(x[1].trim());
				printConst(stanje, s, cost);
			}
		}
		if(consistent == true) {
			System.out.println("[CONCLUSION]: Heuristic is consistent.");
		}
		else{
			System.out.println("[CONCLUSION]: Heuristic is not consistent.");
		}

	}

}
