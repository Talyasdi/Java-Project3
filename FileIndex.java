package il.ac.tau.cs.sw1.ex8.wordsRank;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import il.ac.tau.cs.sw1.ex8.histogram.IHistogram;
import il.ac.tau.cs.sw1.ex8.histogram.HashMapHistogram;
import il.ac.tau.cs.sw1.ex8.wordsRank.RankedWord.rankType;

/**************************************
 *  Add your code to this class !!!   *
 **************************************/

public class FileIndex {
	public static final int UNRANKED_CONST = 30;
	private HashMap<String, HashMap <HashMapHistogram<String> , List<String>>> mapOfFiles;
	private List<RankedWord> rankedList;

	/*
	 * @pre: the directory is no empty, and contains only readable text files
	 */

	public FileIndex (){
		this.mapOfFiles = new HashMap<String, HashMap <HashMapHistogram<String> , List<String>>>();
		this.rankedList = new ArrayList<RankedWord>() ;

	}

	public void indexDirectory(String folderPath) {
		//This code iterates over all the files in the folder. add your code wherever is needed

		File folder = new File(folderPath);
		File[] listFiles = folder.listFiles();
		for (File file : listFiles) {
			// for every file in the folder
			if (file.isFile()) {
				try {
					List<String> wordsInFile = FileUtils.readAllTokens(file);
					HashMapHistogram histogramToAdd = new HashMapHistogram();
					histogramToAdd.addAll(wordsInFile);
					ArrayList<String> ranked = new ArrayList<String>();
					for (Iterator iter = histogramToAdd.iterator() ; iter.hasNext();){
						String s = (String) iter.next();
						ranked.add(s);
					}
					HashMap<HashMapHistogram<String>, List<String>> mp = new HashMap<HashMapHistogram<String>, List<String>>();
					mp.put(histogramToAdd, ranked);

					this.mapOfFiles.put(file.getName(), mp);
				}
				catch (IOException exep){

				}
			}
		}

		for (String currFile : this.mapOfFiles.keySet()){
			HashMapHistogram<String> histoOfFile = getHisto(currFile, this.mapOfFiles);
			for (String currWord : histoOfFile){
				if (isInRankedWordList(currWord, this.rankedList)){
					continue;
				}
				else{
					Map<String,Integer> rankMapOfWord = rankMapForWord(currWord);
					RankedWord newRanked = new RankedWord(currWord, rankMapOfWord);
					this.rankedList.add(newRanked);

				}
			}
		}

	}



  	/*
	 * @pre: the index is initialized
	 * @pre filename is a name of a valid file
	 * @pre word is not null
	 */
	public int getCountInFile(String filename, String word) throws FileIndexException{
		if (mapOfFiles.get(filename) == null) {
			throw new FileIndexException("File Does Not Exists In Folder");
		}
		else {
			HashMapHistogram<String> histoOfFile = getHisto(filename, this.mapOfFiles);
			return histoOfFile.getCountForItem(word.toLowerCase());
		}
	}
	
	/*
	 * @pre: the index is initialized
	 * @pre filename is a name of a valid file
	 * @pre word is not null
	 */
	public int getRankForWordInFile(String filename, String word) throws FileIndexException{
		if (mapOfFiles.get(filename) == null) {
			throw new FileIndexException("File Does Not Exists In Folder");
		}
		else {
			HashMapHistogram<String> histoOfFile = getHisto(filename, this.mapOfFiles);
			List<String> listOfWords =  mapOfFiles.get(filename).get(histoOfFile);
			String lowerWord = word.toLowerCase();
			for (int i=0 ; i< listOfWords.size(); i++){
				if (lowerWord.equals(listOfWords.get(i))){
					return  i+1;
				}
			}
			return listOfWords.size() + UNRANKED_CONST;
		}

	}
	
	/*
	 * @pre: the index is initialized
	 * @pre word is not null
	 */
	public int getAverageRankForWord(String word){
		String lowerWord = word.toLowerCase();
		for (RankedWord currWord : this.rankedList){
			if (lowerWord.equals(currWord.getWord())){
				return currWord.getRankByType(rankType.average);
			}
		}
		return avgOFNoneWord();
	}
	
	
	public List<String> getWordsWithAverageRankSmallerThanK(int k){
		return sortedUnderK(rankType.average, k);
	}
	
	public List<String> getWordsWithMinRankSmallerThanK(int k){
		return sortedUnderK(rankType.min, k);
	}
	
	public List<String> getWordsWithMaxRankSmallerThanK(int k){
		return sortedUnderK(rankType.max, k);
	}


	//Auxiliary Functions
	private List<String> sortedUnderK (rankType cType, int k){
			Collections.sort(this.rankedList, new RankedWordComparator(cType));
			int indexToCut = 0;
			for (int i = this.rankedList.size()-1 ; i>=0 ; i--){
				if (this.rankedList.get(i).getRankByType(cType) < k){
					indexToCut = i+1;
					break;
				}
			}
			List<String> res = new ArrayList<String>();
			for (int j=0 ; j < indexToCut; j++){
				res.add(this.rankedList.get(j).getWord());

			}
			return res;
	}

	private static HashMapHistogram<String> getHisto (String fileName , HashMap<String, HashMap <HashMapHistogram<String> , List<String>>> hashMapFolder){
		Set<HashMapHistogram<String>> myHisoSet = hashMapFolder.get(fileName).keySet();
		HashMapHistogram<String> histoOfFile =  myHisoSet.iterator().next();
		return histoOfFile;
	}

	private static boolean isInRankedWordList (String word, List<RankedWord> rankedList){
		for(RankedWord currWord : rankedList){
			if (word.equals(currWord.getWord())){
				return true;
			}
		}
		return false;
	}

	private HashMap<String, Integer> rankMapForWord (String word){
		HashMap<String,Integer> res = new HashMap<String,Integer>();
		try {
			for (String file : this.mapOfFiles.keySet()){
				HashMapHistogram currHisto = getHisto(file, this.mapOfFiles);
				res.put(file, getRankForWordInFile(file, word));
			}
		}
		catch (FileIndexException e){

		}
		return res;
	}

	private int avgOFNoneWord (){
		int avg = 0;
		for (String file : this.mapOfFiles.keySet()){
			HashMapHistogram currHisto = getHisto(file, this.mapOfFiles);
			avg += (currHisto.getItemsSet().size() + UNRANKED_CONST);
		}
		return (int) Math.round(((double)avg)/(this.mapOfFiles.keySet().size()));
	}

}
