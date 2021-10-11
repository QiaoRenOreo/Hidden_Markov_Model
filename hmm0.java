
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;


//kattis example
//4 4 0.2 0.5 0.3 0.0 0.1 0.4 0.4 0.1 0.2 0.0 0.4 0.4 0.2 0.3 0.0 0.5
//4 3 1.0 0.0 0.0 0.0 1.0 0.0 0.0 0.0 1.0 0.2 0.6 0.2
//1 4 0.0 0.0 0.0 1.0

//2-coins example
//2 2 0.5 0.5	0.5 0.5	
//2 2 0.9 0.1 0.5 0.5
//1 2 0 1


// quiz
//3 3 0.8 0.2 0.0 0.0 0.9 0.1 0.5 0.3 0.2
//3 4 0.7 0.1 0.0 0.2 0.0 0.3 0.4 0.3 0.0 0.1 0.0 0.9
//1 3 1 0 0

public class hmm0
{
	
	public static void main(String[] args) 
	{
		System.out.println("enter state transition matrix A, emission matrix B, and initial state probability distribution Pie: "); 
		Scanner in = new Scanner(System.in); 
		
		int Arow=in.nextInt(); 
		int Acol=in.nextInt(); 
		int Alen=Arow*Acol;
		double[] A_1D= new double[Alen];
        for(int i=0;i<Alen;i++) 
        {
        	A_1D[i] = in.nextDouble();
        }
//		System.out.println("A complete");
		
		int Brow=in.nextInt(); 
		int Bcol=in.nextInt(); 
        int Blen=Brow*Bcol;
        double[] B_1D= new double[Blen];
        for(int j=0;j<Blen;j++) {
        	B_1D[j] = in.nextDouble();
        }		
//		System.out.println("B complete");
		
		int Pierow=in.nextInt(); // Pie: initial state or current state
		int Piecol=in.nextInt(); // Pie: initial state or current state	
		int Pielen=Pierow*Piecol;
		double[] Pie_1D= new double[Pielen];
        for(int i=0;i<Pielen;i++) 
        {
        	Pie_1D[i] = in.nextDouble();
        }
//		System.out.println("Pie complete");
		
        in.close(); //Scanner Closed
        
        
        double A[][]=convert1Dto2D   ( A_1D,   Arow,   Acol);
        double B[][]=convert1Dto2D   ( B_1D,   Brow,   Bcol);
        double Pie[][]=convert1Dto2D (Pie_1D, Pierow, Piecol);
        
		
        
        double q2[][] =matrixMultiplication (Pie, A );
        System.out.println(" ");
        System.out.println("Question2:");
        System.out.print(q2);

        System.out.println(" ");
        System.out.println("Question3:"); //		// compute the probability of observing each observation:
		double q3[][] =matrixMultiplication (q2, B ); // Q3: the result of Q2 is multiplied with the observation matrix
		double q3_OneDec[][] =convertToOneDecimal (q3);
		List<Double> q3_list=convert2Dto1D(q3_OneDec);
		System.out.print("1"+" "+Bcol+" ");
		printOneDList (q3_list);
		
		


		
	}	
	
	public static double[][] matrixMultiplication (double m1[][], double m2[][] ) 
	{
		
		int m1row= m1.length;
		int m1col= m1[0].length;
		int m2row= m2.length;
		int m2col= m2[0].length;
		
		double[][] result = new double [m1row][m2col];
		if (m1col==m2row) 
		{
			double sum=0;
			
			for (int i=0; i<m1row;i++) 
			{
				for (int j=0;j<m2col;j++) 
				{
					for (int k=0;k<m1col;k++)
					{
						sum = sum + m1[i][k] * m2[k][j];
					}
					result[i][j]=sum;
					sum=0;
				}
			}			
		}
		else
		{
			System.out.println("m1col does not equal to m2row");
		}
		
		return result;
	}

	
	public static double[][] convert1Dto2D(double[] oneDArray, int row, int col )
	{
		
		double twoDArray[][] = new double [row][col];
		for (int i=0;i<row;i=i+1)
		{	
			for (int j=0;j<col;j=j+1)
			{
				twoDArray[i][j]=oneDArray[i*col+j];
			}
		}
			
		return twoDArray;
	}
	
	
	public static List<Double> convert2Dto1D(double[][] twoDArray)
	{
		List<Double> l = new ArrayList<Double>();
		
	    for(int i=0;i<twoDArray.length;i++)
	    {
	        for(int j=0;j<twoDArray[i].length;j++)
	        {
	        	l.add(twoDArray[i][j]);
	        	
	        }
	    }
	    
//define a function which returns type: array[].
//		int len=row*col;
//		double oneDArray[]=new double[len];
//		for (int i=0;i<row;i=i+1)
//		{	
//			for (int j=0;j<col;j=j+1)
//			{
//				oneDArray[i*col+j] = twoDArray[i][j];
//			}
//				
//		}
		return l;
	}
	

	public static void printTwoDArray (double[][]twoDArray)
	{
		int row= twoDArray.length;
		int col= twoDArray[0].length;
		System.out.println(" ");
		for (int i=0;i<row;i++) 
		{
			for (int j=0;j<col;j++) 
			{
				System.out.print(twoDArray[i][j] + " ");
			}
			System.out.println("  ");
		}		
	}
	

	public static double[][] convertToOneDecimal (double m[][]) 
	{
		int row= m.length;
		int col= m[0].length;
		double[][] new_m= new double [row][col];
		for (int i=0;i<row;i++) 
		{
			for (int j=0;j<col;j++) 
			{
				new_m[i][j] = Math. round(m[i][j] * 100.0) / 100.0; //df1.format(m[row][col]);
			}
		}				
		return new_m;
	}
	
	public static void printOneDList (List<Double> l)
	{
		for(int i=0;i<l.size();i++){
		    System.out.print(l.get(i)+" ");
		} 
	}

}



