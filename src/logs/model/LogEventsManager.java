package logs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * This classes contains utilities to open and process logFiles. It stores
 * important and frequently reused informations about the logs. It maintains two
 * representations : a hashmaps by logEvent Types and an ArrayList of events
 * sorted in time
 *
 * @author jeremiegarcia
 *
 * with the participation of marie, clement and charlelie
 */
public abstract class LogEventsManager {

	private long beginTime = 0;
	private long endTime = 1000;

	private HashMap<String, ArrayList<LogEvent>> eventsMap;
	private ArrayList<LogEvent> eventsList;

	private File logFile;
	
	private static HashMap<String,ArrayList<LogEvent>> selectedList=new HashMap<String, ArrayList<LogEvent>>();

	private static String pathFileSauvegarde = "./sauvegarde/sauvegarde.txt";
	
	public static HashMap<String,ArrayList<LogEvent>> getSelectedList(){
		return selectedList;
	}
	
	/**
	 * Process a logFile and extract the data
	 */
	public void setLogFile(File f) {
		if (this.logFile != null && this.logFile.getPath() != f.getPath()) {
			this.reset();
		}
		this.logFile = f;

		if (this.logFile.exists()) {
			this.eventsList = this.extractEventsAsList(this.logFile);
			this.eventsMap = this.createMapFromList(this.eventsList);
			this.updateTimes(this.eventsList);
		}
	}

	private void updateTimes(ArrayList<LogEvent> eventsList2) {
		this.beginTime = eventsList2.get(0).getTimeStamp();
		this.endTime = eventsList2.get(eventsList2.size() - 1).getTimeStamp();
	}

	private HashMap<String, ArrayList<LogEvent>> createMapFromList(ArrayList<LogEvent> eventsList2) {
		HashMap<String, ArrayList<LogEvent>> map = new HashMap<String, ArrayList<LogEvent>>();
		for (LogEvent evt : eventsList2) {
			if (map.containsKey(evt.getLabel())) {
				map.get(evt.getLabel()).add(evt);
			} else {
				ArrayList<LogEvent> list = new ArrayList<LogEvent>();
				list.add(evt);
				map.put(evt.getLabel(), list);
			}
		}
		return map;
	}

	/**
	 * to be implemented by subclasses
	 *
	 * @return
	 */
	protected abstract ArrayList<LogEvent> extractEventsAsList(File logFile);

	private void reset() {
		this.eventsList.clear();
		this.eventsMap.clear();
	}

