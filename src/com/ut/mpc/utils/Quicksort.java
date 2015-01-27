/*
 * Adapted from: http://www.vogella.com/tutorials/JavaAlgorithmsQuicksort/article.html
 * 
 */

package LSTStructure.src.com.ut.mpc.utils;

import java.util.ArrayList;
import java.util.List;

public class Quicksort  {
  private List<Double> numbersDub;
  private int number;
  
  public static void main(String[] args){
	  Quicksort qs = new Quicksort();
	  List<Double> temp = new ArrayList<Double>();
	  
	  temp.add(new Double(7.999));
	  temp.add(new Double(8));
	  temp.add(new Double(8.0001));
	  temp.add(new Double(2));
	  temp.add(new Double(4));
	  
	  qs.sort(temp, 0, temp.size() - 1);
	  
	  for(int i = 0; i < temp.size(); i++){
		  System.out.println(temp.get(i));
	  }
  }

  public void sort(List<Double> values, int low, int high){
	    // check for empty or null array
	    if (values == null || values.size() == 0){
	      return;
	    }
	    this.numbersDub = values;
	    this.number = values.size();
	    quicksort(low, high); 
  }
  
  private void quicksort(int low, int high) {
	    int i = low, j = high;
	    // Get the pivot element from the middle of the list
	    double pivot = this.numbersDub.get((low + (high-low)/2));

	    // Divide into two lists
	    while (i <= j) {
	      // If the current value from the left list is smaller then the pivot
	      // element then get the next element from the left list
	      while (this.numbersDub.get(i).doubleValue() < pivot) {
	        i++;
	      }
	      // If the current value from the right list is larger then the pivot
	      // element then get the next element from the right list
	      while (this.numbersDub.get(j).doubleValue() > pivot) {
	        j--;
	      }

	      // If we have found a values in the left list which is larger then
	      // the pivot element and if we have found a value in the right list
	      // which is smaller then the pivot element then we exchange the
	      // values.
	      // As we are done we can increase i and j
	      if (i <= j) {
	        exchangeDub(i, j);
	        i++;
	        j--;
	      }
	    }
	    // Recursion
	    if (low < j)
	      quicksort(low, j);
	    if (i < high)
	      quicksort(i, high);
  }
  
  private void exchangeDub(int i, int j) {
	Double temp = this.numbersDub.get(i);
	this.numbersDub.set(i, this.numbersDub.get(j));
	this.numbersDub.set(j, temp);
  }

} 
