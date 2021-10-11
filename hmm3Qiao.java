import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.lang.Math;
import java.text.DecimalFormat;
import java.util.Arrays; 
import java.util.Comparator;
import java.util.LinkedHashMap;

public class hmm3Qiao {
	
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
        
        
        
        int maxIters = 100;//maximum number of re-estimation iterations
		int iters = 0;
		double a = Double.NEGATIVE_INFINITY;
		double b = 0;
		
	
		while (iters < maxIters && b > a) {
			System.out.print("b:"+b );
			// 2. alpha pass

			// compute alpha zero
		    double[][] alpha = new double[totalObsTime][Arow];
		    double[] c = new double[totalObsTime];
		    
			c[0]=0;
			for (int i=0;i<Arow;i++)
			{
				alpha[0][i] = Pie[0][i] * B[i][ObsSeq[0]];
	            c[0] += alpha[0][i];
			}
			
			// scale the alpha zero
			c[0] = 1/c[0];
			for (int i=0;i<Arow;i++)
			{
				alpha[0][i] = c[0] * alpha[0][i];
			}
		
		
			// compute alpha t(i)
			for (int t=1;t<totalObsTime;t++) 
			{
				c[t] = 0;
				for (int i=0;i<Arow;i++)
				{
					double alphaT = 0;
					for (int j=0;j<Arow;j++) {
						alphaT += (alpha[t-1][j] * A[j][i]);
					}
					alpha[t][i] = alphaT * B[i][ObsSeq[t]];
					c[t] += alpha[t][i];
				}
					
					//scale alpha t(i)
					c[t] = 1/c[t];
					for (int i=0;i<Arow;i++)
					{
						alpha[t][i] = c[t] * alpha[t][i];
					}
				
			}
		
			
			// 3. beta pass	
		
			//// Let beta(t-1)(i) = 1, scaled by c(totalObsTime-1)
		    double[][] beta = new double[totalObsTime][Arow]; 
		    for (int i=0;i<Arow;i++) {
		    	beta[totalObsTime-1][i] = c[totalObsTime-1];	
		    }
		    
		    //beta pass

		    for (int t=totalObsTime-2;t>0;t--) {
		    	for (int i=0;i<Arow;i++) {
		    		double betaT = 0;
		    		for (int j=0;j<Arow;j++) {
		    			betaT +=  A[i][j] * B[j][ObsSeq[t+1]] * beta[t+1][j];
		    		}
		    		//scale beta(t)(i) with same scale factor as alphat(i)
		    		beta[t][i] = betaT * c[t];
		    	}
		    }
		    
		    
			// 4. compute gamma(t)(i,j) and gamma(t)(i)
		    
		    // No need to normalize gamma(t)(i,j) since using scaled alpha and beta
		    double[][][] digamma = new double[totalObsTime][Arow][Arow];     /////////////////////is this 2D or 3D?
		    double[][] gamma = new double[totalObsTime][Arow];
		   
		    for (int t=0; t<totalObsTime-1; t++) {
		    	for (int i=0; i<Arow; i++) {
		    		gamma[t][i] = 0;
		    		for (int j=0; j<Arow; j++) {
		    			digamma[t][i][j] = alpha[t][i] * A[i][j] * B[j][ObsSeq[t+1]] * beta[t+1][j];
		    			gamma[t][i] += digamma[t][i][j];
		    		}
		    	}
		    }
		    		
		   // Special case for gamma(totalObsTime-1)(i) (as above, no need to normalize)		
		   for (int i=0; i<Arow; i++) { 
			   gamma[totalObsTime-1][i] = alpha[totalObsTime-1][i];
		   }
		   
		   
		   // 5. Re-estimate A, B, Pi
		   
		   // re-estimate Pi
		   for (int i=0;i<Arow;i++) {
	           Pie[0][i] = gamma[0][i];
		   }
	       
	       // re-estimate A
	       for (int i=0;i<Arow;i++) {
	           double denom = 0;
	           for (int t=0; t<totalObsTime-1; t++) {
	               denom += gamma[t][i];
	           }
	           for (int j=0;j<Arow;j++) {
	               double numer = 0;
	               for (int t=0;t<totalObsTime-1;t++) {
	                   numer += digamma[t][i][j];
	               }
	               A[i][j] = numer/denom;
	           }
	       }
	       
	       // re-estimate B
	       for (int i=0;i<Arow;i++) {
	           double denom = 0;
	           for (int t=0;t<totalObsTime;t++) {
	               denom += gamma[t][i];
	           }
	           for (int j=0;j<Bcol;j++) {
	               double numer = 0;
	               for (int t=0;t<totalObsTime;t++) {
	                   if (ObsSeq[t] == j){
	                       numer += gamma[t][i];
	                   }
	               }
	               B[i][j] = numer/denom;
	           }
	       }
	       
	       // 6. Compute log[P(ObsSeq|lambda)]
	       double logProb = 0;
	       for (int i=0;i<totalObsTime;i++) 
	       { 
	    	   logProb += Math.log(c[i]); 
	       }
	       logProb = -logProb;
	       
	       a=b;
	       b=logProb;
	       // 7. To iterate or not to iterate, that is the question....
	       iters += 1;
	       
	       
		}
		
		System.out.print(A.length + " " + A[0].length + " ");
		printTwoDArray(A);

		
		
		System.out.println();

		System.out.print(B.length + " " + B[0].length + " ");
		printTwoDArray(B);

		
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
//		System.out.println(" ");
		for (int i=0;i<row;i++) 
		{
			for (int j=0;j<col;j++) 
			{
				System.out.print(twoDArray[i][j] + " ");
			}
//			System.out.println("  ");
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