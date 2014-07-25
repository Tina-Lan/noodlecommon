package org.fengling.noodlecommon.distributedlock.api;

public interface LockChangeHandler {
	
	public void onMessageGetLock();
	public void onMessageLossLock();
	public void onMessageReleaseLock();
	public void onMessageStart();
	public void onMessageStop();
}