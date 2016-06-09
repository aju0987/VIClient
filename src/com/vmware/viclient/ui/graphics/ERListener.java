package com.vmware.viclient.ui.graphics;

public interface ERListener {

	public void stateChanged(EREvent e);
}

class EREvent {
	enum Event {
             VM_ADDED, VM_REMOVED, HOST_ADDED, HOST_REMOVED;
	}
}
