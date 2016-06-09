package com.vmware.viclient.managedentities;

import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import com.vmware.viclient.connectionmgr.ConnectionManager;
import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.helper.VimUtil;

import com.vmware.vim25.DatastoreInfo;
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
import com.vmware.vim25.VirtualMachineConfigInfo;

import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.VirtualDevice;
import com.vmware.vim25.Description;
import com.vmware.vim25.VirtualDeviceConnectInfo;
import com.vmware.vim25.VirtualDeviceBackingInfo;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.ArrayOfManagedObjectReference;
import com.vmware.vim25.ArrayOfVirtualDevice;

public class VirtualMachineSystem {
	
	private static VirtualMachineSystem vSystem = null;
	private ServiceContent inst = null;
	private VimUtil vimUtil = null;
	ConnectionManager cmgr = ConnectionManager.getInstance();

	private VirtualMachineSystem() {
		inst=cmgr.getServiceContent();
		vimUtil = cmgr.getVimUtil();
	}

	public static VirtualMachineSystem getInstance() {
                if (vSystem == null) {
                    vSystem = new VirtualMachineSystem();
		}
		return vSystem;
	}

	public TableData getVirtualDeviceInfo(ManagedObjectReference vm) {

		try{
		     TableData kdata = new TableData();
		     List<VirtualDevice> vDevices = ((ArrayOfVirtualDevice)(vimUtil.getProperty(vm, "config.hardware.device"))).getVirtualDevice();

		     kdata.colNames.addElement("Device");
		     kdata.colNames.addElement("Summary");
		     kdata.colNames.addElement("Key");
		     kdata.colNames.addElement("Unit number");
		     kdata.colNames.addElement("Controller Key");
		     kdata.colNames.addElement("Status");
		     kdata.colNames.addElement("Connected");
		     kdata.colNames.addElement("Start Connected");
		     kdata.colNames.addElement("Allow Guest Control");
		     kdata.colNames.addElement("Type");
		     kdata.colNames.addElement("Property");

		     if (vDevices != null && !vDevices.isEmpty()) {
			     for (VirtualDevice vDevice : vDevices) {
                                  Vector v = new Vector();
				  Description desc = vDevice.getDeviceInfo();
				  VirtualDeviceConnectInfo dInfo = vDevice.getConnectable();
				  v.addElement(desc.getLabel());
				  v.addElement(desc.getSummary());
				  v.addElement(vDevice.getKey());
				  v.addElement(vDevice.getUnitNumber());
				  v.addElement(vDevice.getControllerKey());
				  if (dInfo != null) {
				      v.addElement(dInfo.getStatus());
				      v.addElement(dInfo.isConnected());
				      v.addElement(dInfo.isStartConnected());
				      v.addElement(dInfo.isAllowGuestControl());
				  } else {
				      v.addElement("");
				      v.addElement("");
				      v.addElement("");
				      v.addElement("");
				  }
				  VirtualDeviceBackingInfo bInfo = vDevice.getBacking();
				  if (bInfo != null) {
                                      v.addElement(bInfo.getDynamicType());
				      List<DynamicProperty> props = bInfo.getDynamicProperty();
				      if (props != null && !props.isEmpty()) {
					  String kv = "";
					  for (DynamicProperty prop : props) {
					       kv = kv + prop.getName() + " - " + prop.getVal() + "; ";
					  }
					  v.addElement(kv);
				      } else {
				         v.addElement("");
				      }
				  } else {
				      v.addElement("");
				      v.addElement("");
				  }
				  kdata.rowData.addElement(v);

			     }
		     }
		     return kdata;
		} catch (Exception e) {
                     e.printStackTrace();
		}
		return null;
	}
	
	public HashMap getVirtualMachineConfigInfo(ManagedObjectReference vm) {
		try {
			 VirtualMachineConfigInfo vmConfig = (VirtualMachineConfigInfo)vimUtil.getProperty(vm, "info");
		    if (vmConfig == null) {
		    	System.err.println("VM is null...");
		    	return null;
		    }
		    
		    HashMap hMap = new HashMap();
		    hMap.put("alternateGuestName ",vmConfig.getAlternateGuestName());
		    hMap.put("annotation",vmConfig.getAnnotation());
		    
		    //need to see how to display this as it returns's int[]
		    hMap.put("cpuAffinityInfo",vmConfig.getCpuAffinity().getAffinitySet());
		    hMap.put("cpuAllocation_limit ",vmConfig.getCpuAllocation().getLimit());
		    hMap.put("cpuAllocation_reservation ",vmConfig.getCpuAllocation().getReservation());
		    hMap.put("cpuAllocation_shares",vmConfig.getCpuAllocation().getShares().getShares());
		    //hMap.put("cpuVendorInfo ",vmConfig.getCpuFeatureMask()[0].getVendor()); //commentedK
		    hMap.put("vmPathName ",vmConfig.getFiles().getVmPathName());
		    hMap.put("snapshotDirectory ",vmConfig.getFiles().getSnapshotDirectory());
		    hMap.put("guestFullName",vmConfig.getGuestFullName());
		    
		    hMap.put("memoryAllocation_limit ",vmConfig.getMemoryAllocation().getLimit());
		    hMap.put("memoryAllocation_reservation ",vmConfig.getMemoryAllocation().getReservation());
		    hMap.put("memoryAllocation_shares",vmConfig.getMemoryAllocation().getShares().getShares());
		    
		    hMap.put("cpuHotAddEnabled",vmConfig.isCpuHotAddEnabled());
		    hMap.put("uuid",vmConfig.getUuid());
		     
		    return hMap;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public HashMap getDatastoreInfo(ManagedObjectReference vm) {
		try {
			List<ManagedObjectReference> dStore = ((ArrayOfManagedObjectReference)(vimUtil.getProperty(vm, "datastore"))).getManagedObjectReference();
                        if (dStore == null || dStore.isEmpty()) {
		            System.err.println("VirtualMachineSystem: Datastore is null...");
                            return null;
                        }
			DatastoreInfo dInfo = (DatastoreInfo)vimUtil.getProperty(dStore.get(0), "info");
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
	
	

}
