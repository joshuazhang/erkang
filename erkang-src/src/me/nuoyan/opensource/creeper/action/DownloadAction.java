package me.nuoyan.opensource.creeper.action;

public class DownloadAction extends Action {
	
	private String saveDir;
	/**
	 * 下载到哪个属性的文件夹下,这里只是对应的属性的名，而不是值
	 */
	private String folder;
	
	private String savePrefix;

	public String getSavePrefix() {
		return savePrefix;
	}

	public void setSavePrefix(String savePrefix) {
		this.savePrefix = savePrefix;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getSaveDir() {
		return saveDir;
	}

	public void setSaveDir(String saveDir) {
		this.saveDir = saveDir;
	}
	
}
