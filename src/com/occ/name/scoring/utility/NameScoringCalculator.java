package com.occ.name.scoring.utility;


import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import com.occ.name.scoring.entity.Name;
import com.occ.name.scoring.strategy.intf.NameScoringStrategy;

public class NameScoringCalculator {

	private String file;
	private String strategyType;
	
	public String getFile() {
		return file;
	}
	public void setFile(String file) {
		this.file = file;
	}
	public String getStrategyType() {
		return strategyType;
	}
	public void setStrategyType(String strategyType) {
		this.strategyType = strategyType;
	}

	public static void main(String ...args) {
		
		NameScoringCalculator sf=new NameScoringCalculator();
		if(args.length<1) {
			System.out.println("Please Supply the Names file");
		}
		else if(args.length==1) {
			sf.setFile(args[0]);
			sf.setStrategyType("simple");
		}
		else if(args.length==2) {
			sf.setFile(args[0]);
			sf.setStrategyType(args[1]);
		}
		else {
			System.out.println("Invalid Input to the Program");
			System.out.println("Usage : java com.occ.name.scoring.utility.NameScoringCalculator <names.txt> <algorithm(optional)>");
		}
		
		System.out.println("File "+sf.getFile());
		System.out.println("Strategy "+sf.getStrategyType());
		
		Instant startTime = Instant.now();
		
		
		List<Name> names=buildNames(sf.getFile());
		NameScoringStrategy nss=NameScoringStrategyFactory.getNameScoringStrategy(sf.getStrategyType());
		long sum=sf.computeScore(names,nss);
		Instant endTime = Instant.now();
		printDuration("Simple",names.size(),sum,startTime, endTime);
		
		startTime = Instant.now();
		names=buildNames(sf.getFile());
		sum=sf.computeScoreInParallel(names,nss);
		endTime = Instant.now();
		printDuration("Parallel",names.size(),sum,startTime, endTime);
		
		
		startTime = Instant.now();
		names=buildNames(sf.getFile());
		sum=sf.computeScoreWithCompletableFuture(names,nss);
		endTime = Instant.now();
		printDuration("CompletableFuture",names.size(),sum,startTime, endTime);
		
	}
	public static List<Name> buildNames(String file){
		
		NamesBuilder nb=new NamesBuilder();
		return nb.loadAndBuildNames(file);
	}
	
    public long computeScoreWithCompletableFuture(List<Name> names,NameScoringStrategy nss) {
		
		final int NUM_OF_THREADS = 10;
		ExecutorService executor = Executors.newFixedThreadPool(Math.min(names.size(), NUM_OF_THREADS));
		
		List<CompletableFuture<Long>> futures= names.stream().map(name->computeScore(name,nss,executor)).collect(Collectors.toList());
		long total=futures.stream().mapToLong(CompletableFuture::join).sum();
		executor.shutdown();
		return total;
	
	}
	public static CompletableFuture<Long> computeScore(Name name,NameScoringStrategy nss,ExecutorService executor){
		return CompletableFuture.supplyAsync(()-> nss.computeScore(name),executor);
	}
	public long computeScoreInParallel(List<Name> names,NameScoringStrategy nss) {
		return names.parallelStream().mapToLong(name->computeScore(name,nss)).sum();
	}
	public long computeScore(List<Name> names,NameScoringStrategy nss) {
		return names.stream().mapToLong(name->computeScore(name,nss)).sum();
	}
	public static long computeScore(Name name,NameScoringStrategy nss){
		return nss.computeScore(name);
	}
	private static void printDuration(String type,int size,long sum,Instant start, Instant end) {
		Duration timeElapsed = Duration.between(start, end);
		System.out.printf("Type :"+type+", Processed %d names in %d MilliSeconds and the sum is %d\n", size, timeElapsed.toMillis(),sum);
	}
	

}
