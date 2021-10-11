import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays; 
import java.util.Comparator;
import java.util.LinkedHashMap;

//kattis example
//4 4 0.0 0.8 0.1 0.1 0.1 0.0 0.8 0.1 0.1 0.1 0.0 0.8 0.8 0.1 0.1 0.0 
//4 4 0.9 0.1 0.0 0.0 0.0 0.9 0.1 0.0 0.0 0.0 0.9 0.1 0.1 0.0 0.0 0.9 
//1 4 1.0 0.0 0.0 0.0 
//4 1 1 2 2 

//sheet example
//4 4 0.6 0.1 0.1 0.2 0.0 0.3 0.2 0.5 0.8 0.1 0.0 0.1 0.2 0.0 0.1 0.7
//4 4 0.6 0.2 0.1 0.1 0.1 0.4 0.1 0.4 0.0 0.0 0.7 0.3 0.0 0.0 0.1 0.9
//1 4 0.5 0.0 0.0 0.5
//4 2 0 3 1

public class hmm2 {
	
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
		
		
		int Brow=in.nextInt(); 
		int Bcol=in.nextInt(); 
        int Blen=Brow*Bcol;
        double[] B_1D= new double[Blen];
        for(int j=0;j<Blen;j++) {
        	B_1D[j] = in.nextDouble();
        }		
		
		
		int Pierow=in.nextInt(); // Pie: initial state or current state
		int Piecol=in.nextInt(); // Pie: initial state or current state	
		int Pielen=Pierow*Piecol;
		double[] Pie_1D= new double[Pielen];
        for(int i=0;i<Pielen;i++) 
        {
        	Pie_1D[i] = in.nextDouble();
        }
		

		
		int totalObsTime=in.nextInt(); 
		int [] ObsSeq = new int[totalObsTime];
        for(int i=0;i<totalObsTime;i++) 
        {
        	ObsSeq[i] = in.nextInt();
        }
    	       
        in.close(); //Scanner Closed
        
        
        double A[][]=convert1Dto2D   ( A_1D,   Arow,   Acol);
        double B[][]=convert1Dto2D   ( B_1D,   Brow,   Bcol);
        double Pie[][]=convert1Dto2D (Pie_1D, Pierow, Piecol);

        int totalNumState=Brow;		
	

		double[][] sigma0 = initialSigma (0,totalNumState, totalObsTime, ObsSeq, Pie, B);

	
		double [][][] allSigma= computeAllSigma (totalNumState, ObsSeq, sigma0 , A, B);

		int [] track= stateTrack ( allSigma, totalNumState, totalObsTime );
		printOneDArray(track);

	}
	
	static double [][] computeSigma(int t, int totalNumState, int ObsSeq[], double [][] sigma_lastStep, double[][]A, double[][]B)
	{
		int obs=ObsSeq[t];
		LinkedHashMap<String, String> map = new LinkedHashMap<>();
		
		double a [][]=new double [totalNumState][2];
		
		for (int j=0; j<totalNumState; j++)
		{
			double[] probList = new double[totalNumState];
			for (int i=0; i<totalNumState; i++)
			{
				 probList [i] =  sigma_lastStep[i][0]*A[i][j]*B[j][obs] ;
			}
			a[j][0]=Max_value(probList);
			a[j][1]=Max_index(probList);
		}
		
		return a;
	}

	
	
	
	static double [][][] computeAllSigma (int totalNumState, int ObsSeq[], double sigma [][], double[][]A, double[][]B)
	{

		
		double totalSigma[][][] = new double [ ObsSeq.length -1 ][totalNumState][2];
		
		for (int t=1; t<ObsSeq.length; t++)
		{
			double sigma_new[][] = computeSigma(t, totalNumState, ObsSeq, sigma, A, B);
			totalSigma[t-1]= sigma_new;
			sigma=sigma_new;
		}
		return totalSigma;
	}
	
	static int [] stateTrack ( double allSigma[][][], int totalNumState, int totalObsTime )
	{
		int [] track= new int[totalObsTime];
		
		double [] sigmaList = new double [totalNumState];
		
		for (int i=0; i<totalNumState; i++)
		{
			sigmaList[i]=allSigma[allSigma.length-1][i][0];
		}
		int finalSateIndex= (int) Max_index ( sigmaList);
		track[totalObsTime-1]=finalSateIndex;
		
		double preState=finalSateIndex;
		for (int j=1; j<totalObsTime; j++)
		{
			preState= allSigma [allSigma.length-j][(int) preState][1];
			track[totalObsTime-1-j]=(int) preState;
			
		}		
	
		return track;
	}
	
	
	public static double Max_value(double[] probList)
	{     //find the highest value of an array

	    double Max_value = probList[0];
	    for (int i = 0; i < probList.length; i++)
	    {
	        if (probList[i] > Max_value) 
	        {
	            Max_value = probList[i];
	        }

	    }
	    return Max_value;
	}	
	
	public static double Max_index(double[] probList)
	{     //find the index of max value, and therefore the state

	    double Max_value = probList[0];
	    int Max_index = 0;
	    for (int i = 0; i < probList.length; i++)
	    {
	        if (probList[i] > Max_value) 
	        {
	            Max_value = probList[i];
	            Max_index = i;
	        }

	    }
	    return Max_index;
	}

	

	
	public static double[][] initialSigma (int t, int totalNumState, int totalObsTime, int ObsSeq[], double Pie[][], double B[][])
	{
		int obs=ObsSeq[t]; 
		double sigma0[][]= new double[totalNumState][2];		
		
		for (int j=0; j<totalNumState; j++) 
		{
			sigma0[j][0]=Pie[0][j]*B[j][obs];	
			sigma0[j][1]=-1;
		}

		return sigma0;
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
//		    System.out.print(l.get(i)+" ");
		} 
	}
	
	public static void printOneDArray (int[] l)
	{
		for(int i=0;i<l.length;i++)
		{
		    System.out.print(l[i]+" ");
		} 		
	}
}
