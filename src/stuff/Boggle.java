package stuff;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class Boggle {
	static HashSet<String> foundWords = new HashSet<String>();
	static HashSet<String> dict = new HashSet<String>(); 
	static HashSet<String> prefix = new HashSet<String>();
	static char[][] board;
	
	static int[][] neighbors = {
			{1,1},
			{1,0},
			{0,1},
			{-1,0},
			{0,-1},
			{-1,-1},
			{-1,1},
			{1,-1}
	};
	
	public static ArrayList<int[]> getAllNeighbors(int row, int col){
		ArrayList<int[]> all = new ArrayList<int[]>();
		for(int[] x: neighbors) {
			int nr = row + x[0];
			int nc = col + x[1];
			if(nr >= 0 && nr < board.length && nc >= 0 && nc < board[0].length) {
				int[] y = {nr,nc};
				all.add(y);
			}
		}
		return all;
	}
	
	public static void dfs(int row, int col, ArrayList<int[]> visited, String curr) {
		String letter = Character.toString(board[row][col]);
		int[] p = {row, col};
		visited.add(p);
		
		curr += letter;

		if(dict.contains(curr) && curr.length() >= 3 /*&& !foundWords.contains(curr)*/) {
			foundWords.add(curr);
			//System.out.println(curr);
		}
		
		if(!prefix.contains(curr)) {
			return;
		}
		
		ArrayList<int[]> next = getAllNeighbors(row,col);
		for(int[] n:next) {
			if(!isInList(visited,n)) {
				ArrayList<int[]> b = new ArrayList<int[]>();
				for(int[] a:visited) {
					b.add(a.clone());
				}
				dfs(n[0],n[1], b,curr);
			}
		}
	}
	
	public static HashSet<String> solve(char[][] bo){
		getDictionary();
		board = new char[bo.length][bo[0].length];
		for(int r = 0; r < bo.length; r++) for(int c = 0; c < bo[0].length; c++) board[r][c] = Character.toLowerCase(bo[r][c]);
		for(int i = 0; i < board.length; i++) {
			for(int j = 0; j < board[0].length; j++) {
				dfs(i, j, new ArrayList<int[]>(), "");
			}
		}
		
		return foundWords;
	}
	
	public static void getDictionary() {
		try {
			File file = new File("enable1.txt");
			BufferedReader br = new BufferedReader(new FileReader(file));

			String line;
			while ((line = br.readLine()) != null) {
				dict.add(line);
				for(int i = 1; i < line.length(); i++) {
					String temp = line.substring(0,i);
					if(!prefix.contains( temp )) {
						prefix.add(temp);
					}
				}
			}
		} catch(Exception e) {System.out.println(e);}

	}
	
	public static boolean isInList(
	        final List<int[]> list, final int[] candidate) {

	    return list.stream().anyMatch(a -> Arrays.equals(a, candidate));
	            //  ^-- or you may want to use .parallelStream() here instead
	}
	
}
