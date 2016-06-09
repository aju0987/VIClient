package com.vmware.viclient.managedentities;

import com.vmware.viclient.helper.VimUtil;
import com.vmware.viclient.connectionmgr.ConnectionManager;

import java.util.HashMap;
import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.vim25.DatastoreCapability;
import com.vmware.vim25.DatastoreInfo;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.StorageIORMInfo;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.ManagedObjectReference;

public class DatastoreSystem {
	
	//private static DatastoreSystem dSystem = null;
	private ServiceContent inst = null;
	private String Esxhost = null;
	private VimUtil vimUtil = null;

	public DatastoreSystem() {
		ConnectionManager cmgr = ConnectionManager.getInstance();
		Esxhost = cmgr.getEsxHost();
		inst=cmgr.getServiceContent();
		vimUtil = cmgr.getVimUtil();
	}

	public ManagedObjectReference getDatastoreSystem(String serverName)  {
        try {
	  ManagedObjectReference rootFolder = inst.getRootFolder();
	  ManagedObjectReference dStore = vimUtil.getMORFromEntityName("Datastore", serverName);
	  return dStore;
    } catch (Exception e) {
              e.printStackTrace();
    }
    return null;
}

public HashMap getDatastoreInfo() {
	try {
		ManagedObjectReference dStore = getDatastoreSystem(Esxhost);
	    if (dStore == null) {
		System.err.println("DataStoreSystem: Datastore is null...");
		return null;
	    }
	    
	    DatastoreInfo dInfo = (DatastoreInfo)vimUtil.getProperty(dStore, "info");
	    HashMap hMap = new HashMap();
	    hMap.put("freeSpace",dInfo.getFreeSpace());
	    hMap.put("maxFileSize",dInfo.getMaxFileSize());
	    hMap.put("name",dInfo.getName());
	    hMap.put("timestamp",dInfo.getTimestamp());
	    hMap.put("url",dInfo.getUrl());
	   
	    return hMap;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public HashMap getStorageIORMInfo() {
	try {
		ManagedObjectReference dStore = getDatastoreSystem(Esxhost);
	    if (dStore == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    
	    StorageIORMInfo stIORMInfo = (StorageIORMInfo)vimUtil.getProperty(dStore, "iormConfigutation");
	    HashMap hMap = new HashMap();
	    hMap.put("congestionThreshold",stIORMInfo.getCongestionThreshold());
	    hMap.put("enabled",stIORMInfo.isEnabled());
	    hMap.put("statsAggregationDisabled",stIORMInfo.isStatsAggregationDisabled());
	    hMap.put("statsCollectionEnabled ",stIORMInfo.isStatsCollectionEnabled());
	    return hMap;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public HashMap getDatastoreSummary(){
	try {
		ManagedObjectReference dStore = getDatastoreSystem(Esxhost);
	    if (dStore == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    
	    DatastoreSummary dSummary = (DatastoreSummary)vimUtil.getProperty(dStore, "summary");
	    HashMap hMap = new HashMap();
	    hMap.put("accessible ",dSummary.isAccessible());
	    hMap.put("capacity",dSummary.getCapacity());
	    
	    hMap.put("datastore_type",dSummary.getDatastore().getType());
	    hMap.put("datastore_val",dSummary.getDatastore().getValue());
	    
	    
	    hMap.put("freeSpace",dSummary.getFreeSpace());
	    hMap.put("maintenanceMode",dSummary.getMaintenanceMode());
	    hMap.put("multipleHostAccess",dSummary.isMultipleHostAccess());
	    hMap.put("name",dSummary.getName());
	    hMap.put("type",dSummary.getType());
	    hMap.put("uncommitted",dSummary.getUncommitted());
	    hMap.put("url",dSummary.getUrl());
	   
	    return hMap;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;	
}

public HashMap getDatastoreCapability(){
	try {
		ManagedObjectReference dStore = getDatastoreSystem(Esxhost);
	    if (dStore == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    
	    DatastoreCapability dCapability = (DatastoreCapability)vimUtil.getProperty(dStore, "capability");
	    HashMap hMap = new HashMap();
	    hMap.put("directoryHierarchySupported",dCapability.isDirectoryHierarchySupported());
	    hMap.put("perFileThinProvisioningSupported",dCapability.isPerFileThinProvisioningSupported());
	    
	    hMap.put("rawDiskMappingsSupported",dCapability.isRawDiskMappingsSupported());
	    hMap.put("storageIORMSupported",dCapability.isStorageIORMSupported());
	   
	    return hMap;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;	
}

}
