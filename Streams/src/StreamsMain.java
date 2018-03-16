import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamsMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] x = {4,6,3,9};
		System.out.println(diffEvenOdd(x));
		System.out.println(primes(100));
	}
	
	
	public static int diffEvenOdd(int[] list) {
		return Math.abs(Arrays.stream(list)
                .map(s -> {
                    if(s % 2 == 0) {
                    	return s *-1;
                    }
                    return s;
                })
                .sum());
    }
	
	public static int numberOfMinimal(int[] list) {
		int i;
		/*return Arrays.stream(list)
				.sorted()
				.forEach(i ->  System.out.println())
				.max();*/
		return 0;
	}
	
	public static List<Integer> primes(int n) {
		Stream<Integer> infiniteStream = Stream.iterate(0, i -> i + 1);
		List<Integer> collect = infiniteStream
		  .limit(n)
		  .filter(i -> isPrime(i))
		  .collect(Collectors.toList());
		return collect;
		 
	}
	
	
	static boolean isPrime(int n) {
	    for(int i=2;i<n;i++) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}


}
