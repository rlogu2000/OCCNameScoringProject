package com.occ.name.scoring.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.occ.name.scoring.entity.Name;
import com.occ.name.scoring.io.FileLoader;


public class NamesBuilder {

	public List<Name> loadAndBuildNames(String file) { //throws IOException, Exception {
		String line=null;
		List<String> names=null;
		FileLoader fileLoader = new FileLoader();
		try (BufferedReader br = fileLoader.loadFile(new File(file))) {
		
			while ((line = br.readLine()) != null) {
				String namesStr=line.replace("\"","");
				names = Arrays.asList(namesStr.split("\\s*,\\s*"));
			}
		} catch (IOException  e) {
			e.printStackTrace();
			//throw e;
		} catch (Exception e) {
			e.printStackTrace();
			//throw e;
		}
		return buildNamesList(names);
	}
	
	private List<Name> buildNamesList(List<String> names){
		
		final AtomicLong counter = new AtomicLong();
		return names.stream().map(fullName-> {
			String[] name=fullName.split(" ");
			if(name.length>1) {
				return new Name(name[0],name[1]);
			}
			else {
				return new Name(name[0],"");
			}
		}).sorted(Comparator.comparing(Name::getFirstName).thenComparing(Name::getLastName)).map(name->
		{
			name.setScore(counter.incrementAndGet());		
			return name;
		}).collect(Collectors.toList());
		}
	}	

