
public class Score implements Comparable<Score> {
	private String userName;
	private int score;
	
	public Score(String userName, int score) {
		this.userName = userName;
		this.score = score;
	}
	
	public String getName() {
		return userName;
	}
	
	public int getScore() {
		return score;
	}

	@Override
	public int compareTo(Score s) {
		return Integer.compare(s.score, score);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + score;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Score other = (Score) obj;
		if (score != other.score) {
			return false;
		}
		return true;
	}
	
}
