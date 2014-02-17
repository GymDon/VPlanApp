package de.gymdon.app.api;

public class MensaClient implements Comparable<MensaClient> {

	public long timestamp;
	public String menu1 = "";
	public String menu2 = "";

	public MensaClient(long timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(MensaClient o) {
		return (int) (timestamp - o.timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((menu1 == null) ? 0 : menu1.hashCode());
		result = prime * result + ((menu2 == null) ? 0 : menu2.hashCode());
		result = prime * result + (int) (timestamp ^ (timestamp >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MensaClient other = (MensaClient) obj;
		if (menu1 == null) {
			if (other.menu1 != null)
				return false;
		} else if (!menu1.equals(other.menu1))
			return false;
		if (menu2 == null) {
			if (other.menu2 != null)
				return false;
		} else if (!menu2.equals(other.menu2))
			return false;
		if (timestamp != other.timestamp)
			return false;
		return true;
	}

}
