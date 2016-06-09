/*
 * File: PerformanceMetrics.java
 * Author: Kannan Balasubramanian
 * Creation Date: 21-03-2013
 * Last Updated Date: 26-03-2013
 */

package com.vmware.viclient.managedentities;


import java.util.HashMap;
import java.util.Vector;
import java.util.List;

import com.vmware.viclient.connectionmgr.ConnectionManager;

import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.ElementDescription;

import com.vmware.vim25.PropertySpec;
import com.vmware.vim25.ObjectSpec;
import com.vmware.vim25.PropertyFilterSpec;
import com.vmware.vim25.RetrieveOptions;
import com.vmware.vim25.RetrieveResult;
import com.vmware.vim25.ObjectContent;
import com.vmware.vim25.VimPortType;
import com.vmware.vim25.ManagedObjectReference;
import com.vmware.vim25.DynamicProperty;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerformanceDescription;
import com.vmware.vim25.PerfCompositeMetric;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfFormat;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerfEntityMetric;
import com.vmware.vim25.PerfMetricSeries;
import com.vmware.vim25.PerfMetricIntSeries;
import com.vmware.vim25.PerfMetricSeriesCSV;
import com.vmware.vim25.PerfSampleInfo;
import com.vmware.vim25.ServiceContent;

import com.vmware.viclient.helper.TableData;
import com.vmware.viclient.helper.Utilities;
import com.vmware.viclient.helper.VimUtil;

public class PerformanceMetrics {

        ManagedObjectReference perfMgr = null;
	private ServiceContent inst = null;
	private VimUtil vimUtil = null;
        VimPortType vimPort = null;

	public PerformanceMetrics () {
		ConnectionManager cmgr = ConnectionManager.getInstance();
		inst=cmgr.getServiceContent();
                vimPort = cmgr.getVimPort();
                perfMgr = inst.getPerfManager();
	}

        public void printPerfInfo() {
           //System.out.println("Perf Desc: " + perfMgr.getDescription()); //commentedK
           PerfCounterInfo [] cInfo = (PerfCounterInfo[])vimUtil.getProperty(perfMgr, "perfCounter");
           int index = 1;
           for (PerfCounterInfo c : cInfo) {
                System.out.println("Index-" + index++ + " Key: " + c.getKey() + " , Level: " + c.getLevel() + " , PerDeviceLevel: " + c.getPerDeviceLevel());
                printElementDescription("GroupInfo", c.getGroupInfo());
                printElementDescription("NameInfo", c.getNameInfo());
                System.out.println("RollType: " + c.getRollupType());
                System.out.println("StatsType: " + c.getStatsType());
                List<Integer> counterIds = c.getAssociatedCounterId();
                System.out.print("Counter ids - ");
                if (counterIds != null)
                for (int id : counterIds) {
                     System.out.print(id + ":");
                }
                System.out.println();
           }
        }

        public TableData getPerfMetrics() {
               TableData kdata = new TableData();
               kdata.colNames.addElement("S.No");
               kdata.colNames.addElement("Key");
               kdata.colNames.addElement("CounterId");
               kdata.colNames.addElement("GroupInfo");
               kdata.colNames.addElement("NameInfo");
               kdata.colNames.addElement("UnitInfo");
               kdata.colNames.addElement("RollType");
               kdata.colNames.addElement("StatType");
               kdata.colNames.addElement("Level");
               kdata.colNames.addElement("PerDeviceLevel");
               kdata.colNames.addElement("Value");

               PerfCounterInfo [] cInfo = (PerfCounterInfo[])vimUtil.getProperty(perfMgr, "perfCounter");
               int index = 1;
               Vector v = null;
               for (PerfCounterInfo c : cInfo) {
                    v = new Vector();
                    v.addElement(index++);
                    v.addElement(c.getKey());
                    List<Integer> counterIds = c.getAssociatedCounterId();
                    if (counterIds != null) {
                        String tmp = "";
                        for (int id : counterIds) {
                            tmp = tmp + (id + ":");
                        }
                        v.addElement(tmp);
                    } else {
                        v.addElement("");
                    }
                    v.addElement(getElementDesc(c.getGroupInfo()));
                    v.addElement(getElementDesc(c.getNameInfo()));
                    v.addElement(getElementDesc(c.getUnitInfo()));
                    v.addElement(c.getRollupType());
                    v.addElement(c.getStatsType());
                    v.addElement(c.getLevel());
                    v.addElement(c.getPerDeviceLevel());
                    v.addElement("");

                    kdata.rowData.addElement(v);
               }

	       try {
                    PropertySpec[] pSpec = new PropertySpec[2];
		    pSpec[0] = new PropertySpec();
                    pSpec[0].setType("HostSystem");
                    //pSpec[0].setType("PerformanceManager");
                    //pSpec[0].setPathSet(new String []{"perfCounter"});
                    pSpec[0].setAll(new Boolean(true));
		    pSpec[1] = new PropertySpec();
                    pSpec[1].setType("VirtualMachine");
                    pSpec[1].setAll(new Boolean(true));

		    ManagedObjectReference ref = ConnectionManager.getInstance().getManagedEntity();

                    ObjectSpec[] obSpec = new ObjectSpec[1];
		    obSpec[0] = new ObjectSpec();
                    //obSpec[0].setObj(inst.getServiceContent().getPerfManager());
                    obSpec[0].setObj(ref);
                    //obSpec[0].setObj(inst.getServiceContent().getRootFolder());
                    //obSpec[0].setObj(perfMgr.getMOR());
                    //obSpec[0].setSkip(new Boolean(false));
                    obSpec[0].setSkip(new Boolean(true));

		    System.out.println("perf mgr MOR: " + inst.getPerfManager());
                    PropertyFilterSpec pFSpec = new PropertyFilterSpec();
                    pFSpec.getPropSet().add(pSpec[0]);
                    pFSpec.getPropSet().add(pSpec[1]);
                    pFSpec.getObjectSet().add(obSpec[0]);

		    /*PropertyCollector collect = inst.getPropertyCollector();
		    System.out.println("Prop collector: " + collect);
		    RetrieveOptions options = new RetrieveOptions();
		    //options.setMaxObjects(5);
		    RetrieveResult results = collect.retrievePropertiesEx(new PropertyFilterSpec[]{pFSpec}, options);
		    ObjectContent [] objs = results.getObjects(); 
		    System.out.println("Metrics: token: " + results.getToken()); */

		    //System.out.println("Perf for Entity: " + ConnectionManager.getInstance().getManagedEntity()); //commentedK
		    List<PropertyFilterSpec> pfList = new Vector<PropertyFilterSpec>();
		    pfList.add(pFSpec);
                    List<ObjectContent> objs = vimPort.retrieveProperties(inst.getPropertyCollector(), pfList);

		    System.out.println("ObjectContent : " + objs);
		    if (objs != null) {
		        //printMetrics(ConnectionManager.getInstance().getManagedEntity(), null);
			for (ObjectContent obj : objs) {
		             System.out.println("Metrics: MOR: " + obj.getObj());
			     List<DynamicProperty> props = obj.getPropSet();
			     Utilities.printDynamicProperties(props, 7);
			}
		    }

	       } catch (Exception ex) {
                    ex.printStackTrace();
	       }

               return kdata;
        }
 
