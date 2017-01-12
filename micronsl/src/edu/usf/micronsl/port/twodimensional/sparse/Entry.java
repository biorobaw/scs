package edu.usf.micronsl.port.twodimensional.sparse;

public class Entry implements Comparable<Entry> {

	public int i;
	public int j;

	public Entry(int i, int j)
	{
		this.i = i;
		this.j = j;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Entry))
			return false;
		
		Entry e = (Entry) obj;
		
		return e.i == i && e.j == j;
	}

	@Override
	public int compareTo(Entry o) {
		if (o.i > i)
			return 1;
		else if (o.i < i)
			return -1;
		else
			if (o.j > j)
				return 1;
			else if (o.j < j)
				return -1;
			else 
				return 0;
	}

	@Override
	public int hashCode() {
		return 31 * i + j;
	}
	
	
	
}
