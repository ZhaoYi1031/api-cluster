import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Hierarchical {
private double[][] matrix;
private int[] belong; 
private int dimension;// 数据维度

private int n;
	private class Node {
//		double[] attributes;
		String[] attributes;
		public Node() {
			attributes = new String[11];//double[100];
	 	}
	}
	
	private ArrayList<Node> arraylist;
	
	private class Model {
		int x = 0;
		int y = 0;
		double value = 0;
	}
	
	private Model minModel = new Model();
	
	private double getDistance(Node one, Node two) { //计算两个字符串的距离// 计算两点间的欧氏距离
		double val = 0;
		String s1, s2;
		s1 = one.attributes[0];
		s2 = two.attributes[0];
		System.out.println("one="+s1+" two="+s2);
		
		String[] w1 = s1.split(" ");
		String[] w2 = s2.split(" ");
		Set<String> h1 = new HashSet<String>(); 
		Set<String> h2 = new HashSet<String>(); 
		int l1 = w1.length, l2 = w2.length;
		for (int i = 0; i < l1; ++i){
			String tmp = "";
			for (int j = i; j < l1; ++j){
				if (j == i){
					tmp = w1[j];
				}else{
					tmp = tmp + " " + w1[j];
				}
				h1.add(tmp);
//				System.out.println(tmp);
			}
		}
		for (int i = 0; i < l2; ++i){
			String tmp = "";
			for (int j = i; j < l2; ++j){
				if (j == i){
					tmp = w2[j];
				}else{
					tmp = tmp + " " + w2[j];
				}
				h2.add(tmp);
//				System.out.println(tmp);
			}
		}
		
		Set<String> v1 = new HashSet<String>(); 
		Set<String> v2 = new HashSet<String>(); 
		
		v1.clear();
	    v1.addAll(h1);
	    v1.retainAll(h2);
	    System.out.println("交集："+v1);
	         
	    v2.clear();
	    v2.addAll(h1);
	    v2.addAll(h2);
	    System.out.println("并集："+v2);
	    int tot1 = 0, tot2 = 0;
	    for (String i: v1){
	    	tot1 += i.replace(" ","").length();
	    }
	    for (String i: v2){
	    	tot2 += i.replace(" ","").length();
	    }
	    System.out.println("tot1="+tot1+" tot2="+tot2);
	    return Math.sqrt(1-1.0*tot1*tot1/(tot2*tot2));//1-1.0*tot1/tot2;
//		for (int i = 0; i < dimension; ++i) {
//			val += (one.attributes[i] - two.attributes[i]) * (one.attributes[i] - two.attributes[i]);
//		}
//		return Math.sqrt(val);
	}
	
	private void loadMatrix() {// 将输入数据转化为矩阵
		for (int i = 0; i < matrix.length; ++i) {
		for (int j = i + 1; j < matrix.length; ++j) {
			double distance = getDistance(arraylist.get(i), arraylist.get(j));
			matrix[i][j] = distance;
		}
		}
	}
	
	private Model findMinValueOfMatrix(double[][] matrix) {// 找出矩阵中距离最近的两个簇
		Model model = new Model();
		double min = 0x7fffffff;
		for (int i = 0; i < matrix.length; ++i) {
			if (findRoot(i) != i)
				continue;
			for (int j = i + 1; j < matrix.length; ++j) {
				if (findRoot(j) != j)
					continue;
				if (min > matrix[i][j] && matrix[i][j] != 0) {
					min = matrix[i][j];
					model.x = i;
					model.y = j;
					model.value = matrix[i][j];
				}
			}
		}
		return model;
	 }

		
	 private int findRoot(int x){
//		 System.out.println("x="+x);
		 if (belong[x] == x){
			 return x;
		 }
		 return findRoot(belong[x]);
	 }
	 private double mymax(double p, double q){
		 return (p>q)?p:q;
	 }
	 private void rootMerge(int x, int y){
		 for (int i = 0; i < n; ++i){
			 matrix[y][i] = mymax(matrix[x][i], matrix[y][i]);
		 }
	 }
	 private void merge(int x, int y){
		int fx, fy;
		fx = findRoot(x);
		fy = findRoot(y);
		if (fx < fy){
			belong[fy] = fx;
			rootMerge(fy, fx);
		}else{
			belong[fx] = fy;
			rootMerge(fx, fy);
		}
	 }
	
	 private void processHierarchical(String path, double threshold) {
		 try {
			 PrintStream out = new PrintStream(path);
			 while (true) {// 凝聚层次聚类迭代
				 out.println("Matrix update as below: ");
				 for (int i = 0; i < matrix.length; ++i) {// 输出每次迭代更新的矩阵
				 for (int j = 0; j < matrix.length - 1; ++j) {
					 	out.print(new DecimalFormat("#.0000").format(matrix[i][j]) + " ");
				 	}
				 	out.println(new DecimalFormat("#.0000").format(matrix[i][matrix.length - 1]));
				 }
				 out.println();
				 minModel = findMinValueOfMatrix(matrix);
				 if (minModel.value >= threshold) {// 当找不出距离最近的两个簇时，迭代结束
					 break;
				 }
				 out.println("Combine " + (minModel.x + 1) + " " + (minModel.y + 1));
				 
				 matrix[minModel.x][minModel.y] = matrix[minModel.y][minModel.x] = 0;
				 merge(minModel.x, minModel.y);
				 
				 out.println("The distance is: " + minModel.value);
				 
				 
//				 matrix[minModel.x][minModel.y] = 0;// 更新矩阵
//				 for (int i = 0; i < matrix.length; ++i) {//取较大值// 如果合并了点 p1 与 p2，则只保留 p1,p2 其中之一与其他点的距离，取较小值
//					 if (matrix[i][minModel.x] <= matrix[i][minModel.y]) {
//						 matrix[i][minModel.x] = 0;
//					 } else {
//						 matrix[i][minModel.y] = 0;
//					 }
//					 if (matrix[minModel.x][i] <= matrix[minModel.y][i]) {
//						 matrix[minModel.x][i] = 0;
//					 } else {
//						 matrix[minModel.y][i] = 0;
//					 }
//				 }
			 	}
			 
			 for (int i = 0; i < n; ++i){
				 System.out.print("i="+i+":  ");
				 for (int j = 0; j < n; ++j){
					 if (findRoot(j) == i){
						 if (j!=i)
							 System.out.print("      ");
						 System.out.println("      "+arraylist.get(j).attributes[0]+';');
					 }
				 }
				 System.out.println("");
			 }
			 
			 out.close();
			 System.out.println("Please check results in: " + path);
		 } catch (Exception e) {
			 e.printStackTrace();
		 }
	 	}
	
	 public void setInput(String path) {
		 try {
			 BufferedReader br = new BufferedReader(new FileReader(path));
			 String str;
			 String[] strArray;
			 arraylist = new ArrayList<Node>();
			 int cnt = 0;
			 while ((str = br.readLine()) != null) {
				 System.out.println(str);
//				 strArray = str.split(",");
//				 dimension = strArray.length;
//				 System.out.println("dimension="+dimension);
				 Node node = new Node();
//				 for (int i = 0; i < dimension; ++i) {
//					 node.attributes[i] = Double.parseDouble(strArray[i]);
//				 }
				 node.attributes[0] = str;
				 arraylist.add(node);
				 if (++cnt>10){
					 break;
				 }
			 }
//			 System.exit(0);
			 matrix = new double[arraylist.size()][arraylist.size()];//double
			 belong = new int[arraylist.size()];
			 for (int i = 0; i < arraylist.size(); ++i){
				 belong[i] = i;
			 }
			 n = arraylist.size();
			 loadMatrix();
			 br.close();
		 } catch (IOException e) {
			 e.printStackTrace();
		 }
	 }
	
	 public void printOutput(String path) {
		 processHierarchical(path, 0.999);
	 }
	
	 public static void main(String[] args) {
		 ArrayList<String> arraylist = new ArrayList<String>();
		 arraylist.add("fuck");
		 arraylist.add("u");
		 arraylist.add("ok");
//		 System.out.println(arraylist[0]);
		 Hierarchical hi = new Hierarchical();
		 hi.setInput("usage_data/test.txt");//"hierarchical.txt");
		 hi.printOutput("hierarchical_results.txt");
	 }
}