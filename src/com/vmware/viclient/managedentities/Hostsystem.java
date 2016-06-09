package com.vmware.viclient.managedentities;

import java.util.HashMap;
import java.util.Vector;
import java.util.List;
import java.util.Map;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.helper.XMLCreate;
import com.vmware.viclient.helper.VimUtil;

import com.vmware.vim25.HostFirewallDefaultPolicy;
import com.vmware.vim25.HostFirewallInfo;
import com.vmware.vim25.HostFirewallRuleset;
import com.vmware.vim25.KernelModuleInfo;
import com.vmware.vim25.DatastoreSummary;
import com.vmware.vim25.HostHardwareStatusInfo;
import com.vmware.vim25.HostHardwareElementInfo;
import com.vmware.vim25.HostStorageElementInfo;
import com.vmware.vim25.HostStorageOperationalInfo;
import com.vmware.vim25.HostSystemHealthInfo;
import com.vmware.vim25.HostNumericSensorInfo;
import com.vmware.vim25.HostDiagnosticPartition;
import com.vmware.vim25.HostCapability;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ArrayOfManagedObjectReference;

public class Hostsystem {
	
	private ServiceContent inst = null;
	private String Esxhost = null;
	private long exeTime=0;
	XMLCreate xmlFile = null;
	VimUtil vimUtil = null;
	ConnectionManager cmgr = null;
	
	public Hostsystem(){
		cmgr = ConnectionManager.getInstance();
		Esxhost = cmgr.getEsxHost();
		inst=cmgr.getServiceContent();
		vimUtil = cmgr.getVimUtil();
		xmlFile = new XMLCreate("hostsystem.xml");
	}
	
	public String [] getAllManagedEntities() {

		final String [] entities = {"HostSystem", "Datacenter", "Datastore", "VirtualMachine", "Network", "ResourcePool", "ComputeResource","DistributedVirtualSwitch"};
		return entities;
	}

	public ManagedObjectReference [] getAllEntities() {
             try {
		  ManagedObjectReference rootFolder = inst.getRootFolder();
		  ManagedObjectReference [] entities = vimUtil.getAllManagedEntities();
		  return entities;
	     } catch (Exception e) {
                   e.printStackTrace();
	     }
	     return null;
	}
	
	public ManagedObjectReference[] getHostSystems() {
        try {
	  ManagedObjectReference rootFolder = inst.getRootFolder();
	  ManagedObjectReference [] hosts = vimUtil.getManagedEntities(VimUtil.HOSTSYSTEM);
	  return hosts;
    } catch (Exception e) {
              e.printStackTrace();
    }
    return null;
}

public ManagedObjectReference getHostSystem(String serverName)  {
        try {
	  ManagedObjectReference rootFolder = inst.getRootFolder();
	  ManagedObjectReference host = vimUtil.getMORFromEntityName(VimUtil.HOSTSYSTEM, serverName);
	  return host;
    } catch (Exception e) {
              e.printStackTrace();
    }
    return null;
}

public TableData getCapabilitiesInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    HostCapability cap = (HostCapability)vimUtil.getProperty(host, "capability");
	    TableData kdata = new TableData();
	    kdata.colNames.addElement("Feature");
	    kdata.colNames.addElement("Comments");

