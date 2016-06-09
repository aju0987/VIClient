/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vmware.viclient.ui.fx;

import java.util.Vector;

public class Props {

	public static Host entity = null;

	public Props() {
              entity = new Host("Esxi");
	      entity.init();
	}

	public String[] navigateHostProperties() {
	      String [] props = entity.getProperties();
	      for (String prop : props) {
                   //System.err.println(prop);
	      }
              VirtualMachine [] machines = entity.getVMs();
	      for (VirtualMachine m : machines) {
		   navigateVMProperties(m);
	      }
	      return props;
	}

	public String[] navigateVMProperties(VirtualMachine vm) {
	      String [] props = vm.getProperties();
	      for (String prop : props) {
                   //System.err.println(prop);
	      }
              Network [] networks = vm.getNetworks();
	      for (Network n : networks) {
		   navigateNetworkProperties(n);
	      }
	      return props;
	}

	public String[] navigateNetworkProperties(Network n) {
	      String [] props = n.getProperties();
	      for (String prop : props) {
                   //System.err.println(prop);
	      }
              Host [] hosts = n.getHosts();
	      for (Host h : hosts) {
                   //System.err.println(h.getTitle());
	      }
	      return props;
	}

	public static void main (String [] args) {
            new Props().navigateHostProperties();
	}
}

abstract class ManagedEntity {
        protected String title = null;
        protected String [] props;

	public String getTitle() {
           return title;
	}

	public String [] getProperties() {
           return props;
	}
}

class Host extends ManagedEntity {
       private Vector<VirtualMachine> v = new Vector<VirtualMachine>();

       public Host (String t) {
            title = "Host-" + t;
       }

       public void init() {
	    props = new String[40];
	    for (int i=0; i<props.length; i++) {
                 props[i] = new String("HostProp: " + i);
	    }
	    for (int i=0; i<3; i++) {
                 v.add(new VirtualMachine(i));
	    }
       }

       public VirtualMachine [] getVMs() {
	      int size = v.size();
	      VirtualMachine [] ms = new VirtualMachine[size];
	      for (int i=0; i<size; i++) {
                   ms[i] = v.elementAt(i);
	      }
	      return ms;
       }
}

class VirtualMachine extends ManagedEntity {
       private Vector<Network> v = new Vector<Network>();

       public VirtualMachine(int i) {
            title = "VM-" + i;
	    props = new String[5];
	    for (int j=0; j<props.length; j++) {
                 props[j] = new String("VMProp: " + j);
	    }
	    for (int j=0; j<3; j++) {
                 v.add(new Network(j));
	    }
       }

       public Network [] getNetworks() {
	    int size = v.size();
	    Network [] ns = new Network[size];
	    for (int i=0; i<size; i++) {
                 ns[i] = v.elementAt(i);
	    }
	    return ns;
       }
}

class Network extends ManagedEntity{
	public Vector<Host> v = new Vector<Host>();

	public Network(int i) {
            title = "Network-" + i;
	    props = new String[5];
	    for (int j=0; j<props.length; j++) {
                 props[j] = new String("Network: " + j);
	    }
	    v.add(Props.entity);
	}

	public Host [] getHosts() {
	    int size = v.size();
	    Host [] hs = new Host[size];
	    for (int i=0; i<size; i++) {
                 hs[i] = v.elementAt(i);
	    }
	    return hs;
	}
}
