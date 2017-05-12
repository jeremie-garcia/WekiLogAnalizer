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
 */
public abstract class LogEventsManager {

	private long beginTime = 0;
	private long endTime = 1000;

	private HashMap<String, ArrayList<LogEvent>> eventsMap;
	private ArrayList<LogEvent> eventsList;

	private File logFile;
	
	private static HashMap<String,ArrayList<LogEvent>> selectedList=new HashMap<String, ArrayList<LogEvent>>();

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

	public Map<String, ArrayList> recherchePattern(){
		/*Renvoie une Map avec la nouvelle ligne composée de tous les LogEvent sous la clé "eventList" et le nom des lignes
		 * qui la compose sous la clé "keyList"
		*/
		System.out.println("Ca passe");
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
			Collections.sort(intermediaire);;
			
			for(LogEvent evt:intermediaire){
				order.add((String) evt.getLabel());
			}
			
			//recherche de pattern
			int a=0;
			for (LogEvent evt:eventsList){
				String key=order.get(a);
				if(evt.getLabel().equals(key)){
					newLigne.add(evt);
					a++;
					if(a==order.size()){a=0;}
				}
				else{
					if(order.contains(evt.getLabel())){
						if(a==1 && evt.getLabel().equals(order.get(0))){
							newLigne.remove(newLigne.size()-1);
							newLigne.add(evt);
							a=1;
						}
						else{	
							System.out.println(a);
							for(int i=0;i<a;i++){
								newLigne.remove(newLigne.size()-1);
							}
							a=0;
						}
				}
			}
			}
			
			//On enlève les événements d'un pattern non terminé à la fin de la ligne
			for(int i=0;i<a;i++){
				newLigne.remove(newLigne.size()-1);
			}
			
			//Créer les aggrégés correspondant
			for (int j=0;j<newLigne.size()/order.size();j++){
				for(int i=0;i<order.size()-1;i++){
					if(i==0){
						newLigneAggregated.add(LogEventsAggregator.aggregateLogEvents(newLigne.get(j*order.size()),newLigne.get(j*order.size()+1)));
					}
					else{
						newLigneAggregated.add(LogEventsAggregator.aggregateLogEvents(newLigneAggregated.get(newLigneAggregated.size()-1),newLigne.get(j*order.size()+i+1)));
						newLigneAggregated.remove(newLigneAggregated.size()-2);
					}
				}
			}
			
			Map <String,ArrayList> map=new HashMap();
			map.put("keyList",order);
			map.put("eventList", newLigneAggregated);
			System.out.println(map);
			return map;
			}
		}

	}