               Vector v = new Vector();
               v.addElement("Clone From Snapshot");
               v.addElement(cap.isCloneFromSnapshotSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Delta Disk Backings");
               v.addElement(cap.isDeltaDiskBackingsSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Firewall IpRules");
               v.addElement(cap.isFirewallIpRulesSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Ft supported");
               v.addElement(cap.isFtSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Ipmi supported");
               v.addElement(cap.isIpmiSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Login by SSL Thumprint");
               v.addElement(cap.isLoginBySSLThumbprintSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Maxhost Running Vms");
               v.addElement(cap.getMaxHostRunningVms());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Max Running Vms");
               v.addElement(cap.getMaxRunningVMs());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("MaxHost supported Vcpus");
               v.addElement(cap.getMaxHostSupportedVcpus());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Max supported Vcpus");
               v.addElement(cap.getMaxSupportedVcpus());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Max supported VMs");
               v.addElement(cap.getMaxSupportedVMs());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("PerVM Network traffice shaping");
               v.addElement(cap.isPerVMNetworkTrafficShapingSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Record Replay ");
               v.addElement(cap.isRecordReplaySupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Replay unsupported reason");
               v.addElement(cap.getReplayUnsupportedReason());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Service Package Info");
               v.addElement(cap.isServicePackageInfoSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Snapshot Relayout");
               v.addElement(cap.isSnapshotRelayoutSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Storage IORM");
               v.addElement(cap.isStorageIORMSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Storage VMotion");
               v.addElement(cap.isStorageVMotionSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Tpm supported");
               v.addElement(cap.isTpmSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Virtual Exec Usage supported");
               v.addElement(cap.isVirtualExecUsageSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("VM DirectPathGen2 supported");
               v.addElement(cap.isVmDirectPathGen2Supported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("VM DirectPathGen2UnsupportedReason Extended");
               v.addElement(cap.getVmDirectPathGen2UnsupportedReasonExtended());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Vmfs Datastore Mount capable");
               v.addElement(cap.isVmfsDatastoreMountCapable());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Vmotion with Storage VMotion supported");
               v.addElement(cap.isVmotionWithStorageVMotionSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("VStorage capable");
               v.addElement(cap.isVStorageCapable());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Background Snapshots supported");
               v.addElement(cap.isBackgroundSnapshotsSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("CPU-Memory resource configuration supported");
               v.addElement(cap.isCpuMemoryResourceConfigurationSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Datastore principal supported");
               v.addElement(cap.isDatastorePrincipalSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("HighGuestMem supported");
               v.addElement(cap.isHighGuestMemSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Iscsi supported");
               v.addElement(cap.isIscsiSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("LocalSwap Datastore supported");
               v.addElement(cap.isLocalSwapDatastoreSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Maintenance Mode supported");
               v.addElement(cap.isMaintenanceModeSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Nfs Supported");
               v.addElement(cap.isNfsSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Nic Teaming supported");
               v.addElement(cap.isNicTeamingSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("PerVmSwapFiles");
               v.addElement(cap.isPerVmSwapFiles());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("PreAssigned PCI Unit Numbers supported");
               v.addElement(cap.isPreAssignedPCIUnitNumbersSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Reboot supported");
               v.addElement(cap.isRebootSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Recursive Resource Pools supported");
               v.addElement(cap.isRecursiveResourcePoolsSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Restricted Snapshot Reolcate supported");
               v.addElement(cap.isRestrictedSnapshotRelocateSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("San supported");
               v.addElement(cap.isSanSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Scaled Screenshot supported");
               v.addElement(cap.isScaledScreenshotSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Screenshot supported");
               v.addElement(cap.isScreenshotSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Shutdown supported");
               v.addElement(cap.isShutdownSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Standby supported");
               v.addElement(cap.isStandbySupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Suspended Relocate supported");
               v.addElement(cap.isSuspendedRelocateSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Unshare Swap VMotion supported");
               v.addElement(cap.isUnsharedSwapVMotionSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("VlanTagging supported");
               v.addElement(cap.isVlanTaggingSupported());
               kdata.rowData.addElement(v);

               v = new Vector();
               v.addElement("Vmotion supported");
               v.addElement(cap.isVmotionSupported());
               kdata.rowData.addElement(v);

	    return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public TableData getHostDiagnosticSystemInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    ManagedObjectReference hDiag = (ManagedObjectReference)vimUtil.getProperty(host, "configManager.diagnosticSystem");
	    List<HostDiagnosticPartition> partitions = cmgr.getVimPort().queryAvailablePartition(hDiag);
	    TableData kdata = new TableData();
	    kdata.colNames.addElement("Id");
	    kdata.colNames.addElement("Storage Type");
	    kdata.colNames.addElement("Slots");
	    kdata.colNames.addElement("Diagnostic Type");

	    if (partitions != null) {
	        for (int i=0; i<partitions.size(); i++) {
                        Vector v = new Vector();
		     v.addElement(partitions.get(i).getId().getDiskName() + " : " + partitions.get(i).getId().getPartition());
		     v.addElement(partitions.get(i).getStorageType());
		     v.addElement(partitions.get(i).getSlots());
		     v.addElement(partitions.get(i).getDiagnosticType());
                        kdata.rowData.addElement(v);
		}
	    }
               return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;

}

public TableData getSystemHealthInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    HostSystemHealthInfo hInfo = null;
	    try {

		    hInfo = (HostSystemHealthInfo)vimUtil.getProperty(host, "runtime.healthSystemRuntime.systemHealthInfo");
	            //hInfo = host.getHealthStatusSystem().getRuntime().getSystemHealthInfo();
	    } catch (Exception ex) {
		    return new TableData();
	    }
	    List<HostNumericSensorInfo> sInfo = hInfo.getNumericSensorInfo();
	    TableData kdata = new TableData();
	    kdata.colNames.addElement("Name");
	    kdata.colNames.addElement("Base Units");
	    kdata.colNames.addElement("Current Reading");
	    kdata.colNames.addElement("Rate Units");
	    kdata.colNames.addElement("Sensor Type");
	    kdata.colNames.addElement("Unit Modifier");
	    kdata.colNames.addElement("Health State");

	    if (sInfo != null) {
	        for (int i=0; i<sInfo.size(); i++) {
                        Vector v = new Vector();
		     v.addElement(sInfo.get(i).getName());
		     v.addElement(sInfo.get(i).getBaseUnits());
		     v.addElement(sInfo.get(i).getCurrentReading());
		     v.addElement(sInfo.get(i).getRateUnits());
		     v.addElement(sInfo.get(i).getSensorType());
		     v.addElement(sInfo.get(i).getUnitModifier());
		     v.addElement(sInfo.get(i).getHealthState().getKey());
                        kdata.rowData.addElement(v);
		}
	    }
	    return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public TableData getHealthStatusInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    HostHardwareStatusInfo hInfo = null;

	    try {
		    hInfo = (HostHardwareStatusInfo)vimUtil.getProperty(host, "runtime.healthSystemRuntime.hardwareStatusInfo");
	            //hInfo = host.getHealthStatusSystem().getRuntime().getHardwareStatusInfo();
	    } catch (Exception ex) {
		    return new TableData();
	    }

	    List<HostHardwareElementInfo>  hCpu = hInfo.getCpuStatusInfo();
	    List<HostHardwareElementInfo> hMemory = hInfo.getMemoryStatusInfo();
	    List<HostStorageElementInfo> hStorage = hInfo.getStorageStatusInfo();

	    TableData kdata = new TableData();
	    kdata.colNames.addElement("CPU");
	    kdata.colNames.addElement("CPU Status");
	    kdata.colNames.addElement("Memory");
	    kdata.colNames.addElement("Memory Status");
	    kdata.colNames.addElement("Storage");
	    kdata.colNames.addElement("Storage Value");
	    if (hCpu != null) {
	        for (int i=0; i<hCpu.size(); i++) {
                        Vector v = new Vector();
		     v.addElement(hCpu.get(i).getName());
		     v.addElement(hCpu.get(i).getStatus().getKey());
                        kdata.rowData.addElement(v);
		}
	    }
	    int cursize = kdata.rowData.size();

	    if (hMemory != null) {
	        for (int i=0; i<hMemory.size(); i++) {
		     Vector v = null;
		     if (i < cursize) {
                            v = (Vector)kdata.rowData.get(i);
		     } else {
			 v = new Vector();
			 v.addElement("");
			 v.addElement("");
		     }
		     v.addElement(hMemory.get(i).getName());
		     v.addElement(hMemory.get(i).getStatus().getKey());
		     if (i >= cursize) {
                            kdata.rowData.addElement(v);
		     }
		}
	    }

	    cursize = kdata.rowData.size();

	    if (hStorage!= null) {
	        for (int i=0; i<hStorage.size(); i++) {
		    List<HostStorageOperationalInfo> sInfo = hStorage.get(i).getOperationalInfo();
		    for (int j=0; j<sInfo.size(); j++) {
		         Vector v = null;
		         if (i < cursize) {
                                v = (Vector)kdata.rowData.get(i);
		         } else {
			     v = new Vector();
			     v.addElement("");
			     v.addElement("");
			     v.addElement("");
			     v.addElement("");
		         }
		         v.addElement(sInfo.get(i).getProperty());
		         v.addElement(sInfo.get(i).getValue());
		         if (i >= cursize) {
                                kdata.rowData.addElement(v);
		         }
		    }
		}
	    }

	    return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}


public TableData getDatastoreInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Host is null...");
		return null;
	    }
	    Map<String, Object> map = vimUtil.getProperties(host, new String[]{"datastore"});
            List<ManagedObjectReference>  dstores = ((ArrayOfManagedObjectReference)map.get("datastore")).getManagedObjectReference();
	    TableData kdata = new TableData();
	    kdata.colNames.addElement("Name");
	    kdata.colNames.addElement("Type");
	    kdata.colNames.addElement("Url");
	    kdata.colNames.addElement("Capacity");
	    kdata.colNames.addElement("Free Space");
	    kdata.colNames.addElement("Maintenance Mode");
	    kdata.colNames.addElement("Multiple Host Access");
	    kdata.colNames.addElement("Accessible");
	    kdata.colNames.addElement("Uncommitted");
	    String prop = "summary";
	    for (int i=0; i<dstores.size(); i++) {
		 Vector v = new Vector();
                    DatastoreSummary summary = (DatastoreSummary)vimUtil.getProperty(dstores.get(i), prop);
		 v.addElement(summary.getName());
		 v.addElement(summary.getType());
		 v.addElement(summary.getUrl());
		 v.addElement(summary.getCapacity());
		 v.addElement(summary.getFreeSpace());
		 v.addElement(summary.getMaintenanceMode());
		 v.addElement(summary.isMultipleHostAccess());
		 v.addElement(String.valueOf(summary.isAccessible()));
		 v.addElement(String.valueOf(summary.getUncommitted()));
                    kdata.rowData.addElement(v);
	    }
	    return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public TableData getKernelModuleInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Could not get hostsystem info...");
		return null;
	    }
	    ManagedObjectReference kernelSystem = (ManagedObjectReference)vimUtil.getProperty(host, "configManager.kernelModuleSystem");
            List<KernelModuleInfo> kModules = cmgr.getVimPort().queryModules(kernelSystem);
	    TableData kdata = new TableData();
	    kdata.colNames.addElement("Id");
	    kdata.colNames.addElement("Filename");
	    kdata.colNames.addElement("Name");
	    kdata.colNames.addElement("Loaded");
	    kdata.colNames.addElement("Enabled");
	    kdata.colNames.addElement("OptionString");
	    kdata.colNames.addElement("Version");
	    kdata.colNames.addElement("Usecount");
	    for (int i=0; i<kModules.size(); i++) {
		 Vector v = new Vector();
		 v.addElement(String.valueOf(kModules.get(i).getId()));
		 v.addElement(kModules.get(i).getFilename());
		 v.addElement(kModules.get(i).getName());
		 v.addElement(String.valueOf(kModules.get(i).isLoaded()));
		 v.addElement(String.valueOf(kModules.get(i).isEnabled()));
		 v.addElement(kModules.get(i).getOptionString());
		 v.addElement(kModules.get(i).getVersion());
		 v.addElement(String.valueOf(kModules.get(i).getUseCount()));
		 kdata.rowData.addElement(v);
	    }
	    
	    //String str = getMethodName("KernelInfo");
	    //System.out.println("value:\n"+str);
	    
	    return kdata;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}

public HashMap getFirewallInfo() {
	try {
	    ManagedObjectReference host = getHostSystem(Esxhost);
	    if (host == null) {
		System.err.println("Could not get hostsystem info...");
		return null;
	    }
	    long startTime = System.currentTimeMillis();
	    //HostFirewallInfo hFirewall = host.getConfig().getFirewall();
	    HostFirewallInfo hFirewall = (HostFirewallInfo)vimUtil.getProperty(host, "configManager.firewallSystem.firewallInfo");
	    HostFirewallDefaultPolicy policy = hFirewall.getDefaultPolicy();
	    long endTime = System.currentTimeMillis();
	    exeTime = endTime-startTime;
	    HashMap map = new HashMap();
	    map.put("incoming blocked", policy.isIncomingBlocked());
	    map.put("outgoing blocked", policy.isOutgoingBlocked());
	    List<HostFirewallRuleset> ruleset = hFirewall.getRuleset();
	    Vector rulevector = new Vector();
	    if (ruleset != null && ! ruleset.isEmpty()) {
		    for (int i=0; i<ruleset.size(); i++) {
		         rulevector.addElement(ruleset.get(i).getKey() + " : " + ruleset.get(i).getLabel() + " : " + ruleset.get(i).getService());
		    }
		    map.put("ruleset", rulevector);
	    }
	    return map;
	} catch (Exception e) {
		e.printStackTrace();
	}
	return null;
}


public String getMethodName(String strName){
	
	StringBuilder strMthNames = new StringBuilder();

	
	strMthNames.append("com.vmware.vim25.mo.ManagedObject\n\tcom.vmware.vim25.mo.ExtensibleManagedObject\n\t\tcom.vmware.vim25.mo.ManagedObjectReference\n\t\t\t");
	
	
	strMthNames.append("com.vmware.vim25.mo.HostSystem :");
	
	if(strName.equalsIgnoreCase("KernelInfo")){
		strMthNames.append("getKernelModuleInfo()");
		
	}
	else if(strName.equalsIgnoreCase("Datastore")){
		strMthNames.append("getDatastoreInfo()");
		
	}
	else if(strName.equalsIgnoreCase("Hardware Status")){
		strMthNames.append("getHealthStatusInfo()");
		
	}
	else if(strName.equalsIgnoreCase("System Health")){
		strMthNames.append("getSystemHealthInfo()");
		
	}
	else if(strName.equalsIgnoreCase("Diagnostic")){
		strMthNames.append("getHostDiagnosticSystemInfo()");
		
	}
	else if(strName.equalsIgnoreCase("Capabilities")){
		strMthNames.append("getCapabilitiesInfo()");
	}
	else if(strName.equalsIgnoreCase("Firewall")){
		strMthNames.append("getFirewallInfo()");
		
	}
	return strMthNames.toString();
}

}
