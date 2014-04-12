package cz.brno.greld.DiscussWithUs;

public class UserSettings {
	private int updateFrequencyIfActive; // in seconds
	private int updateFrequencyIfInactive; // in seconds

	public UserSettings(int updateFrequencyIfActive, int updateFrequencyIfInactive) {
		super();
		this.updateFrequencyIfActive = updateFrequencyIfActive;
		this.updateFrequencyIfInactive = updateFrequencyIfInactive;
	}

	
	
	public int getUpdateFrequencyIfActive() {
		return updateFrequencyIfActive;
	}

	public int getUpdateFrequencyIfInactive() {
		return updateFrequencyIfInactive;
	}

	public void setUpdateFrequencyIfInactive(int updateFrequencyIfInactive) {
		this.updateFrequencyIfInactive = updateFrequencyIfInactive;
	}

	public void setUpdateFrequencyIfActive(int updateFrequencyIfActive) {
		this.updateFrequencyIfActive = updateFrequencyIfActive;
	}
	
	

}
