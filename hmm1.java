import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.lang.Math;
import java.text.DecimalFormat;

//kattis example
//4 4 0.0 0.8 0.1 0.1 0.1 0.0 0.8 0.1 0.1 0.1 0.0 0.8 0.8 0.1 0.1 0.0 
//4 4 0.9 0.1 0.0 0.0 0.0 0.9 0.1 0.0 0.0 0.0 0.9 0.1 0.1 0.0 0.0 0.9 
//1 4 1.0 0.0 0.0 0.0 
//8 0 1 2 3 0 1 2 3
//result: 0.090276 

public class hmm1 {
	public static void main(String[] args) 
	{

		//System.out.println("enter state transition matrix A, emission matrix B, initial state probability distribution Pie, and a sequence of observation: "); 
		Scanner in = new Scanner(System.in); 
		
		int Arow=in.nextInt(); 
		int Acol=in.nextInt(); 
		int Alen=Arow*Acol;
		double[] A_1D= new double[Alen];
        for(int i=0;i<Alen;i++) 
        {
        	A_1D[i] = in.nextDouble();
        }
		//System.out.println("A complete");
		
		int Brow=in.nextInt(); 
		int Bcol=in.nextInt(); 
        int Blen=Brow*Bcol;
        double[] B_1D= new double[Blen];
        for(int j=0;j<Blen;j++) {
        	B_1D[j] = in.nextDouble();
        }		
		//System.out.println("B complete");
		
		int Pierow=in.nextInt(); // Pie: initial state or current state
		int Piecol=in.nextInt(); // Pie: initial state or current state	
		int Pielen=Pierow*Piecol;
		double[] Pie_1D= new double[Pielen];
        for(int i=0;i<Pielen;i++) 
        {
        	Pie_1D[i] = in.nextDouble();
        }
		//System.out.println("Pie complete");

		
		int totalObsTime=in.nextInt(); 
		int [] ObsSeq = new int[totalObsTime];
        for(int i=0;i<totalObsTime;i++) 
        {
        	ObsSeq[i] = in.nextInt();
        }
    	//System.out.print("Observation sequence complete" );        
        in.close(); //Scanner Closed
        
        
        double A[][]=convert1Dto2D   ( A_1D,   Arow,   Acol);
        double B[][]=convert1Dto2D   ( B_1D,   Brow,   Bcol);
        double Pie[][]=convert1Dto2D (Pie_1D, Pierow, Piecol);

        int totalNumState=Brow;
        

        // when time step=0:
		double alpha0 [] = initialAlpha (0, totalNumState,ObsSeq,Pie,B );
		
		// time step t starts from 1 to (totalTime-1): 
		double alpha_final [] = updateAlpha ( totalObsTime, totalNumState, ObsSeq, alpha0, A, B);
		double totalProb= sumProb (alpha_final);
		
		//System.out.println("  ");
		System.out.print(display6Decimals (totalProb));
		
	}	
	
	
	public static String display6Decimals (double i)
	{
		DecimalFormat f = new DecimalFormat("##.000000");
		String s=f.format(i);
		return s;
	}
	
	
	
	public static double[] initialAlpha (int t, int totalNumState, int ObsSeq[], double Pie[][], double B_2D[][])
	{
		int obs=ObsSeq[t]; 
		double alpha0[]= new double[totalNumState];
		
		for (int i=0; i<totalNumState; i++) 
		{
			alpha0[i]=Pie[0][i]*B_2D[i][obs];
		}
		
		return alpha0;
		
		
	}

	public static double[] updateAlpha ( int totalTime, int totalNumState, int ObsSeq[], double alpha[], double A_2D[][], double B_2D[][])
	{
		
		for (int t=1; t<totalTime; t++)
		{
			double alpha_new[]= computeAlpha (t, totalNumState,  ObsSeq, alpha, A_2D, B_2D);
			alpha=alpha_new;
		}
		return alpha;
	}
	
	public static double[] computeAlpha (int t, int totalNumState, int ObsSeq[], double alpha_lastStep[], double A_2D[][], double B_2D[][])
	{ 
		//t: time step goes from 1. because initialized time was 0 
		int obs=ObsSeq[t];
		double alpha_thisStep[]= new double[totalNumState];

		    
		for (int j=0; j< totalNumState; j++) 
		{
			double stateTrackProb = 0;
			for (int i=0; i<totalNumState; i++)
			{
				stateTrackProb += alpha_lastStep[i]*A_2D[i][j] ;
			}
			alpha_thisStep[j] = stateTrackProb*B_2D[j][obs];				
		}
		
		return alpha_thisStep;
		
	}
		

	
	public static double sumProb (double prob[])
	{
		int length = prob.length;
		double totalProb=0;
		for (int i=0; i< length; i++) 
		{
			totalProb=totalProb+prob[i];
		}
		return totalProb;
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
		for(int i=0;i<l.size();i++)
		{
		    System.out.print(l.get(i)+" ");
		} 
	}

}
