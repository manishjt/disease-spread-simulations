/*
 * Copyright (C) 2016 SFINA Team
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package experiment;
import core.SimulationAgentNew;
import agent.DisasterSpreadAgentNew;
import input.Domain;
import input.SfinaParameter;
import backend.HelbingEtAlModelBackendDomainAgent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import org.apache.log4j.Logger;
import power.backend.PowerBackend;
import protopeer.Experiment;
import protopeer.Peer;
import protopeer.PeerFactory;
import protopeer.SimulatedExperiment;
import protopeer.util.quantities.Time;


/**
 *
 * @author dinesh
 */
public class TotalDamagedNodesNew extends SimulatedExperiment{
    private static final Logger logger = Logger.getLogger(TotalDamagedNodesNew.class);
    
    private static String expSeqNum="Case500Grid_AverageDamage";
    private final static String peersLogDirectory="peerlets-log/";
    private static String experimentID="experiment-"+expSeqNum;
    
    //Simulation Parameters
    private final static int bootstrapTime=2000;
    private final static int runTime=1000;
    private final static int runDuration=3;
    private final static int N=1;
    
    // TODO following needed?
    private final static String columnSeparator = ",";
    public static int nodeToInfect;
    
    public static void main(String[] args){
       nodeToInfect = 32;
        if (args.length > 0) {
            try {
                nodeToInfect = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Argument" + args[0] + " must be an integer.");
                System.exit(1);
            }
        }
        
        run();
    }
    
    public static void run() {
        Experiment.initEnvironment();
        final TotalDamagedNodesNew test = new TotalDamagedNodesNew();
        test.init();
        final File folder = new File(peersLogDirectory+experimentID);
        clearExperimentFile(folder);
        folder.mkdir();
        //createLinkAttackEvents();
        PeerFactory peerFactory=new PeerFactory() {
            public Peer createPeer(int peerIndex, Experiment experiment) {
                Peer newPeer = new Peer(peerIndex);
                newPeer.addPeerlet(new DisasterSpreadAgentNew(
                        experimentID,
                        Time.inMilliseconds(bootstrapTime),
                        Time.inMilliseconds(runTime), nodeToInfect));
                newPeer.addPeerlet(new HelbingEtAlModelBackendDomainAgent(
                        experimentID,
                        Time.inMilliseconds(bootstrapTime),
                        Time.inMilliseconds(runTime)));
                return newPeer;
            }
        };
        test.initPeers(0,N,peerFactory);
        test.startPeers(0,N);
        //run the simulation
        test.runSimulation(Time.inSeconds(runDuration));
    }

    
    public final static void clearExperimentFile(File experiment){
        File[] files = experiment.listFiles();
        if(files!=null) { //some JVMs return null for empty dirs
            for(File f: files) {
                if(f.isDirectory()) {
                    clearExperimentFile(f);
                } else {
                    f.delete();
                }
            }
        }
        experiment.delete();
    }
}