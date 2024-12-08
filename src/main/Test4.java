package main;
import java.io.IOException;

import utils.utils;
public class Test4 {
	public static void main(String[] args) {
        try {
			utils.removeDuplicates("processed_kol_links.txt", "processed1_kol_links.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
