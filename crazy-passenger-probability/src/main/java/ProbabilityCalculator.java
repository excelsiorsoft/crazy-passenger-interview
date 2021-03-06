
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class ProbabilityCalculator {
	
	public ProbabilityCalculator(int capacity, int numberOfTrials, boolean verbose) {
		this.capacity = capacity;
		this.numberOfTrials = numberOfTrials;
		this.verbose = verbose;
	}

	private int capacity;
	private int numberOfTrials;
	private boolean verbose;
	
	@SuppressWarnings("unused")
	private static class Tuple{
		
		public int number;
		public boolean isEmpty = true;
		public int ticket;
		
		@Override
		public String toString() {
			return isEmpty?"[_]":"["+ticket+"]";
		}
	}
	
	 
	public static void main(String [] args) {
		
		int capacity = Integer.parseInt(args[0]);
		int numOfTrials = Integer.parseInt(args[1]);
		boolean verbose = Boolean.parseBoolean(args[2]);
		
		ProbabilityCalculator calc = new ProbabilityCalculator(capacity,numOfTrials,verbose);
		double probability = calc.calculate(capacity,numOfTrials);
		System.out.println("Probability: " +probability);
			
	}
	
	private Map<Integer, Tuple> initState(Stream<Integer> range) {
		return range.collect(Collectors.toMap(Function.identity(), v->new Tuple()));
	}
	
	public double calculate(int capacity, int numOfTrials) {
		
		Random random = new Random();
		int accumOfPositives = 0;
		
		
		for (int n = 0; n < numOfTrials; n++) {
			
			Stream<Integer> range = IntStream.rangeClosed(1,capacity).boxed();
			Map<Integer, Tuple> state = initState(range);
			
			for (int tkt = 1; tkt <= capacity; tkt++) {

				if(tkt == 1) { //handle first passenger
					Tuple seat = state.get(findRandomSeat(random, 1, capacity));
							seat.isEmpty=false;
							seat.ticket=tkt;
					if(verbose) print(n,tkt,state);
					continue;
				}
				
				if (tkt == capacity) { //decide if trial was successful
					if(state.get(tkt).isEmpty) {
						accumOfPositives++;
					}
				}
				
				Tuple assignedSeat = state.get(tkt);
				if(assignedSeat.isEmpty) {				//if seat is empty, occupy your own seat
					assignedSeat.isEmpty=false;
					assignedSeat.ticket=tkt;
				}else {									//take a random seat 
					Tuple randomSeat = state.get(findEmptySeat(state));
					randomSeat.isEmpty=false;
					randomSeat.ticket=tkt;
				}
								
				if(verbose) print(n,tkt,state);
			}
			
			if(verbose) System.out.println("...");
		}
		

		return ((double)accumOfPositives)/numOfTrials; //calculate probability
		
	}
	
	private void print(int n, int tkt, Map<Integer, Tuple> state) {
		System.out.println("n="+n+",tkt="+tkt+","+state);
	}
	
	private int findEmptySeat(Map<Integer, Tuple> state) {
		int result = 0;
		for (Map.Entry<Integer, Tuple> entry : state.entrySet()) {
            if (Boolean.TRUE.equals(entry.getValue().isEmpty)) {
                result = entry.getKey();
                break;
            }
        }
		return result;
	}
	
	private int findRandomSeat(Random random, int min, int max) { 
		return random.nextInt(max - min + 1) + min;
	}

}
