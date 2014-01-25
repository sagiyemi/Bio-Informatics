package alignments;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

public class SequenceGenerator {


	public static void main(String[] args) {
		char[] DNA = {'T', 'G', 'C', 'A'};
		Scanner sc = new Scanner(System.in);
		System.out.println(Double.POSITIVE_INFINITY+1);
		System.out.println("please enter the file name");
		String fName = sc.nextLine();
		System.out.println("please enter the length of the random sequence");
		int length = sc.nextInt();
		try{
			BufferedWriter generator= new BufferedWriter(new FileWriter(new File(fName)));
			Random r = new Random();
			for (int i = 0; i < length; i++) 
				generator.append(DNA[r.nextInt(4)]);
				generator.close();
		}
		catch (Exception e){
			System.out.println(e.getMessage());
		}
		sc.close();
	}

}
