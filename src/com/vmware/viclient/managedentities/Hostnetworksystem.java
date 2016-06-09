package com.vmware.viclient.managedentities;

import com.vmware.viclient.helper.VimUtil;
import com.vmware.viclient.connectionmgr.ConnectionManager;

import com.vmware.vim25.HostDnsConfig;
import com.vmware.vim25.HostIpRouteConfig;
import com.vmware.vim25.HostNetCapabilities;
import com.vmware.vim25.HostNetworkConfig;
import com.vmware.vim25.ServiceContent;
import com.vmware.vim25.ManagedObjectReference;

public class Hostnetworksystem {

	private ServiceContent inst = null;
	private String Esxhost = null;
	private VimUtil vimUtil = null;

	public Hostnetworksystem() {
		ConnectionManager cmgr = ConnectionManager.getInstance();
		Esxhost = cmgr.getEsxHost();
		inst=cmgr.getServiceContent();
		vimUtil = cmgr.getVimUtil();
	}

	public ManagedObjectReference getNetworkSystem(String serverName)  {
		try {
			  ManagedObjectReference rootFolder = inst.getRootFolder();
			  ManagedObjectReference host = vimUtil.getMORFromEntityName(VimUtil.HOSTSYSTEM, serverName);
			    if(host==null)
			    {
			      System.out.println("Host not found");
			      
			      return null;
			    }
			    ManagedObjectReference hns = (ManagedObjectReference)vimUtil.getProperty(host, "configManager.networkSystem");
			  return hns;
		} catch (Exception e) {
              e.printStackTrace();
		}
		return null;
	}
	
	public void printHostNetworkSystem(){
		
		ManagedObjectReference hns = getNetworkSystem(Esxhost);
		
		System.out.println("\nHost net capabilities");
		HostNetCapabilities capabilities = (HostNetCapabilities)vimUtil.getProperty(hns, "capabilities");
		printHostNetCapabilities(capabilities);
		
	    System.out.println("\nHostIpRouteConfig");
		HostIpRouteConfig ipConfig= (HostIpRouteConfig)vimUtil.getProperty(hns, "consoleIpRouteConfig");
	    printHostIpRouteConfig(ipConfig);
	    
	    System.out.println("\nHost Dns Config");
	    HostDnsConfig dnsConfig = (HostDnsConfig)vimUtil.getProperty(hns, "dnsConfig");
	    printHostDnsConfig(dnsConfig);
	    
	    System.out.println("\nHost Network Config");
	    HostNetworkConfig networkConfig = (HostNetworkConfig)vimUtil.getProperty(hns, "networkConfig");
	    printHostNetworkConfig(networkConfig);
	    
	}
	
	public void printHostNetCapabilities(HostNetCapabilities hnc){
		
	}
	
	public void printHostIpRouteConfig(HostIpRouteConfig cfg){
		
	}
	public void printHostDnsConfig(HostDnsConfig dns){
		
	}
	public void printHostNetworkConfig(HostNetworkConfig cfg){
		
	}

}