	public long getBeginTime() {
		return beginTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public long getDuration() {
		return endTime - beginTime;
	}

	public HashMap<String, ArrayList<LogEvent>> getLogevents() {
		return eventsMap;
	}

	public ArrayList<LogEvent> getTimeSortedLogEventsAsArrayList() {
		return eventsList;
	}

	//Fait par le projetSITA
	/**
	 * This method searches patterns in the selected lines and creates a line with the aggregates formed
	 * from the patterns.
	 * 
	 * It returns a map with the list of aggregated events (key = "eventList")
	 * and the lines used for the research (key = keyList).
	 * 
	 * @return Map<String, ArrayList> map
	 */
	public Map<String, ArrayList> recherchePattern(){
		ArrayList isFusion = new ArrayList();
		isFusion.add(true);
		if (selectedList.isEmpty()){
			System.out.println("La liste est vide");
			return null;
		}
		else{
			ArrayList<String> order=new ArrayList<String>();
			ArrayList<LogEvent> newLigne=new ArrayList<LogEvent>();
			ArrayList<LogEvent> newLigneAggregated = new ArrayList<LogEvent>();
			ArrayList<LogEvent> intermediaire=new ArrayList<LogEvent>();
			
			for (Entry<java.lang.String, java.util.ArrayList<LogEvent>> entry : selectedList.entrySet())
			{
			   ArrayList<LogEvent> evt=entry.getValue();
			   intermediaire.addAll(evt);
			}
			Collections.sort(intermediaire);
			
			for(LogEvent evt:intermediaire){
				order.add(evt.getLabel());
			}
			
			//recherche de pattern
			int a=0;
			for (LogEvent evt:eventsList){
				String key=order.get(a);
				if(evt.getLabel().equals(key)){
					System.out.println("Trouvé un element "+String.valueOf(a));
					newLigne.add(evt);
					a++;
					if(a==order.size()){a=0;}
				}
				else{
					if(order.contains(evt.getLabel())){
						isFusion.set(0, false);
						//Les lignes ne sont pas fusionnables
						if(evt.getLabel().equals(order.get(0))){
							for(int cst=0;cst<a;cst++){
								newLigne.remove(newLigne.size()-cst-1);
							}
							newLigne.add(evt);
							a=1;
						}
						else{	
							for(int i=0;i<a;i++){
								newLigne.remove(newLigne.size()-1);
							}
							a=0;
						}
				}
			}
			}
			System.out.println("Nouvelle ligne : " +newLigne);
			
			//On enlève les événements d'un pattern non terminé à la fin de la ligne
			for(int i=0;i<a;i++){
				newLigne.remove(newLigne.size()-1);
			}
			
			//Créer les aggrégés correspondant
			for (int j=0;j<newLigne.size()/order.size();j++){
				for(int i=0;i<order.size()-1;i++){
					if(i==0){
						LogEvent nouveau=LogEventsAggregator.aggregateLogEvents(newLigne.get(j*order.size()),newLigne.get(j*order.size()+1));
						newLigneAggregated.add(nouveau);
						eventsList.add(nouveau);
					}
					else{
						LogEvent nouveau=LogEventsAggregator.aggregateLogEvents(newLigneAggregated.get(newLigneAggregated.size()-1),newLigne.get(j*order.size()+i+1));
						newLigneAggregated.add(nouveau);
						newLigneAggregated.remove(newLigneAggregated.size()-2);
						eventsList.add(nouveau);
						eventsList.remove(eventsList.size()-2);
						if(i==order.size()-2){
							try{
								eventsMap.get(nouveau.getLabel()).add(nouveau);
							}
							catch(Exception e){
								eventsMap.put(nouveau.getLabel(), new ArrayList<LogEvent>());
								eventsMap.get(nouveau.getLabel()).add(nouveau);
							}
						}
					}
				}
			}
			Collections.sort(eventsList);
			
			Map <String,ArrayList> map=new HashMap();
			map.put("keyList",order);
			map.put("eventList", newLigneAggregated);
			System.out.println(order);
			System.out.println(newLigneAggregated);
			
			map.put("fusion", isFusion);
			
			//Sauvegarder dans un fichier txt les recherches effectuées
//			try{
//				FileWriter fw=new FileWriter(pathFileSauvegarde,true);
//				BufferedWriter out = new BufferedWriter(fw);
//				out.write("Sauvegarde: [");
//				int b=0;
//				for(String str:order){
//					if(b==order.size()-1){
//						out.write(str);
//					}
//					else{
//						out.write(str+",");
//						b++;
//					}
//				}
//				out.write("]\n");
//				out.close();
//			}
//			catch(IOException e){
//				System.out.println("Impossible d'ouvrir ou de créer le fichier");
//			}
			return map;
			}
		}

	public static boolean equalSelectedList(HashMap<String, ArrayList<LogEvent>> oldSelectedList){
		if(oldSelectedList.size()==selectedList.size()){
			for (Entry<java.lang.String, java.util.ArrayList<LogEvent>> entry : selectedList.entrySet())
			{
				String key= entry.getKey();
				ArrayList<LogEvent> listEvent=entry.getValue();
				if (oldSelectedList.containsKey(key)){
					if(listEvent.size()!=oldSelectedList.get(key).size()){
						return false;
					}
					else{
						for(LogEvent evt:listEvent){
							if (!oldSelectedList.get(key).contains(evt)){
								return false;
							}
						}
					}
				}
				else{
					return false;
				}
			}
			return true;
		}
		else{
			return false;
		}
	}
	
	public static HashMap<String, ArrayList<LogEvent>> copieSelectedList(){
		HashMap<String, ArrayList<LogEvent>> temp=new HashMap<String, ArrayList<LogEvent>>();
		for (Entry<java.lang.String, java.util.ArrayList<LogEvent>> entry : selectedList.entrySet())
		{
			String key= entry.getKey();
			ArrayList<LogEvent> listEvent=entry.getValue();
			temp.put(new String(key), new ArrayList<LogEvent>(listEvent));
		}
		return temp;
	}
	}
