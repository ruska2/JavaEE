import java.util.Arrays;
import java.util.stream.Stream;

public class StreamsMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[] x = {1,2,3,4,5,6,7,8,10};
		System.out.println(diffEvenOdd(x));
	}
	
	
	public static int diffEvenOdd(int[] list) {
		return Arrays.stream(list)
                .map(s -> {
                    if(s % 2 == 0) {
                    	return s *-1;
                    }
                    return s;
                })
                .sum();
    }
		

}
