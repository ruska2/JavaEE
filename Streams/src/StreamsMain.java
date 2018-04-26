import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.function.Function;
import java.util.function.Supplier;
public class StreamsMain {

	public static void main(String[] args) {
		int[] x = {4,6,3,9,3,1,1,1};
		int[][] x1 = {{1,2,3,6},{3,2,8,6},{1,8,3,6}};
		List<Integer> l1 = Arrays.asList(1,2,3);
		List<Integer> l2 = Arrays.asList(3,4,5);
		System.out.println(diffEvenOdd(x));
		System.out.println(getPrimes(100));
		System.out.println(numberOfMinimal(x));
		System.out.println(intersect(x1));
		System.out.println(transpose(x1));
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
		List<Integer> l = Arrays.stream(list)
				.boxed()
				.collect(Collectors.toList());
		Long res = l.stream()
				.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
				.get(l.stream()
						.sorted()
						.findFirst()
						.get());
		return res.intValue();
	}
	
	public static List<Integer> getPrimes(int n) {
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

	
	public static List<Integer> intersect(int[][] list) {
		List<List<Integer>> l = Arrays.stream(list)
				.distinct()
				.map(r -> Arrays.stream(r)
						.boxed()
				.collect(Collectors.toList()))
				.collect(Collectors.toList());
		return l.stream()
				.min(Comparator.comparing(Collection::size))
				.get()
				.stream()
				.filter(p -> l.stream().allMatch(e -> e.contains(p))).collect(Collectors.toList());
 
    }
	
	public static List<List<Integer>> transpose(int[][] list){
		Supplier<Stream<Integer>> cols = () -> Stream.iterate(0, i -> i+1).limit(list[0].length);
		Supplier<Stream<Integer>> rows = () -> Stream.iterate(0, i -> i+1).limit(list.length);
		return cols
				.get()
				.map(i -> rows
						.get()
						.map(j -> list[j][i])
						.collect(Collectors.toList()))
				.collect(Collectors.toList());

	}
	
}
