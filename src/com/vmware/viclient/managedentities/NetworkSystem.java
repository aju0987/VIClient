package com.vmware.viclient.managedentities;

import java.util.HashMap;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.helper.VimUtil;

import com.vmware.vim25.NetworkSummary;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ServiceContent;

	public class NetworkSystem {
		
		private ServiceContent inst = null;
		private String Esxhost = null;
		private VimUtil vimUtil = null;
	
		public NetworkSystem() {
			ConnectionManager cmgr = ConnectionManager.getInstance();
			Esxhost = cmgr.getEsxHost();
			inst=cmgr.getServiceContent();
			vimUtil = cmgr.getVimUtil();
		}
	
		public ManagedObjectReference getNetworkSystem(String serverName)  {
	        try {
		  ManagedObjectReference rootFolder = inst.getRootFolder();
		  System.err.println("servername: " + serverName + " " + rootFolder);
		  ManagedObjectReference netSystem = vimUtil.getMORFromEntityName("Network", serverName);
		  return netSystem;
	    } catch (Exception e) {
	              e.printStackTrace();
	    }
	    return null;
	}

	public HashMap getSummaryInfo() {
		try {
                    ManagedObjectReference nref = getNetworkSystem(Esxhost);
		    if (nref == null) {
			System.err.println("Network is null...");
			return null;
		    }
		    
		    NetworkSummary nSummary = (NetworkSummary)vimUtil.getProperty(nref, "summary");
		    HashMap hMap = new HashMap();
		    hMap.put("name",nSummary.getName());
		    hMap.put("accessible",nSummary.isAccessible());
		    hMap.put("ipPoolName",nSummary.getIpPoolName());
		    return hMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}



}
