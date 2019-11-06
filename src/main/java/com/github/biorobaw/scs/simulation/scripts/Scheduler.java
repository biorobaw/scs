package com.github.biorobaw.scs.simulation.scripts;

import java.util.HashMap;
import java.util.PriorityQueue;

public class Scheduler<T> {
	PriorityQueue<T> queue = new PriorityQueue<>( (a, b) ->  scriptComparator(a, b));
	HashMap<T,Long> schedules  = new HashMap<>();
	HashMap<T,Integer> priorities = new HashMap<>();
	HashMap<T,Long> insertionTimes = new HashMap<>();
	
	/**
	 * Compare the priority of 2 objects.
	 * An object has higher priority if its execution is scheduled sooner.
	 * If they are scheduled for the same time, then the priority is compared.
	 * If they have the same priority, then the object inserted first has higher priority.
	 * @param a	object A
	 * @param b object B
	 * @return -1 if A has higher priority, 
	 */
	private int scriptComparator(T a, T b) {
		var time_a = schedules.get(a);
		var time_b = schedules.get(b);
		if (time_a < time_b) return -1;
		if (time_b < time_a) return 1;
		
		var priority_a = priorities.get(a);
		var priority_b = priorities.get(b);
		if( priority_a < priority_b) return -1;
		if( priority_b < priority_a) return 1;
		
		var ins_a = insertionTimes.get(a);
		var ins_b = insertionTimes.get(b);
		if( ins_a < ins_b) return -1;
		if( ins_b < ins_a) return 1;

		return 0;
		
	}
	
	public void setPriority(T object, int value) {
		priorities.put(object, value);
	}
	
	public void squedule(T object, Long time) {
		schedules.put(object, time);
		insertionTimes.put(object, System.currentTimeMillis());
		queue.add(object);
		
	}
	
	public boolean isEmpty() {
		return queue.isEmpty();
	}
	
	public Long nextTime() {
		return schedules.get(queue.peek());
	}
	
	public T pop() {
		return queue.poll();
	}
	
	public void clear() {
		queue.clear();
		schedules.clear();
		priorities.clear();
	}
	
}