	public void printMetrics(ManagedObjectReference entity, DynamicProperty prop) {
               //System.out.println("Entity: " + entity + " - Props: " + prop.getName() + " - " + prop.getVal());
	       try {
	            PerfProviderSummary summary = vimPort.queryPerfProviderSummary(perfMgr, entity);
	            if (summary != null) {
		        //System.out.println("Perf Summary: " + summary.isCurrentSupported() + " : " + summary.isSummarySupported() + " : " + summary.getRefreshRate());
	            }
		    List<PerfMetricId> ids = vimPort.queryAvailablePerfMetric(perfMgr, entity, null, null, 20);
	            if (ids != null) {
			 int count  = 0;
			 //for (PerfMetricId id : ids) {
                              //System.out.println(++count + ": PerfMetricId : " + id.getCounterId() + " : " + id.getInstance());
			 //}
			 PerfQuerySpec spec = new PerfQuerySpec();
			 spec.setEntity(entity);
			 spec.setIntervalId(20);
			 spec.setFormat("normal");
			 spec.setMaxSample(1);
			 for (PerfMetricId id : ids) {
			      spec.getMetricId().add(id);
			 }
			 Vector<PerfQuerySpec> pfList = new Vector();
			 pfList.add(spec);
			 List<PerfEntityMetricBase> mBase = vimPort.queryPerf(perfMgr, pfList);
			 if (mBase != null) {
				 for (PerfEntityMetricBase base : mBase) {
			              if(base instanceof PerfEntityMetric) {
                                         List<PerfSampleInfo> info = ((PerfEntityMetric)base).getSampleInfo();
                                         List<PerfMetricSeries> series = ((PerfEntityMetric)base).getValue();
					 if (series != null) {
						 count = 0;
                                             for (PerfMetricSeries s : series) {
                                                  if (s instanceof PerfMetricIntSeries) {
						        List<Long> v = ((PerfMetricIntSeries)s).getValue();
							if (v != null) {
								count++;
							   for (int i=0; i<v.size(); i++) {
							        System.out.println(count + " - " + ids.get(i).getCounterId() + " : " + ids.get(i).getInstance() + " : " +  v.get(i));
							   }
							}

						  } else if (s instanceof PerfMetricSeriesCSV) {
						        String v = ((PerfMetricSeriesCSV)s).getValue();
							System.out.println(count + " - value : " + v);

						  }
					     }
					 }
				      }
				 }
			 }

		    } else {
                        System.out.println("PerfMetricId is null...");
		    }

	       } catch (Exception e) {
		       e.printStackTrace();
	       }
	}

        public void printElementDescription(String element, ElementDescription e) {
           System.out.println(element + " - Key: " + e.getKey() + " , Label: " + e.getLabel() + " , Summary: " + e.getSummary());
        }

        public String getElementDesc(ElementDescription e) {
           return (e.getKey() + " : " + e.getLabel() + " : " + e.getSummary());
        }
}
