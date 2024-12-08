package main;
import java.io.IOException;

import utils.utils;

public class Test3 {
	public static void main(String[] args) {
		String[] inputfile = {"te.json", "te_Tranthaiabcabc.json", "te1_Tranthaiabcabc.json", "te_@ThiTrn600349781.json", "te1_@ThiTrn600349781.json", "te_@QucThiTrn174803.json", "te1_@QucThiTrn174803.json",
				"te_@ThuyLinh62474.json", "te1_@ThuyLinh62474.json"};
		try {
			utils.mergeJsonFiles(inputfile, "database.json");
			utils.extractUrlsToFile("database.json", "kol_links2.txt");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
